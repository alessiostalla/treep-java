package treep.language.datatypes.tree;

import treep.language.Object;

public interface Tree {

    Object getHead();

    Tree getTail();

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
