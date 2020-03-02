package treep.language.functions;

import treep.language.Object;
import treep.language.Symbols;
import treep.language.datatypes.Environment;
import treep.language.datatypes.Function;
import treep.language.datatypes.Operator;
import treep.language.datatypes.symbol.Symbol;
import treep.language.datatypes.tree.Cons;
import treep.language.datatypes.tree.Nothing;
import treep.language.datatypes.tree.Tree;

public class template extends Operator {

    public Object apply(Object tree) {
        if(tree instanceof Cons) {
            Object head = ((Tree) tree).getHead();
            Object tail = (Object) ((Tree) tree).getTail();
            if(head == Symbols.INSERT) {
                return tail;
            } else if(head == Symbols.TEMPLATE) {
                return apply(apply(tail));
            } else {
                return new Cons(Symbols.CONS, new Cons(apply(head), apply(tail)));
            }
        } else if(tree instanceof Symbol) {
            return new Cons(Symbols.QUOTE, new Cons(tree));
        } else {
            return tree;
        }
    }

    @Override
    public Object apply(Tree form, Environment environment) {
        return apply((Object) form.getTail());
    }
}
