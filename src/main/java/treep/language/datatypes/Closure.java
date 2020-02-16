package treep.language.datatypes;

import treep.language.datatypes.tree.Tree;

public class Closure extends Function {
    public final Environment enclosingEnvironment;

    public Closure(Tree arglist, Environment enclosingEnvironment) {
        super(arglist);
        this.enclosingEnvironment = enclosingEnvironment;
    }
}
