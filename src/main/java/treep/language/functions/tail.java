package treep.language.functions;

import treep.language.Object;
import treep.language.datatypes.Function;
import treep.language.datatypes.tree.Tree;

public class tail extends Function {

    @Override
    public Object apply(Object tree) {
        if(tree instanceof Tree) {
            return (Object) ((Tree) tree).getTail();
        } else {
            throw new IllegalArgumentException("Not a tree: " + tree); //TODO
        }
    }

}
