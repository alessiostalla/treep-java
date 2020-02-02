package treep.eval;

import org.pcollections.PSequence;
import treep.Function;
import treep.Macro;
import treep.Object;
import treep.Operator;
import treep.ast.Node;
import treep.ast.Nothing;
import treep.ast.Tree;
import treep.base.QuoteOperator;
import treep.symbols.NameSpace;
import treep.symbols.Symbol;

public class SimpleEvaluator {

    public final Environment initialEnvironment;
    public static final NameSpace NAMESPACE_TREEP = new NameSpace();

    public static final Symbol SYMBOL_FUNCTION = NAMESPACE_TREEP.intern("function");
    public static final Symbol SYMBOL_QUOTE = NAMESPACE_TREEP.intern("quote");
    public static final Symbol SYMBOL_SEQUENTIALLY = NAMESPACE_TREEP.intern("sequentially");

    {
        Environment environment = Environment.empty();
        environment = environment.extend(SYMBOL_FUNCTION, new FunctionOperator());
        environment = environment.extend(SYMBOL_QUOTE, new QuoteOperator());
        environment = environment.extend(SYMBOL_SEQUENTIALLY, new SequentiallyOperator());
        initialEnvironment = environment;
    }

    public Object eval(Object expression) {
        return eval(expression, initialEnvironment);
    }

    public Object eval(Object expression, Environment environment) {
        if(expression instanceof Symbol) {
            return environment.bindings.computeIfAbsent((Symbol) expression, symbol -> {
                throw new RuntimeException("Unbound: " + symbol); //TODO specific exception
            });
        } else if(expression instanceof Node) {
            Node node = (Node) expression;
            Object head = node.head;
            if(head instanceof Symbol) {
                //TODO document this (Lisp-1)
                Object operator = environment.bindings.get(head);
                //TODO maybe optimize for 1, 2, 3 arguments
                if(operator instanceof Function) {
                    Object[] arguments = node.children.stream().map(o -> eval(o, environment)).toArray(Object[]::new);
                    return ((Function) operator).apply(arguments);
                } else if(operator instanceof Macro) {
                    return eval(((Operator) operator).apply(node, environment), environment);
                } else if(operator instanceof Operator) {
                    return ((Operator) operator).apply(node, environment);
                } else {
                    return new SignalNoSuchOperatorError().apply(head);
                }
            } else {
                return new SignalInvalidExpression().apply(expression);
            }
        } else {
            return expression;
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

    public class SequentiallyOperator extends Operator {
        @Override
        public Object apply(Node form, Environment environment) {
            Object result = Nothing.AT_ALL;
            for(Object expression : form.children) {
                result = eval(expression, environment);
            }
            return result;
        }
    }

    public class FunctionOperator extends Operator {

        @Override
        public Object apply(Node form, Environment environment) {
            if(form.children.size() == 1) {
                Object nameOrFunction = form.children.get(0);
                if(nameOrFunction instanceof Function) {
                    return nameOrFunction;
                }
                Object value = environment.bindings.get(nameOrFunction);
                if(value instanceof Function) {
                    return value;
                } else {
                    throw new IllegalArgumentException("Not a function: " + nameOrFunction); //TODO
                }
            } else if(form.children.size() > 1) {
                Object argumentsList = form.children.get(0);
                if(argumentsList instanceof Tree) {
                    PSequence<Object> body = form.children.subList(1, form.children.size());
                    if(((Tree) argumentsList).getHead() == Nothing.AT_ALL) {
                        return new InterpretedFunction0(body, environment);
                    } else if(((Tree) argumentsList).getChildren().size() == 0) {
                        return new InterpretedFunction1(body, environment, (Symbol) ((Tree) argumentsList).getHead()); //TODO check
                    } else { //TODO other cases
                        return new InterpretedFunctionN(body, environment, (PSequence) ((Tree) argumentsList).getChildren().plus(0, ((Tree) argumentsList).getHead())); //TODO check
                    }
                } else {
                    throw new IllegalArgumentException("Not an arguments list: " + argumentsList); //TODO
                }
            }
            throw new IllegalArgumentException("Not a function: " + form); //TODO
        }
    }

    public static class InterpretedFunction extends Function {

        public final Object body;
        public final Environment enclosingEnvironment;

        public InterpretedFunction(PSequence<Object> body, Environment enclosingEnvironment) {
            this.body = new Node(SYMBOL_SEQUENTIALLY, body);
            this.enclosingEnvironment = enclosingEnvironment;
        }
    }

    public class InterpretedFunction0 extends InterpretedFunction {
        public InterpretedFunction0(PSequence<Object> body, Environment enclosingEnvironment) {
            super(body, enclosingEnvironment);
        }

        @Override
        public Object apply() {
            return eval(body, enclosingEnvironment);
        }
    }

    public class InterpretedFunction1 extends InterpretedFunction {

        public final Symbol argumentName;

        public InterpretedFunction1(PSequence<Object> body, Environment enclosingEnvironment, Symbol argumentName) {
            super(body, enclosingEnvironment);
            this.argumentName = argumentName;
        }

        @Override
        public Object apply(Object argument) {
            return eval(body, enclosingEnvironment.extend(argumentName, argument));
        }
    }

    public class InterpretedFunctionN extends InterpretedFunction {

        public final PSequence<Symbol> argumentNames;

        public InterpretedFunctionN(PSequence<Object> body, Environment enclosingEnvironment, PSequence<Symbol> argumentNames) {
            super(body, enclosingEnvironment);
            this.argumentNames = argumentNames;
        }

        @Override
        public Object apply(Object... arguments) {
            if(argumentNames.size() != arguments.length) { //TODO
                throw new IllegalArgumentException("Invalid arguments"); //TODO
            }
            Environment environment = enclosingEnvironment;
            int i = 0;
            for(Symbol symbol : argumentNames) {
                environment = environment.extend(symbol, arguments[i++]);
            }
            return eval(body, environment);
        }
    }

}
