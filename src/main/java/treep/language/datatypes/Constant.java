package treep.language.datatypes;

import treep.language.Object;

public class Constant extends Function {
    public final Object value;

    public Constant(Object value) {
        this.value = value;
    }

    @Override
    public Object apply() {
        return value;
    }
}
