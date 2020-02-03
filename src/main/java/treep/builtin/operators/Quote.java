package treep.builtin.operators;

import treep.Object;
import treep.builtin.datatypes.Operator;
import treep.eval.Environment;
import treep.builtin.datatypes.tree.Tree;

public class Quote extends Operator {

    @Override
    public Object apply(Tree form, Environment environment) {
        checkRequiredNumberOfArguments(form, 1);
        return form.getTail().getHead();
    }

}
