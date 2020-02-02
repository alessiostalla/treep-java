package treep.ast;

import org.pcollections.PSequence;
import org.pcollections.TreePVector;
import treep.Object;

public class Node extends Object implements Tree {

    public final Object head;
    public final PSequence<Object> children;

    public Node(Object head) {
        this(head, TreePVector.empty());
    }

    public Node(Object head, PSequence<Object> children) {
        if(head == null) {
            throw new IllegalArgumentException("Head cannot be null");
        }
        this.head = head;
        this.children = children;
    }

    @Override
    public Tree with(Object object) {
        return new Node(head, children.plus(object));
    }

    @Override
    public Object getHead() {
        return head;
    }

    @Override
    public PSequence<Object> getChildren() {
        return children;
    }

    public static Tree extend(Object tree, Object child) {
        if(tree instanceof Tree) {
            return ((Tree) tree).with(child);
        } else {
            return new Node(tree).with(child);
        }
    }
}
