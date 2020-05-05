package treep.language.datatypes.tree;

import org.pcollections.Empty;
import org.pcollections.PMap;
import treep.language.Object;
import treep.language.datatypes.symbol.Symbol;

public class Cons extends Object implements Tree {

    public final Object head;
    public final Tree tail;
    public PMap<Symbol, Object> metadata;

    public Cons(Object head) {
        this(head, Nothing.AT_ALL);
    }

    public Cons(Object head, Object tail) {
        if(head == null) {
            throw new IllegalArgumentException("Head cannot be null");
        }
        this.head = head;
        this.tail = tail instanceof Tree ? (Tree) tail : new Cons(tail);
    }

    public Cons(Object head, Tree tail) {
        this.head = head;
        this.tail = tail;
    }

    public Cons(Object head, Nothing tail) {
        this.head = head;
        this.tail = tail;
    }

    public Cons(Object head, Cons tail) {
        this.head = head;
        this.tail = tail;
    }

    @Override
    public Object getHead() {
        return head;
    }

    @Override
    public Tree getTail() {
        return tail;
    }

    public Cons append(Tree cons) {
        return new Cons(head, tail.append(cons));
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("(" + head);
        Tree rest = tail;
        while (rest != Nothing.AT_ALL) {
            result.append(" ").append(rest.getHead());
            rest = rest.getTail();
        }
        return result + ")";
    }

    public synchronized void addMetadata(Symbol key, Object value) {
        if(metadata == null) {
            metadata = Empty.map();
        }
        metadata = metadata.plus(key, value);
    }
}
