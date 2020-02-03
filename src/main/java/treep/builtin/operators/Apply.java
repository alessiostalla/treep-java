package treep.builtin.operators;

import treep.Object;
import treep.builtin.datatypes.Operator;
import treep.builtin.datatypes.tree.Tree;
import treep.eval.Environment;

public class Apply extends Operator {

    @Override
    public Object apply(Tree form, Environment environment) {
        Object function = form.getTail().getHead();
        throw new UnsupportedOperationException("TODO");
    }

}
