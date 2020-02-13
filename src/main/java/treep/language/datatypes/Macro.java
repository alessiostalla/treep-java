package treep.language.datatypes;

import treep.language.Object;
import treep.language.datatypes.tree.Tree;

public class Macro extends Operator {

    protected final Function macro;

    public Macro(Function macro) {
        this.macro = macro;
    }

    @Override
    public Object apply(Tree form, Environment environment) {
        return macro.apply((Object) form, environment);
    }
}
