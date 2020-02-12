package treep.language;

import treep.language.datatypes.symbol.NameSpace;
import treep.language.datatypes.symbol.Symbol;

public class Symbols {
    public static final NameSpace NAMESPACE_TREEP = new NameSpace();
    public static final Symbol SYMBOL_THE_ENVIRONMENT = NAMESPACE_TREEP.intern("the-environment");
    public static final Symbol SYMBOL_TAIL = NAMESPACE_TREEP.intern("tail");
    public static final Symbol SYMBOL_QUOTE = NAMESPACE_TREEP.intern("quote");
    public static final Symbol SYMBOL_EVAL = NAMESPACE_TREEP.intern("eval");
    public static final Symbol SYMBOL_HEAD = NAMESPACE_TREEP.intern("head");
    public static final Symbol SYMBOL_FUNCTION = NAMESPACE_TREEP.intern("function");
    public static final Symbol SYMBOL_CONS = NAMESPACE_TREEP.intern("cons");
    public static final Symbol SYMBOL_BIND = NAMESPACE_TREEP.intern("bind");
    //The very base language
    public static final Symbol SYMBOL_APPLY = NAMESPACE_TREEP.intern("apply");
}
