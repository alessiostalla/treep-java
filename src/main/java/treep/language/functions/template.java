package treep.language.functions;

import treep.language.Object;
import treep.language.Symbols;
import treep.language.datatypes.Environment;
import treep.language.datatypes.Function;
import treep.language.datatypes.Macro;
import treep.language.datatypes.Operator;
import treep.language.datatypes.symbol.Symbol;
import treep.language.datatypes.tree.Cons;
import treep.language.datatypes.tree.Nothing;
import treep.language.datatypes.tree.Tree;

public class template extends Function {

    public template() {
        super(new Cons(Symbols.TEMPLATE)); //TODO
    }

    public Object apply(Object tree) {
        if(tree instanceof Cons) {
            Object head = ((Tree) tree).getHead();
            Tree tail = ((Tree) tree).getTail();
            if(head == Symbols.INSERT) {
                return tail.getHead(); //TODO check tail is nil
            } else if(head == Symbols.TEMPLATE) {
                return apply(apply((Object) tail));
            } else {
                return new Cons(Symbols.CONS, new Cons(apply(head), apply((Object) tail)));
            }
        } else if(tree instanceof Symbol) {
            return new Cons(Symbols.QUOTE, new Cons(tree));
        } else {
            return tree;
        }
    }

}
