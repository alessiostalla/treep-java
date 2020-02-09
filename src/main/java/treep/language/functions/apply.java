package treep.language.functions;

import treep.language.Object;
import treep.language.datatypes.Function;

public class apply extends Function {
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
