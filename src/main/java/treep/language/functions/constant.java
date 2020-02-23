package treep.language.functions;

import treep.language.Object;
import treep.language.datatypes.Constant;
import treep.language.datatypes.Function;
import treep.language.datatypes.Variable;
import treep.language.datatypes.tree.Cons;
import treep.language.datatypes.tree.Nothing;

public class constant extends Function {
    public constant() {
        super(new Cons(Nothing.AT_ALL)); //TODO
    }

    @Override
    public Constant apply(Object value) {
        return new Constant(value);
    }
}
