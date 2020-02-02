package treep.ast;

import org.pcollections.PSequence;
import org.pcollections.TreePVector;
import treep.Object;

public class Nothing extends Object implements Tree {

    public static final Nothing AT_ALL = new Nothing();

    private Nothing() {}

    @Override
    public Tree with(Object object) {
        return new Node(object);
    }

    @Override
    public Object getHead() {
        return this;
    }

    @Override
    public PSequence<Object> getChildren() {
        return TreePVector.empty();
    }
}
