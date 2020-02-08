package treep.eval;

import org.pcollections.Empty;
import org.pcollections.HashPMap;
import org.pcollections.IntTreePMap;
import org.pcollections.PMap;
import treep.Object;
import treep.builtin.datatypes.Function;
import treep.builtin.datatypes.Operator;
import treep.builtin.datatypes.symbol.Symbol;

public class Environment extends Object {

    public final PMap<Symbol, Object> bindings;
    public final PMap<Symbol, Object> defaultBindings = Empty.<Symbol, Object>map().plus(
            SimpleEvaluator.SYMBOL_THE_ENVIRONMENT, new Binding(this));

    protected Environment(PMap<Symbol, Object> bindings) {
        this.bindings = defaultBindings.plusAll(bindings);
    }

    public Environment extend(Symbol symbol, Object value) {
        if(!(value instanceof Function || value instanceof Operator)) {
            value = new Binding(value);
        }
        return new Environment(bindings.plus(symbol, value));
    }

    public static Environment empty() {
        return new Environment(HashPMap.empty(IntTreePMap.empty()));
    }

    public static class Binding extends Function {
        public final Object value;

        public Binding(Object value) {
            this.value = value;
        }

        @Override
        public Object apply() {
            return value;
        }
    }

}
