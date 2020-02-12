package treep.language.eval;

import treep.language.Symbols;
import treep.language.datatypes.*;
import treep.language.Object;
import treep.language.datatypes.tree.Cons;
import treep.language.datatypes.tree.Nothing;
import treep.language.datatypes.tree.Tree;
import treep.language.functions.apply;
import treep.language.functions.head;
import treep.language.functions.tail;
import treep.language.functions.cons;
import treep.language.operators.quote;
import treep.language.datatypes.symbol.Symbol;

import java.util.ArrayList;
import java.util.List;

public class SimpleEvaluator extends Function {

    public Environment globalEnvironment = Environment.empty();
    {
        Environment env = globalEnvironment;
        env = env.extend(Symbols.SYMBOL_APPLY, new apply());
        env = env.extend(Symbols.SYMBOL_BIND, new bind(this));
        env = env.extend(Symbols.SYMBOL_CONS, new cons());
        env = env.extend(Symbols.SYMBOL_FUNCTION, new FunctionOperator(this));
        env = env.extend(Symbols.SYMBOL_EVAL, this);
        env = env.extend(Symbols.SYMBOL_HEAD, new head());
        env = env.extend(Symbols.SYMBOL_QUOTE, new quote());
        env = env.extend(Symbols.SYMBOL_TAIL, new tail());
        globalEnvironment = env;
    }

    public Object apply(Object expression) {
        return eval(expression, globalEnvironment);
    }

    public Object apply(Object expression, Object environment) {
        if (!(environment instanceof Environment)) {
            throw new IllegalArgumentException("Not an environment: " + environment); //TODO
        }
        return eval(expression, (Environment) environment);
    }

    public Object eval(Object expression, Environment environment) {
        if (expression instanceof Symbol) {
            return eval(new Cons(expression), environment);
        } else if (expression instanceof Cons) {
            Tree tree = (Cons) expression;
            Object head = tree.getHead();
            if (head instanceof Symbol) {
                return evalApplication((Symbol) head, tree, environment);
            } else if (head instanceof Function) {
                return evalFunctionApplication((Function) head, tree, environment);
            } else if (head instanceof Operator) {
                return evalOperatorApplication((Operator) head, tree, environment);
            } else if (tree.getTail() == Nothing.AT_ALL) {
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
        if (operator instanceof Function) {
            return evalFunctionApplication((Function) operator, tree, environment);
        } else if (operator instanceof Operator) {
            return evalOperatorApplication((Operator) operator, tree, environment);
        } else {
            return new SignalNoSuchOperatorError().apply(head);
        }
    }

    public Object evalOperatorApplication(Operator operator, Tree expression, Environment environment) {
        Object result = operator.apply(expression, environment);
        if (operator instanceof Macro) {
            return eval(result, environment);
        } else {
            return result;
        }
    }

    public Object evalFunctionApplication(Function function, Tree expression, Environment environment) {
        //TODO optimize for 1, 2, 3 arguments
        List<Object> arguments = new ArrayList<>(2);
        while (expression.getTail() != Nothing.AT_ALL) {
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


    public static class FunctionOperator extends Operator {

        protected final Function evalutator;

        public FunctionOperator(Function evalutator) {
            this.evalutator = evalutator;
        }

        @Override
        public Object apply(Tree form, Environment environment) {
            if (form.getTail().getTail() == Nothing.AT_ALL) {
                Object nameOrFunction = form.getTail().getHead();
                if (nameOrFunction instanceof Function) {
                    return nameOrFunction;
                }
                Object value = environment.bindings.get(nameOrFunction);
                if (value instanceof Function) {
                    return value;
                } else {
                    throw new IllegalArgumentException("Not a function: " + nameOrFunction); //TODO
                }
            } else {
                Object argumentsList = form.getTail().getHead();
                if (argumentsList instanceof Tree) {
                    Tree body = form.getTail().getTail();
                    if (((Tree) argumentsList).getHead() == Nothing.AT_ALL) {
                        //TODO check tail is empty too
                        return new InterpretedFunction0(body, environment, evalutator);
                    } else if (((Tree) argumentsList).getTail() == Nothing.AT_ALL) {
                        return new InterpretedFunction1(body, environment, evalutator, (Symbol) ((Tree) argumentsList).getHead()); //TODO check
                    } else { //TODO other cases
                        return new InterpretedFunctionN(body, environment, evalutator, (Cons) argumentsList); //TODO check
                    }
                } else {
                    throw new IllegalArgumentException("Not an arguments list: " + argumentsList); //TODO
                }
            }
        }
    }

    public static class InterpretedFunction extends Closure {

        public final Object body;
        public final Function evaluator;

        public InterpretedFunction(Tree body, Environment environment, Function evaluator) {
            super(environment);
            this.body = new Cons(Symbols.SYMBOL_BIND, new Cons(Nothing.AT_ALL, body));
            this.evaluator = evaluator;
        }
    }

    public static class InterpretedFunction0 extends InterpretedFunction {
        public InterpretedFunction0(Tree body, Environment environment, Function evaluator) {
            super(body, environment, evaluator);
        }

        @Override
        public Object apply() {
            return evaluator.apply(body, enclosingEnvironment);
        }
    }

    public static class InterpretedFunction1 extends InterpretedFunction {

        public final Symbol argumentName;

        public InterpretedFunction1(Tree body, Environment environment, Function evaluator, Symbol argumentName) {
            super(body, environment, evaluator);
            this.argumentName = argumentName;
        }

        @Override
        public Object apply(Object argument) {
            return evaluator.apply(body, enclosingEnvironment.extend(argumentName, argument));
        }
    }

    public static class InterpretedFunctionN extends InterpretedFunction {

        public final Cons argumentNames;

        public InterpretedFunctionN(Tree body, Environment environment, Function evaluator, Cons argumentNames) {
            super(body, environment, evaluator);
            this.argumentNames = argumentNames;  //TODO check they are all symbols
        }

        @Override
        public Object apply(Object... arguments) {
            if (argumentNames.tailSize() + 1 != arguments.length) { //TODO
                throw new IllegalArgumentException("Invalid arguments"); //TODO
            }
            Environment environment = enclosingEnvironment;
            Tree arg = argumentNames;
            for (int i = 0; i < arguments.length; i++) {
                environment = environment.extend((Symbol) arg.getHead(), arguments[i]);
                arg = arg.getTail();
            }
            return evaluator.apply(body, environment);
        }
    }

    public static class bind extends Operator {
        private final SimpleEvaluator evaluator;

        public bind(SimpleEvaluator evaluator) {
            this.evaluator = evaluator;
        }

        @Override
        public Object apply(Tree form, Environment environment) {
            Environment original = environment;
            Object bindings = form.getTail().getHead();
            if (!(bindings instanceof Tree)) {
                throw new IllegalArgumentException("Invalid bindings: " + bindings); //TODO
            }
            Tree bindingsTree = (Tree) bindings;
            while (bindingsTree != Nothing.AT_ALL) {
                Object binding = bindingsTree.getHead();
                if(binding instanceof Cons) {
                    Object value = evaluator.eval(((Cons) binding).tail.getHead(), original);
                    binding = new Cons(((Cons) binding).head, new Cons(value));
                }
                environment = environment.extend(binding);
                bindingsTree = bindingsTree.getTail();
            }
            Object result = Nothing.AT_ALL;
            Tree body = form.getTail().getTail();
            while (body != Nothing.AT_ALL) {
                result = evaluator.eval(body.getHead(), environment);
                body = body.getTail();
            }
            return result;
        }
    }
}
