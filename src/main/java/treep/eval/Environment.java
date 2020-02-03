package treep.eval;

import org.pcollections.HashPMap;
import org.pcollections.IntTreePMap;
import org.pcollections.PMap;
import treep.Object;
import treep.builtin.datatypes.symbol.Symbol;

public class Environment extends Object {

    public final PMap<Symbol, Object> bindings;

    protected Environment(PMap<Symbol, Object> bindings) {
        this.bindings = bindings;
    }

    public Environment extend(Symbol symbol, Object value) {
        return new Environment(bindings.plus(symbol, value));
    }

    public Environment extend(PMap<Symbol, Object> values) {
        return new Environment(this.bindings.plusAll(values));
    }

    public static Environment empty() {
        return new Environment(HashPMap.empty(IntTreePMap.empty()));
    }

}
