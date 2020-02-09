package treep.language.operators;

import treep.language.Object;
import treep.language.datatypes.Operator;
import treep.language.datatypes.Environment;
import treep.language.datatypes.tree.Tree;

public class quote extends Operator {

    @Override
    public Object apply(Tree form, Environment environment) {
        checkRequiredNumberOfArguments(form, 1);
        return form.getTail().getHead();
    }

}
