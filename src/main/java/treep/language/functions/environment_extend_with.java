package treep.language.functions;

import treep.language.Object;
import treep.language.Symbols;
import treep.language.datatypes.Environment;
import treep.language.datatypes.Function;
import treep.language.datatypes.Macro;
import treep.language.datatypes.Variable;
import treep.language.datatypes.symbol.Symbol;
import treep.language.datatypes.tree.Cons;
import treep.language.datatypes.tree.Nothing;

public class environment_extend_with extends Function {

    public environment_extend_with() {
        super(new Cons(Nothing.AT_ALL, new Cons(Nothing.AT_ALL, new Cons(Nothing.AT_ALL)))); //TODO
    }

    @Override
    public Object apply(Object environment, Object type, Object name, Object value) {
        if(!(environment instanceof Environment)) {
            throw new IllegalArgumentException("Not an environment: " + environment); //TODO
        }
        if(!(name instanceof Symbol)) {
            throw new IllegalArgumentException("Not a symbol: " + name); //TODO
        }
        if(type == Symbols.CONSTANT) {
            return ((Environment) environment).extendWithValue((Symbol) name, value);
        } else if(type == Symbols.FUNCTION) {
            if(!(value instanceof Function)) {
                throw new IllegalArgumentException("Not a function: " + value); //TODO
            }
            return ((Environment) environment).extendWithFunction((Symbol) name, (Function) value);
        } else if(type == Symbols.MACRO) {
            if(!(value instanceof Macro)) {
                throw new IllegalArgumentException("Not a macro: " + value); //TODO
            }
            return ((Environment) environment).extendWithOperator((Symbol) name, (Macro) value);
        } else if(type == Symbols.VARIABLE) {
            if(!(value instanceof Variable)) {
                throw new IllegalArgumentException("Not a variable: " + value); //TODO
            }
            return ((Environment) environment).extendWithVariable((Symbol) name, (Variable) value);
        } else {
            throw new IllegalArgumentException("Invalid binding type: " + type); //TODO
        }
    }
}
