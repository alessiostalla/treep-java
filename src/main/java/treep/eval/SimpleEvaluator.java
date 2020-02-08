package treep.eval;

import treep.builtin.datatypes.Function;
import treep.builtin.datatypes.Macro;
import treep.Object;
import treep.builtin.datatypes.Operator;
import treep.builtin.datatypes.tree.Cons;
import treep.builtin.datatypes.tree.Nothing;
import treep.builtin.datatypes.tree.Tree;
import treep.builtin.operators.Quote;
import treep.builtin.datatypes.symbol.NameSpace;
import treep.builtin.datatypes.symbol.Symbol;

import java.util.ArrayList;
import java.util.List;

public class SimpleEvaluator {

    public final Environment initialEnvironment;
    public static final NameSpace NAMESPACE_TREEP = new NameSpace();

    public static final Symbol SYMBOL_BIND = NAMESPACE_TREEP.intern("bind");
    public static final Symbol SYMBOL_FUNCTION = NAMESPACE_TREEP.intern("function");
    public static final Symbol SYMBOL_QUOTE = NAMESPACE_TREEP.intern("quote");
    public static final Symbol SYMBOL_SEQUENTIALLY = NAMESPACE_TREEP.intern("sequentially");
    public static final Symbol SYMBOL_THE_ENVIRONMENT = NAMESPACE_TREEP.intern("the-environment");

    {
        Environment environment = Environment.empty();
        environment = environment.extend(SYMBOL_FUNCTION, new FunctionOperator());
        environment = environment.extend(SYMBOL_QUOTE, new Quote());
        environment = environment.extend(SYMBOL_SEQUENTIALLY, new SequentiallyOperator());
        initialEnvironment = environment;
    }

    public Object eval(Object expression) {
        return eval(expression, initialEnvironment);
    }

    public Object eval(Object expression, Environment environment) {
        if(expression instanceof Symbol) {
            return eval(new Cons(expression), environment);
        } else if(expression instanceof Cons) {
            Tree tree = (Cons) expression;
            Object head = tree.getHead();
            if(head instanceof Symbol) {
                return evalApplication((Symbol) head, tree, environment);
            } else if(head instanceof Function) {
                return evalFunctionApplication((Function) head, tree, environment);
            } else if(head instanceof Operator) {
                return evalOperatorApplication((Operator) head, tree, environment);
            } else if(tree.getTail() == Nothing.AT_ALL) {
                return tree.getHead();
            } else {
                return new SignalInvalidExpression().apply(expression);
            }
        } else {
            return expression;
        }
    }

    public Object evalApplication(Symbol head, Tree tree, Environment environment) {
        //TODO document this (Lisp-1)
        Object operator = environment.bindings.get(head);
        if(operator instanceof Function) {
            return evalFunctionApplication((Function) operator, tree, environment);
        } else if(operator instanceof Operator) {
            return evalOperatorApplication((Operator) operator, tree, environment);
        } else {
            return new SignalNoSuchOperatorError().apply(head);
        }
    }

    public Object evalOperatorApplication(Operator operator, Tree expression, Environment environment) {
        Object result = operator.apply(expression, environment);
        if(operator instanceof Macro) {
            return eval(result, environment);
        } else {
            return result;
        }
    }

    public Object evalFunctionApplication(Function function, Tree expression, Environment environment) {
        //TODO optimize for 1, 2, 3 arguments
        List<Object> arguments = new ArrayList<>(2);
        while(expression.getTail() != Nothing.AT_ALL) {
            expression = expression.getTail();
            arguments.add(eval(expression.getHead(), environment));
        }
        return function.apply(arguments.toArray(new Object[0]));
    }

    public static class SignalNoSuchOperatorError extends Function {

        @Override
        public Object apply(Object argument) {
            throw new RuntimeException("Undefined operator: " + argument);
        }

        @Override
        public Object apply() {
            throw new RuntimeException("Undefined operator"); //TODO
        }
    }

    public static class SignalInvalidExpression extends Function {

        @Override
        public Object apply(Object argument) {
            throw new RuntimeException("Invalid expression: " + argument); //TODO
        }

    }

    public class ApplyFunction extends Closure {
        public ApplyFunction(Environment enclosingEnvironment) {
            super(enclosingEnvironment);
        }

        @Override
        public Object apply(Object... arguments) {
            if(arguments.length == 0) {
                return super.apply();
            } else if(!(arguments[0] instanceof Function)) {
                throw new IllegalArgumentException("Not a function: " + arguments[0]); //TODO
            } else {
                //TODO optimize for 1, 2, 3 arguments
                return ((Function) arguments[0]).apply(arguments);
            }
        }
    }


    public class SequentiallyOperator extends Operator {
        @Override
        public Object apply(Tree form, Environment environment) {
            Object result = Nothing.AT_ALL;
            while (form.getTail() != Nothing.AT_ALL) {
                form = form.getTail();
                result = eval(form.getHead(), environment);
            }
            return result;
        }
    }

    public class FunctionOperator extends Operator {

        @Override
        public Object apply(Tree form, Environment environment) {
            if(form.getTail().getTail() == Nothing.AT_ALL) {
                Object nameOrFunction = form.getTail().getHead();
                if(nameOrFunction instanceof Function) {
                    return nameOrFunction;
                }
                Object value = environment.bindings.get(nameOrFunction);
                if(value instanceof Function) {
                    return value;
                } else {
                    throw new IllegalArgumentException("Not a function: " + nameOrFunction); //TODO
                }
            } else {
                Object argumentsList = form.getTail().getHead();
                if(argumentsList instanceof Tree) {
                    Tree body = form.getTail().getTail();
                    if(((Tree) argumentsList).getHead() == Nothing.AT_ALL) {
                        //TODO check tail is empty too
                        return new InterpretedFunction0(body, environment);
                    } else if(((Tree) argumentsList).getTail() == Nothing.AT_ALL) {
                        return new InterpretedFunction1(body, environment, (Symbol) ((Tree) argumentsList).getHead()); //TODO check
                    } else { //TODO other cases
                        return new InterpretedFunctionN(body, environment, (Cons) argumentsList); //TODO check
                    }
                } else {
                    throw new IllegalArgumentException("Not an arguments list: " + argumentsList); //TODO
                }
            }
        }
    }

    public static class Closure extends Function {
        public final Environment enclosingEnvironment;

        public Closure(Environment enclosingEnvironment) {
            this.enclosingEnvironment = enclosingEnvironment;
        }
    }

    public static class InterpretedFunction extends Closure {

        public final Object body;

        public InterpretedFunction(Tree body, Environment enclosingEnvironment) {
            super(enclosingEnvironment);
            this.body = new Cons(SYMBOL_SEQUENTIALLY, body);
        }
    }

    public class InterpretedFunction0 extends InterpretedFunction {
        public InterpretedFunction0(Tree body, Environment enclosingEnvironment) {
            super(body, enclosingEnvironment);
        }

        @Override
        public Object apply() {
            return eval(body, enclosingEnvironment);
        }
    }

    public class InterpretedFunction1 extends InterpretedFunction {

        public final Symbol argumentName;

        public InterpretedFunction1(Tree body, Environment enclosingEnvironment, Symbol argumentName) {
            super(body, enclosingEnvironment);
            this.argumentName = argumentName;
        }

        @Override
        public Object apply(Object argument) {
            return eval(body, enclosingEnvironment.extend(argumentName, argument));
        }
    }

    public class InterpretedFunctionN extends InterpretedFunction {

        public final Cons argumentNames;

        public InterpretedFunctionN(Tree body, Environment enclosingEnvironment, Cons argumentNames) {
            super(body, enclosingEnvironment);
            this.argumentNames = argumentNames;  //TODO check they are all symbols
        }

        @Override
        public Object apply(Object... arguments) {
            if(argumentNames.tailSize() + 1 != arguments.length) { //TODO
                throw new IllegalArgumentException("Invalid arguments"); //TODO
            }
            Environment environment = enclosingEnvironment;
            Tree arg = argumentNames;
            for(int i = 0; i < arguments.length; i++) {
                environment = environment.extend((Symbol) arg.getHead(), arguments[i]);
                arg = arg.getTail();
            }
            return eval(body, environment);
        }
    }

}
