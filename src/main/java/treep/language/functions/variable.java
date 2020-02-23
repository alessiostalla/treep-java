package treep.language.functions;

import treep.language.Object;
import treep.language.datatypes.Function;
import treep.language.datatypes.Variable;
import treep.language.datatypes.tree.Cons;
import treep.language.datatypes.tree.Nothing;

public class variable extends Function {
    public variable() {
        super(new Cons(Nothing.AT_ALL)); //TODO
    }

    @Override
    public Variable apply() {
        return new Variable();
    }

    @Override
    public Variable apply(Object value) {
        return new Variable(value);
    }
}
