package treep.language.operators;

import treep.language.Object;
import treep.language.datatypes.Environment;
import treep.language.datatypes.Operator;
import treep.language.datatypes.tree.Nothing;
import treep.language.datatypes.tree.Tree;
import treep.language.eval.SimpleEvaluator;

public class sequentially extends Operator {
    private final SimpleEvaluator simpleEvaluator;

    public sequentially(SimpleEvaluator simpleEvaluator) {
        this.simpleEvaluator = simpleEvaluator;
    }

    @Override
    public Object apply(Tree form, Environment environment) {
        Object result = Nothing.AT_ALL;
        while (form.getTail() != Nothing.AT_ALL) {
            form = form.getTail();
            result = simpleEvaluator.eval(form.getHead(), environment);
        }
        return result;
    }
}
