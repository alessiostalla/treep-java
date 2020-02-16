package treep.language.datatypes;

import treep.language.Object;
import treep.language.Symbols;
import treep.language.datatypes.tree.Cons;
import treep.language.datatypes.tree.Nothing;

public class Variable extends Function {

    private Object value;
    protected static final Cons ARGLIST = new Cons(Symbols.NIL);

    public Variable() {
        this(Nothing.AT_ALL);
    }

    public Variable(Object value) {
        super(ARGLIST);
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
