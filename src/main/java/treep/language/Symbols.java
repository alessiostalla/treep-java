package treep.language;

import treep.language.datatypes.symbol.NameSpace;
import treep.language.datatypes.symbol.Symbol;

public class Symbols {
    public static final NameSpace NAMESPACE_TREEP = new NameSpace();
    //The very base language
    public static final Symbol APPLY = NAMESPACE_TREEP.intern("apply");
    public static final Symbol BIND = NAMESPACE_TREEP.intern("bind");
    public static final Symbol CONS = NAMESPACE_TREEP.intern("cons");
    public static final Symbol CONSTANT = NAMESPACE_TREEP.intern("constant");
    public static final Symbol ENVIRONMENT_EXTEND_WITH = NAMESPACE_TREEP.intern("environment:extend-with");
    public static final Symbol EVAL = NAMESPACE_TREEP.intern("eval");
    public static final Symbol FUNCTION = NAMESPACE_TREEP.intern("function");
    public static final Symbol HEAD = NAMESPACE_TREEP.intern("head");
    public static final Symbol IF = NAMESPACE_TREEP.intern("if");
    public static final Symbol LOOP = NAMESPACE_TREEP.intern("loop");
    public static final Symbol MACRO = NAMESPACE_TREEP.intern("macro");
    public static final Symbol MACRO_BODY = NAMESPACE_TREEP.intern("macro:body");
    public static final Symbol MACRO_ENVIRONMENT = NAMESPACE_TREEP.intern("macro:environment");
    public static final Symbol MACRO_FORM = NAMESPACE_TREEP.intern("macro:form");
    public static final Symbol NIL = NAMESPACE_TREEP.intern("nil");
    public static final Symbol QUOTE = NAMESPACE_TREEP.intern("quote");
    public static final Symbol RETURN = NAMESPACE_TREEP.intern("return");
    public static final Symbol SET = NAMESPACE_TREEP.intern("set!");
    public static final Symbol TAIL = NAMESPACE_TREEP.intern("tail");
    public static final Symbol THE_ENVIRONMENT = NAMESPACE_TREEP.intern("the-environment");
    public static final Symbol THE_GLOBAL_ENVIRONMENT = NAMESPACE_TREEP.intern("the-global-environment");
    public static final Symbol VAR = NAMESPACE_TREEP.intern("var");
}
