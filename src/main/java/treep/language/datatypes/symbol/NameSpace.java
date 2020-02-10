package treep.language.datatypes.symbol;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NameSpace {

    protected final Map<String, Symbol> symbolMap = new ConcurrentHashMap<>();

    public Symbol intern(String name) {
        return symbolMap.computeIfAbsent(name, this::make);
    }

    protected Symbol make(String name) {
        return new Symbol(name);
    }
}
