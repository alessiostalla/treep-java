package treep.ast;

import java.math.BigDecimal;

public class Number extends Object {

    protected final BigDecimal value;

    public Number(BigDecimal value) {
        this.value = value;
    }
}
