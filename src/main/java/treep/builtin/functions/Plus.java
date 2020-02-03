package treep.builtin.functions;

import treep.Object;
import treep.builtin.datatypes.Function;
import treep.builtin.datatypes.RealNumber;

public class Plus extends Function {

    @Override
    public RealNumber apply(Object... arguments) {
        RealNumber result = RealNumber.ZERO;
        for(Object arg : arguments) {
            if(arg instanceof RealNumber) {
                result = result.plus((RealNumber) arg);
            } else {
                throw new IllegalArgumentException("Not a number: " + arg); //TODO
            }
        }
        return result;
    }
}
