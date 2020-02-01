package treep.ast;

import org.pcollections.PSequence;
import org.pcollections.TreePVector;

public class Node extends Object implements Tree {

    protected Object head;
    protected final PSequence<Object> children;

    public Node(Object head) {
        this.head = head;
        children = TreePVector.empty();
    }

    protected Node(Object head, PSequence<Object> children) {
        this.head = head;
        this.children = children;
    }

    @Override
    public Tree with(Object object) {
        return new Node(head, children.plus(object));
    }
}
