package treep.language.functions;

import treep.language.Object;
import treep.language.datatypes.Function;
import treep.language.datatypes.tree.Cons;
import treep.language.datatypes.tree.Nothing;
import treep.language.datatypes.tree.Tree;

public class cons extends Function {

    public cons() {
        super(new Cons(Nothing.AT_ALL, new Cons(Nothing.AT_ALL))); //TODO
    }

    @Override
    public Object apply(Object head) {
        return new Cons(head);
    }

    @Override
    public Object apply(Object head, Object tail) {
        if(tail instanceof Tree) {
            return new Cons(head, (Tree) tail);
        } else {
            return new Cons(head, new Cons(tail));
        }
    }

}
