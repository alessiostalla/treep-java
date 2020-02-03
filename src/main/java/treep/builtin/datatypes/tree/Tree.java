package treep.builtin.datatypes.tree;

import treep.Object;

public interface Tree {

    Tree with(Tree tail);

    Object getHead();

    Tree getTail();

    default Tree with(Object object) {
        if(object instanceof Tree) {
            return with((Tree) object);
        } else {
            return with((Tree) new Cons(object));
        }
    }

    default int tailSize() {
        int size = 0;
        Tree tail = this.getTail();
        while(tail != Nothing.AT_ALL) {
            size++;
            tail = tail.getTail();
        }
        return size;
    }
}
