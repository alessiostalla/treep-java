package treep.ast;

import org.pcollections.PSequence;
import treep.Object;

public interface Tree {

    Tree with(Object object);

    Object getHead();

    PSequence<Object> getChildren();

}
