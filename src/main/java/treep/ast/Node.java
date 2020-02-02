package treep.ast;

import org.pcollections.PSequence;
import org.pcollections.TreePVector;
import treep.Object;

public class Node extends Object implements Tree {

    public final Object head;
    public final PSequence<Object> children;

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

    public static Tree extend(Object tree, Object child) {
        if(tree instanceof Tree) {
            return ((Tree) tree).with(child);
        } else {
            return new Node(tree).with(child);
        }
    }
}
