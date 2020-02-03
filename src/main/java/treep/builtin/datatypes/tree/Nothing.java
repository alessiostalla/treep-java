package treep.builtin.datatypes.tree;

import treep.Object;

public class Nothing extends Object implements Tree {

    public static final Nothing AT_ALL = new Nothing();

    private Nothing() {}

    @Override
    public Tree with(Tree object) {
        if(object == Nothing.AT_ALL) {
            return new Cons((Object) object);
        } else {
            return object;
        }
    }

    @Override
    public Object getHead() {
        return this;
    }

    @Override
    public Tree getTail() {
        return this;
    }
}
