package treep.language.datatypes.symbol;

import treep.language.Object;

public class Symbol extends Object {

    public final String name;

    public Symbol(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
