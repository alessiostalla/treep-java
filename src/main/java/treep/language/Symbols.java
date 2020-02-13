package treep.language;

import treep.language.datatypes.symbol.NameSpace;
import treep.language.datatypes.symbol.Symbol;

public class Symbols {
    public static final NameSpace NAMESPACE_TREEP = new NameSpace();
    //The very base language
    public static final Symbol APPLY = NAMESPACE_TREEP.intern("apply");
    public static final Symbol BIND = NAMESPACE_TREEP.intern("bind");
    public static final Symbol CONS = NAMESPACE_TREEP.intern("cons");
    public static final Symbol EVAL = NAMESPACE_TREEP.intern("eval");
    public static final Symbol FUNCTION = NAMESPACE_TREEP.intern("function");
    public static final Symbol HEAD = NAMESPACE_TREEP.intern("head");
    public static final Symbol MACRO = NAMESPACE_TREEP.intern("macro");
    public static final Symbol NIL = NAMESPACE_TREEP.intern("nil");
    public static final Symbol QUOTE = NAMESPACE_TREEP.intern("quote");
    public static final Symbol TAIL = NAMESPACE_TREEP.intern("tail");
    public static final Symbol THE_ENVIRONMENT = NAMESPACE_TREEP.intern("the-environment");
}
