package treep.language.datatypes;

import org.pcollections.*;
import treep.language.Object;
import treep.language.Symbols;
import treep.language.datatypes.symbol.Symbol;
import treep.language.datatypes.tree.Nothing;

import java.util.function.Consumer;

public class Environment extends Object {

    public static final PSet<Symbol> DEFAULT_FORBIDDEN_REBINDINGS =
            Empty.<Symbol>set()
                    .plus(Symbols.ENVIRONMENT_GLOBAL).plus(Symbols.ENVIRONMENT_LOCAL)
                    .plus(Symbols.NIL).plus(Symbols.T);
    public final Object name;
    public final PMap<Symbol, Object> bindings;
    public final PSet<Symbol> forbiddenRebindings = DEFAULT_FORBIDDEN_REBINDINGS;

    protected Environment(Object name, PMap<Symbol, Object> bindings) {
        this.name = name;
        this.bindings = bindings.plus(Symbols.ENVIRONMENT_LOCAL, new Constant(this));
    }

    protected Environment(PMap<Symbol, Object> bindings) {
        this(Nothing.AT_ALL, bindings);
    }

    protected Environment(Object name, PMap<Symbol, Object> bindings, PSequence<Symbol> newBindings,
                          java.util.function.Function<Environment, PSequence<Object>> newValues) {
        PSequence<Object> values = newValues.apply(this); //Note: uninitialized final fields here
        if(values.size() != bindings.size()) {
            throw new Error("Bug: bindings don't match with values");
        }
        this.name = name;
        for(int i = 0; i < bindings.size(); i++) {
            bindings = bindings.plus(newBindings.get(i), values.get(i));
        }
        this.bindings = bindings;
    }

    public void checkRebindingAllowed(Symbol symbol) {
        if(!isRebindingAllowed(symbol)) {
            throw new IllegalArgumentException("Rebinding " + symbol + " is forbidden"); //TODO
        }
    }

    public boolean isRebindingAllowed(Symbol symbol) {
        return !forbiddenRebindings.contains(symbol) || !bindings.containsKey(symbol);
    }

    public Environment extendWithConstant(Symbol symbol, Object value) {
        checkRebindingAllowed(symbol);
        return new Environment(bindings.plus(symbol, new Constant(value)));
    }


    public Environment extendWithFunction(Symbol symbol, Function function) {
        checkRebindingAllowed(symbol);
        return new Environment(bindings.plus(symbol, function));
    }

    public Environment extendWithVariable(Symbol symbol, Variable function) {
        checkRebindingAllowed(symbol);
        return new Environment(bindings.plus(symbol, function));
    }

    public Environment extendWithOperator(Symbol symbol, Operator operator) {
        checkRebindingAllowed(symbol);
        return new Environment(bindings.plus(symbol, operator));
    }

    public Environment withName(Object name) {
        return new Environment(name, bindings);
    }

    public static Environment empty() {
        return new Environment(Empty.map());
    }

}
