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
        env = env.extendWithFunction(Symbols.APPLY, new apply());
        env = env.extendWithOperator(Symbols.BIND, new bind());
        env = env.extendWithFunction(Symbols.CONS, new cons());
        env = env.extendWithOperator(Symbols.FUNCTION, new FunctionOperator());
        env = env.extendWithFunction(Symbols.EVAL, this);
        env = env.extendWithFunction(Symbols.HEAD, new head());
        env = env.extendWithValue(Symbols.NIL, Nothing.AT_ALL);
        env = env.extendWithOperator(Symbols.QUOTE, new quote());
        env = env.extendWithFunction(Symbols.TAIL, new tail());
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

    public Function makeFunction(Tree definition, Environment environment) {
        if (definition.getTail() == Nothing.AT_ALL) {
            Object nameOrFunction = definition.getHead();
            if (nameOrFunction instanceof Function) {
                return (Function) nameOrFunction;
            }
            Object value = environment.bindings.get(nameOrFunction);
            if (value instanceof Function) {
                return (Function) value;
            } else {
                throw new IllegalArgumentException("Not a function: " + nameOrFunction); //TODO
            }
        } else {
            Object argumentsList = definition.getHead();
            if (argumentsList instanceof Tree) {
                Tree body = definition.getTail();
                if (((Tree) argumentsList).getHead() == Nothing.AT_ALL) {
                    //TODO check tail is empty too
                    return new InterpretedFunction0(body, environment, this);
                } else if (((Tree) argumentsList).getTail() == Nothing.AT_ALL) {
                    return new InterpretedFunction1(body, environment, this, (Symbol) ((Tree) argumentsList).getHead()); //TODO check
                } else { //TODO other cases
                    return new InterpretedFunctionN(body, environment, this, (Cons) argumentsList); //TODO check
                }
            } else {
                throw new IllegalArgumentException("Not an arguments list: " + argumentsList); //TODO
            }
        }
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

    public class FunctionOperator extends Operator {

        @Override
        public Object apply(Tree form, Environment environment) {
            Tree definition = form.getTail();
            return makeFunction(definition, environment);
        }
    }

    public static class InterpretedFunction extends Closure {

        public final Object body;
        public final Function evaluator;

        public InterpretedFunction(Tree body, Environment environment, Function evaluator) {
            super(environment);
            this.body = new Cons(Symbols.BIND, new Cons(Nothing.AT_ALL, body));
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
            return evaluator.apply(body, enclosingEnvironment.extendWithValue(argumentName, argument));
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
                environment = environment.extendWithValue((Symbol) arg.getHead(), arguments[i]);
                arg = arg.getTail();
            }
            return evaluator.apply(body, environment);
        }
    }

    public class bind extends Operator {
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
                    Cons theBinding = (Cons) binding;
                    Object head = theBinding.head;
                    if(!(head instanceof Symbol)) {
                        throw new IllegalArgumentException("Invalid binding: " + binding); //TODO
                    }
                    if(head == Symbols.FUNCTION) {
                        Object name = theBinding.tail.getHead();
                        if(!(name instanceof Symbol)) {
                            throw new IllegalArgumentException("Not a symbol: " + name); //TODO
                        }
                        Tree definition = theBinding.tail.getTail();
                        Function function = makeFunction(definition, environment);
                        environment = environment.extendWithFunction((Symbol) name, function);
                    } else if(head == Symbols.MACRO) {
                        Object name = theBinding.tail.getHead();
                        if(!(name instanceof Symbol)) {
                            throw new IllegalArgumentException("Not a symbol: " + name); //TODO
                        }
                        Tree definition = theBinding.tail.getTail();
                        Function function = makeFunction(definition, environment);
                        //TODO who does the destructuring?
                        environment = environment.extendWithOperator((Symbol) name, new Macro(function));
                    } else {
                        Object value = eval(theBinding.tail.getHead(), original);
                        environment = environment.extendWithValue((Symbol) head, value);
                    }
                } else {
                    throw new IllegalArgumentException("Invalid binding: " + binding); //TODO
                }
                bindingsTree = bindingsTree.getTail();
            }
            Object result = Nothing.AT_ALL;
            Tree body = form.getTail().getTail();
            while (body != Nothing.AT_ALL) {
                result = eval(body.getHead(), environment);
                body = body.getTail();
            }
            return result;
        }
    }
}
