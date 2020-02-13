package treep.language.datatypes;

import treep.language.Object;
import treep.language.datatypes.tree.Nothing;

public class Variable extends Function {

    private Object value;

    public Variable() {
        this(Nothing.AT_ALL);
    }

    public Variable(Object value) {
        this.value = value;
    }

    @Override
    public Object apply() {
        return value;
    }

    @Override
    public Object apply(Object argument) {
        return this.value = argument;
    }
}
