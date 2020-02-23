package treep.language.datatypes;

import treep.language.Object;
import treep.language.datatypes.tree.Tree;

public abstract class Function extends Object {

    public final Tree arglist;

    protected Function(Tree arglist) {
        this.arglist = arglist;
    }

    public Object apply(Object... arguments) {
        if(arguments.length == 0) {
            return apply();
        } else if(arguments.length == 1) {
            return apply(arguments[0]);
        } else if(arguments.length == 2) {
            return apply(arguments[0], arguments[1]);
        } else if(arguments.length == 3) {
            return apply(arguments[0], arguments[1], arguments[2]);
        } else if(arguments.length == 4) {
            return apply(arguments[0], arguments[1], arguments[2], arguments[3]);
        }
        throw new RuntimeException("Invalid number of arguments: " + arguments.length); //TODO
    }

    public Object apply() {
        throw new RuntimeException("Invalid number of arguments: 0"); //TODO
    }

    public Object apply(Object argument) {
        throw new RuntimeException("Invalid number of arguments: 1"); //TODO
    }

    public Object apply(Object argument1, Object argument2) {
        throw new RuntimeException("Invalid number of arguments: 2"); //TODO
    }

    public Object apply(Object argument1, Object argument2, Object argument3) {
        throw new RuntimeException("Invalid number of arguments: 3"); //TODO
    }

    public Object apply(Object argument1, Object argument2, Object argument3, Object argument4) {
        throw new RuntimeException("Invalid number of arguments: 4"); //TODO
    }

}
