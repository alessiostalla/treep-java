package treep.language.eval;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import treep.language.Symbols;
import treep.language.datatypes.*;
import treep.language.Object;
import treep.language.datatypes.tree.Cons;
import treep.language.datatypes.tree.Nothing;
import treep.language.datatypes.tree.Tree;
import treep.language.functions.*;
import treep.language.operators.quote;
import treep.language.datatypes.symbol.Symbol;
import treep.language.read.ASTBuilder;
import treep.language.read.SimpleDatumParser;
import treep.language.read.TreepLexer;
import treep.language.read.TreepParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.lang.String;

public class BootstrapEvaluator extends Function {

    public Variable globalEnvironment = new Variable(Environment.empty()) {
        @Override
        public Environment apply(Object argument) {
            if(!(argument instanceof Environment)) {
                throw new IllegalArgumentException("Attempting to replace the global environment with " + argument + " which is not an environment"); //TODO
            }
            return (Environment) super.apply(argument);
        }
    };
    {
        Environment env = getGlobalEnvironment();
        env = env.extendWithFunction(Symbols.APPEND, new append());
        env = env.extendWithFunction(Symbols.APPLY, new apply());
        env = env.extendWithFunction(Symbols.CONS, new cons());
        env = env.extendWithFunction(Symbols.CONS_HEAD, new head());
        env = env.extendWithFunction(Symbols.CONS_TAIL, new tail());
        env = env.extendWithFunction(Symbols.CONSTANT, new constant());
        env = env.extendWithOperator(Symbols.DO, new doOperator());
        env = env.extendWithFunction(Symbols.ENVIRONMENT_EXTEND, new environment_extend());
        env = env.extendWithVariable(Symbols.ENVIRONMENT_GLOBAL, globalEnvironment);
        //Note: ENVIRONMENT_LOCAL is bound by the environment itself
        env = env.extendWithFunction(Symbols.EVAL, this);
        env = env.extendWithFunction(Symbols.EQ, new eq());
        env = env.extendWithFunction(Symbols.ERROR, new error());
        env = env.extendWithOperator(Symbols.FUNCTION, new function());
        env = env.extendWithOperator(Symbols.IF, new ifOperator());
        env = env.extendWithOperator(Symbols.LOOP, new loop());
        env = env.extendWithConstant(Symbols.LOWERCASE_T, Symbols.T);
        env = env.extendWithOperator(Symbols.MACRO, new macro());
        env = env.extendWithFunction(Symbols.MACRO_EXPAND, new macro_expand());
        env = env.extendWithConstant(Symbols.NIL, Nothing.AT_ALL);
        env = env.extendWithOperator(Symbols.RETURN, new returnOperator());
        env = env.extendWithFunction(Symbols.QUIT, new quit());
        env = env.extendWithOperator(Symbols.QUOTE, new quote());
        env = env.extendWithOperator(Symbols.SET, new set());
        env = env.extendWithConstant(Symbols.T, Symbols.T);
        env = env.extendWithOperator(Symbols.TEMPLATE, new Macro(new template()));
        env = env.extendWithFunction(Symbols.VARIABLE, new variable());
        env = env.extendWithOperator(Symbols.WITH_ENVIRONMENT, new with_environment());
        globalEnvironment.apply(env);
    }

    public BootstrapEvaluator() {
        super(new Cons(Nothing.AT_ALL, new Cons(Nothing.AT_ALL))); //TODO arg names? Optional args?
    }

    public Environment getGlobalEnvironment() {
        return (Environment) globalEnvironment.apply();
    }

    public Object apply(Object expression) {
        return eval(expression, getGlobalEnvironment());
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
        if (operator instanceof Macro) { //TODO if result == expression, what happens?
            return eval(result, environment);
        } else {
            return result;
        }
    }

    public Object evalFunctionApplication(Function function, Tree expression, Environment environment) {
        //TODO optimize for 1, 2, 3 arguments
        List<Object> arguments = new ArrayList<>(2);
        Tree args = expression.getTail();
        while (args != Nothing.AT_ALL) {
            arguments.add(eval(args.getHead(), environment));
            args = args.getTail();
        }
        return function.apply(arguments.toArray(new Object[0]));
    }

    protected final Function evalBody = new Function(Nothing.AT_ALL) { //TODO
        @Override
        public Object apply(Object body, Object environment) {
            return evalBody((Tree) body, (Environment) environment);
        }
    };

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
            Object arglist = definition.getHead();
            if (arglist instanceof Tree) {
                Tree body = definition.getTail();
                Tree args = (Tree) arglist;
                if (args.getHead() == Nothing.AT_ALL) {
                    //TODO check tail is empty too
                    return new InterpretedFunction0(args, body, environment, evalBody);
                } else if (args.getTail() == Nothing.AT_ALL) {
                    return new InterpretedFunction1(args, body, environment, evalBody, (Symbol) args.getHead()); //TODO check
                } else { //TODO other cases
                    return new InterpretedFunctionN(args, body, environment, evalBody, (Cons) arglist); //TODO check
                }
            } else {
                throw new IllegalArgumentException("Not an arguments list: " + arglist); //TODO
            }
        }
    }

    public static class SignalNoSuchOperatorError extends Function {

        public SignalNoSuchOperatorError() {
            super(new Cons(Symbols.NIL));
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

    public static class SignalInvalidExpression extends Function {

        public SignalInvalidExpression() {
            super(new Cons(Symbols.NIL));
        }

        @Override
        public Object apply(Object argument) {
            throw new RuntimeException("Invalid expression: " + argument); //TODO
        }

    }

    public class function extends Operator {

        @Override
        public Object apply(Tree form, Environment environment) {
            Tree definition = form.getTail();
            return makeFunction(definition, environment);
        }
    }

    public class macro extends Operator {

        @Override
        public Object apply(Tree form, Environment environment) {
            Tree definition = form.getTail();
            return new Macro(makeFunction(definition, environment));
        }
    }

    public static class InterpretedFunction extends Closure {

        public final Object body;
        public final Function evaluator;

        public InterpretedFunction(Tree arglist, Tree body, Environment environment, Function evaluator) {
            super(arglist, environment);
            this.body = (Object) body;
            this.evaluator = evaluator;
        }
    }

    public static class InterpretedFunction0 extends InterpretedFunction {
        public InterpretedFunction0(Tree arglist, Tree body, Environment environment, Function evaluator) {
            super(arglist, body, environment, evaluator);
        }

        @Override
        public Object apply() {
            return evaluator.apply(body, enclosingEnvironment);
        }
    }

    public static class InterpretedFunction1 extends InterpretedFunction {

        public final Symbol argumentName;

        public InterpretedFunction1(Tree arglist, Tree body, Environment environment, Function evaluator, Symbol argumentName) {
            super(arglist, body, environment, evaluator);
            this.argumentName = argumentName;
        }

        @Override
        public Object apply(Object argument) {
            return evaluator.apply(body, enclosingEnvironment.extendWithConstant(argumentName, argument));
        }
    }

    public static class InterpretedFunctionN extends InterpretedFunction {

        public final Cons argumentNames;

        public InterpretedFunctionN(Tree arglist, Tree body, Environment environment, Function evaluator, Cons argumentNames) {
            super(arglist, body, environment, evaluator);
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
                environment = environment.extendWithConstant((Symbol) arg.getHead(), arguments[i]);
                arg = arg.getTail();
            }
            return evaluator.apply(body, environment);
        }
    }

    protected Object evalBody(Tree body, Environment environment) {
        Object result = Nothing.AT_ALL;
        while (body != Nothing.AT_ALL) {
            result = eval(body.getHead(), environment);
            body = body.getTail();
        }
        return result;
    }

    public class with_environment extends Operator {

        @Override
        public Object apply(Tree form, Environment environment) {
            Object newEnvForm = form.getTail().getHead();
            Object newEnv = eval(newEnvForm, environment);
            if(!(newEnv instanceof Environment)) {
                throw new IllegalArgumentException("Not an environment: " + environment); //TODO
            }
            Tree body = form.getTail().getTail();
            return evalBody(body, (Environment) newEnv);
        }
    }

    public class ifOperator extends Operator {

        @Override
        public Object apply(Tree form, Environment environment) {
            if(form.tailSize() > 3) {
                throw new IllegalArgumentException("Invalid if form: " + form); //TODO
            }
            Object condition = form.getTail().getHead();
            Object trueForm = form.getTail().getTail().getHead();
            Object falseForm = form.getTail().getTail().getTail().getHead();
            Object test = eval(condition, environment);
            if(test == Nothing.AT_ALL) {
                return eval(falseForm, environment);
            } else {
                return eval(trueForm, environment);
            }
        }
    }

    public class returnOperator extends Operator {

        @Override
        public Object apply(Tree form, Environment environment) {
            if(form.tailSize() > 2) {
                throw new IllegalArgumentException("Invalid return form: " + form); //TODO
            }
            //TODO support return-from
            Object value = form.getTail().getHead();
            throw new returnFromBlock(Nothing.AT_ALL, eval(value, environment));
        }
    }

    public class doOperator extends Operator {
        @Override
        public Object apply(Tree form, Environment environment) {
            return evalBody(form.getTail(), environment);
        }
    }

    public class loop extends Operator {

        @Override
        public Object apply(Tree form, Environment environment) {
            //TODO allow naming a loop?
            Symbol loopName = Symbols.LOOP;
            Environment loopEnvironment = environment.withName(loopName);
            while(true) try {
                evalBody(form.getTail(), loopEnvironment);
            } catch (returnFromBlock r) {
                if(r.blockName == Nothing.AT_ALL || r.blockName == loopName) {
                    return r.value;
                } else {
                    throw r;
                }
            }
        }
    }

    public static class returnFromBlock extends RuntimeException {
        public final Object blockName;
        public final Object value;

        public returnFromBlock(Object blockName, Object value) {
            this.blockName = blockName;
            this.value = value;
        }
    }

    public class set extends Operator {

        @Override
        public Object apply(Tree form, Environment environment) {
            int numberOfArguments = form.tailSize();
            if(numberOfArguments < 2 || numberOfArguments % 2 != 0) {
                throw new IllegalArgumentException("Invalid number of arguments: " + numberOfArguments); //TODO
            }
            Tree args = form.getTail();
            while(args != Nothing.AT_ALL) {
                if(!(args.getHead() instanceof Symbol)) {
                    throw new IllegalArgumentException("Not a symbol: " + args.getHead()); //TODO
                }
                if(!(environment.bindings.get(args.getHead()) instanceof Variable)) {
                    throw new IllegalArgumentException("Not a variable: " + args.getHead()); //TODO
                }
                args = args.getTail().getTail();
            }

            Object lastValue = Nothing.AT_ALL;
            args = form.getTail();
            while(args != Nothing.AT_ALL) {
                Variable variable = (Variable) environment.bindings.get(args.getHead());
                lastValue = variable.apply(eval(args.getTail().getHead(), environment));
                args = args.getTail().getTail();
            }
            return lastValue;
        }

    }

    public static class error extends Function {
        protected error() {
            super(new Cons(new Cons(Nothing.AT_ALL))); //TODO
        }

        @Override
        public Object apply(Object message) {
            throw new RuntimeException(message.toString()); //TODO
        }

        @Override
        public Object apply(Object type, Object message) {
            throw new RuntimeException(message.toString()); //TODO
        }
    }

    public class macro_expand extends Function {

        protected macro_expand() {
            super(new Cons(new Cons(Nothing.AT_ALL))); //TODO
        }

        @Override
        public Object apply(Object form) {
            return apply(form, getGlobalEnvironment());
        }

        @Override
        public Object apply(Object form, Object environment) {
            if(!(environment instanceof Environment)) {
                throw new IllegalArgumentException("Not an environment: " + environment); //TODO
            }
            if(form instanceof Cons) {
                Object head = ((Cons) form).getHead();
                Object macro = ((Environment) environment).bindings.get(head);
                if(macro instanceof Macro) {
                    return ((Macro) macro).apply((Tree) form, ((Environment) environment));
                } else {
                    return form;
                }
            }
            return form;
        }
    }

    public static class quit extends Function {
        protected quit() {
            super(Nothing.AT_ALL);
        }

        @Override
        public Object apply() {
            throw new EvaluationTerminatedException();
        }
    }

    public static class EvaluationTerminatedException extends RuntimeException {}

    public static void main(String[] args) throws IOException {
        String lineSeparator = System.lineSeparator();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        BootstrapEvaluator eval = new BootstrapEvaluator();
        String input = "";
        System.out.print("treep> ");
        while (true) {
            String line = reader.readLine();
            if(line == null) {
                break;
            }
            input += line + lineSeparator;
            String trim = input.trim();
            if((line.trim().isEmpty() && !trim.isEmpty()) || trim.startsWith("(") || trim.startsWith("'(")) {
                TreepLexer lexer = new TreepLexer(CharStreams.fromString(input));
                CommonTokenStream tokens = new CommonTokenStream(lexer);
                TreepParser parser = new TreepParser(tokens);
                TreepParser.TreeContext tree = parser.topLevelTree().tree();
                if(parser.getNumberOfSyntaxErrors() == 0) {
                    try {
                        ASTBuilder astBuilder = new ASTBuilder(new SimpleDatumParser(Symbols.NAMESPACE_TREEP));
                        Object form = astBuilder.visit(tree);
                        Object result = eval.apply(form);
                        System.out.println(result);
                    } catch (EvaluationTerminatedException e) {
                        break;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    input = "";
                    System.out.print("treep> ");
                } else if(line.trim().isEmpty()) {
                    input = "";
                    System.out.print("treep> ");
                }
            }

        }
    }
}
