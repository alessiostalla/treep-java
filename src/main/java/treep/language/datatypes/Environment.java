package treep.language.datatypes;

import org.pcollections.Empty;
import org.pcollections.PMap;
import treep.language.Object;
import treep.language.Symbols;
import treep.language.datatypes.symbol.Symbol;
import treep.language.datatypes.tree.Nothing;

public class Environment extends Object {

    public final Object name;
    public final PMap<Symbol, Object> bindings;

    protected Environment(Object name, PMap<Symbol, Object> bindings) {
        this.name = name;
        this.bindings = bindings;
    }

    protected Environment(PMap<Symbol, Object> bindings) {
        this(Nothing.AT_ALL, bindings);
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

    public Environment withName(Object name) {
        return new Environment(name, bindings);
    }

    public static Environment empty() {
        Environment env = new Environment(Empty.map());
        return env.extendWithValue(Symbols.THE_ENVIRONMENT, new Constant(env));
    }

}
