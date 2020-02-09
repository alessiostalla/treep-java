package treep.math.functions;

import treep.language.Object;
import treep.language.datatypes.Function;
import treep.math.RealNumber;

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
