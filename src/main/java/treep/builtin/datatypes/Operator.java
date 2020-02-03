package treep.builtin.datatypes;

import treep.Object;
import treep.eval.Environment;
import treep.builtin.datatypes.tree.Tree;

public abstract class Operator extends Object {

    public abstract Object apply(Tree form, Environment environment);

    public static void checkRequiredNumberOfArguments(Tree form, int requiredNumberOfArguments) {
        int numberOfArguments = form.tailSize();
        if(numberOfArguments != requiredNumberOfArguments) {
            throw new IllegalArgumentException("Invalid number of arguments: " + numberOfArguments); //TODO
        }
    }
}
