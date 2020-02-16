package treep.language.datatypes;

import treep.language.Object;
import treep.language.datatypes.tree.Nothing;

public class Constant extends Function {
    public final Object value;

    public Constant(Object value) {
        super(Nothing.AT_ALL);
        this.value = value;
    }

    @Override
    public Object apply() {
        return value;
    }
}
