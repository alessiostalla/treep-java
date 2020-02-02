package treep.eval;

import treep.Function;
import treep.Object;
import treep.Operator;
import treep.ast.Node;
import treep.ast.Nothing;
import treep.symbols.Symbol;

import java.util.stream.Collectors;

public class SimpleEvaluator {

    public static final Environment defaultEnvironment = Environment.empty();

    public Object eval(Object expression) {
        return eval(expression, defaultEnvironment);
    }

    public Object eval(Object expression, Environment environment) {
        if(expression instanceof Symbol) {
            return environment.bindings.getOrDefault(expression, Nothing.AT_ALL);
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
                } else if(operator instanceof Operator) {
                    return ((Operator) operator).apply(node.children.toArray(new Object[0]));
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

    public static class SignalNoSuchOperatorError extends Operator {

        @Override
        public Object apply(Object... arguments) {
            if(arguments.length == 0) {
                return apply();
            } else {
                return apply(arguments[0]);
            }
        }

        @Override
        public Object apply(Object argument) {
            throw new RuntimeException("Undefined operator: " + argument);
        }

        @Override
        public Object apply() {
            throw new RuntimeException("Undefined operator"); //TODO
        }
    }

    public static class SignalInvalidExpression extends Operator {

        @Override
        public Object apply(Object... arguments) {
            return apply(arguments[0]);
        }

        @Override
        public Object apply(Object argument) {
            throw new RuntimeException("Invalid expression: " + argument); //TODO
        }

    }

}
