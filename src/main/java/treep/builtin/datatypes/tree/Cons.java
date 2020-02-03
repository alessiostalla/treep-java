package treep.builtin.datatypes.tree;

import treep.Object;

public class Cons extends Object implements Tree {

    public final Object head;
    public final Tree tail;

    public Cons(Object head) {
        this(head, Nothing.AT_ALL);
    }

    public Cons(Object head, Tree tail) {
        if(head == null) {
            throw new IllegalArgumentException("Head cannot be null");
        }
        this.head = head;
        this.tail = tail;
    }

    public Tree with(Tree tree) {
        return new Cons(head, tail.with(tree));
    }

    @Override
    public Object getHead() {
        return head;
    }

    @Override
    public Tree getTail() {
        return tail;
    }

    public static Tree extend(Object tree, Object child) {
        if(tree instanceof Tree) {
            return ((Tree) tree).with(child);
        } else {
            return new Cons(tree).with(child);
        }
    }
}
