package treep.language.functions;

import treep.language.Object;
import treep.language.Symbols;
import treep.language.datatypes.Function;
import treep.language.datatypes.tree.Cons;
import treep.language.datatypes.tree.Nothing;
import treep.language.datatypes.tree.Tree;

import java.util.Objects;

public class eq extends Function {

    public eq() {
        super(new Cons(Nothing.AT_ALL, new Cons(Nothing.AT_ALL))); //TODO
    }

    @Override
    public Object apply(Object o1, Object o2) {
        return Objects.equals(o1, o2) ? Symbols.T : Nothing.AT_ALL;
    }

}
