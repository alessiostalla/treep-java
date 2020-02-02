package treep.base;

import treep.Object;
import treep.Operator;
import treep.ast.Node;
import treep.eval.Environment;

public class QuoteOperator extends Operator {

    @Override
    public Object apply(Node form, Environment environment) {
        checkRequiredNumberOfArguments(form, 1);
        return form.children.get(0);
    }

}
