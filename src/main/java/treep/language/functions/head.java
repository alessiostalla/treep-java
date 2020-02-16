package treep.language.functions;

import treep.language.Object;
import treep.language.datatypes.Function;
import treep.language.datatypes.tree.Cons;
import treep.language.datatypes.tree.Nothing;
import treep.language.datatypes.tree.Tree;

public class head extends Function {

    public head() {
        super(new Cons(Nothing.AT_ALL)); //TODO
    }

    @Override
    public Object apply(Object tree) {
        if(tree instanceof Tree) {
            return ((Tree) tree).getHead();
        } else {
            throw new IllegalArgumentException("Not a tree: " + tree); //TODO
        }
    }

}
