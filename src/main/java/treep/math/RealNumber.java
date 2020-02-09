package treep.math;

import treep.language.Object;

import java.math.BigDecimal;

public class RealNumber extends Object {

    public static final RealNumber ZERO = new RealNumber(new BigDecimal("0"));
    public final BigDecimal value;

    public RealNumber(BigDecimal value) {
        this.value = value;
    }

    public RealNumber plus(RealNumber other) {
        return new RealNumber(value.add(other.value));
    }
}
