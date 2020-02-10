package treep.language.datatypes;

import org.pcollections.Empty;
import org.pcollections.PMap;
import treep.language.Object;
import treep.language.datatypes.symbol.Symbol;
import treep.language.datatypes.tree.Cons;
import treep.language.datatypes.tree.Nothing;
import treep.language.eval.SimpleEvaluator;

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

    public Environment extend(Object binding) {
        if(binding instanceof Symbol) {
            return extend((Symbol) binding, Nothing.AT_ALL);
        } else if(binding instanceof Cons) {
            Object name = ((Cons) binding).head;
            if(!(name instanceof Symbol)) {
                throw new IllegalArgumentException("Not a symbol: " + name);
            }
            return extend((Symbol) name, ((Cons) binding).tail.getHead()); //TODO metadata
        } else {
            throw new IllegalArgumentException("Not a binding form: " + binding);
        }
    }

    public static Environment empty() {
        return new Environment(Empty.map());
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
