package treep;

import treep.ast.Node;
import treep.eval.Environment;

public abstract class Operator extends Object {

    public abstract Object apply(Node form, Environment environment);

    public static void checkRequiredNumberOfArguments(Node form, int requiredNumberOfArguments) {
        int numberOfArguments = form.children.size();
        if(numberOfArguments != requiredNumberOfArguments) {
            throw new IllegalArgumentException("Invalid number of arguments: " + numberOfArguments); //TODO
        }
    }
}
