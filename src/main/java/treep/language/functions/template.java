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

    @Override
    public Object apply(Object tree) {
        if(tree instanceof Cons) {
            Cons theTree = (Cons) tree;
            if(theTree.getHead() == Symbols.INSERT) {
                return theTree.getTail().getHead(); //TODO check tail is nil
            } else if(theTree.getHead() == Symbols.SPLICE) {
                throw new IllegalArgumentException("Splice (,@) outside of a list context"); //TODO
            }
            return processList((Tree) tree);
        } else {
            return new Cons(Symbols.QUOTE, new Cons(tree));
        }
    }

    public Cons processList(Tree tree) {
        Tree theTree = tree;
        Cons result = new Cons(Symbols.APPEND);
        while(theTree != Nothing.AT_ALL) {
            Object elem = theTree.getHead();
            if(elem instanceof Cons) {
                Cons cons = (Cons) elem;
                if(cons.head == Symbols.INSERT) {
                    result = result.append(new Cons(new Cons(Symbols.CONS, new Cons(cons.tail.getHead())))); //TODO check tail is nil
                } else if(cons.getHead() == Symbols.SPLICE) {
                    result = result.append(cons.tail);
                } else {
                    result = result.append(new Cons(processList(cons)));
                }
            } else {
                result = result.append(new Cons(new Cons(Symbols.QUOTE, new Cons(new Cons(elem)))));
            }
            theTree = theTree.getTail();
        }
        return result;
    }


}
