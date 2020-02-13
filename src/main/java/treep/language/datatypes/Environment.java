package treep.language.datatypes;

import org.pcollections.Empty;
import org.pcollections.PMap;
import treep.language.Object;
import treep.language.Symbols;
import treep.language.datatypes.symbol.Symbol;

public class Environment extends Object {

    public final PMap<Symbol, Object> bindings;
    public final PMap<Symbol, Object> defaultBindings = Empty.<Symbol, Object>map().plus(
            Symbols.THE_ENVIRONMENT, new Constant(this));

    protected Environment(PMap<Symbol, Object> bindings) {
        this.bindings = defaultBindings.plusAll(bindings);
    }

    public Environment extendWithValue(Symbol symbol, Object value) {
        return new Environment(bindings.plus(symbol, new Constant(value)));
    }

    public Environment extendWithFunction(Symbol symbol, Function function) {
        return new Environment(bindings.plus(symbol, function));
    }

    public Environment extendWithOperator(Symbol symbol, Operator operator) {
        return new Environment(bindings.plus(symbol, operator));
    }

    public static Environment empty() {
        return new Environment(Empty.map());
    }

}
