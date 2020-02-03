package treep.builtin.datatypes.symbol;

import treep.ObjectFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NameSpace implements ObjectFactory<Symbol> {

    protected final Map<String, Symbol> symbolMap = new ConcurrentHashMap<>();

    @Override
    public Symbol get(String literal) {
        return intern(literal);
    }

    public Symbol intern(String name) {
        return symbolMap.computeIfAbsent(name, this::make);
    }

    protected Symbol make(String name) {
        return new Symbol(name);
    }
}
