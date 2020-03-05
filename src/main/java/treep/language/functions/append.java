package treep.language.functions;

import treep.language.Object;
import treep.language.datatypes.Function;
import treep.language.datatypes.tree.Cons;
import treep.language.datatypes.tree.Nothing;
import treep.language.datatypes.tree.Tree;

public class append extends Function {

    public append() {
        super(new Cons(Nothing.AT_ALL, new Cons(Nothing.AT_ALL))); //TODO
    }

    @Override
    public Object apply(Object... objects) {
        Tree result = Nothing.AT_ALL;
        for(Object o : objects) {
            if (!(o instanceof Tree)) {
                throw new IllegalArgumentException("Not a tree: " + o); //TODO
            }
            result = result.append((Tree) o);
        }
        return (Object) result;
    }

    @Override
    public Nothing apply() {
        return Nothing.AT_ALL;
    }

    public Object apply(Object o) {
        if (!(o instanceof Tree)) {
            throw new IllegalArgumentException("Not a tree: " + o); //TODO
        }
        return o;
    }

    @Override
    public Object apply(Object argument1, Object argument2) {
        if (!(argument1 instanceof Tree)) {
            throw new IllegalArgumentException("Not a tree: " + argument1); //TODO
        }
        if (!(argument2 instanceof Tree)) {
            throw new IllegalArgumentException("Not a tree: " + argument2); //TODO
        }
        return (Object) ((Tree) argument1).append((Tree) argument2);
    }

    @Override
    public Object apply(Object argument1, Object argument2, Object argument3) {
        return apply(apply(argument1, argument2), argument3);
    }

    @Override
    public Object apply(Object argument1, Object argument2, Object argument3, Object argument4) {
        return apply(apply(apply(argument1, argument2), argument3), argument4);
    }

}
