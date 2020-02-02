package treep.ast;

import treep.Object;

import java.math.BigDecimal;

public class Number extends Object {

    public final BigDecimal value;

    public Number(BigDecimal value) {
        this.value = value;
    }
}
