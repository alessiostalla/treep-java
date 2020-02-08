package treep.builtin.datatypes.tree;

import treep.Object;

public class Nothing extends Object implements Tree {

    public static final Nothing AT_ALL = new Nothing();

    private Nothing() {}

    @Override
    public Object getHead() {
        return this;
    }

    @Override
    public Tree getTail() {
        return this;
    }
}
