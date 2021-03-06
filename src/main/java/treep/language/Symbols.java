package treep.language;

import treep.language.datatypes.symbol.NameSpace;
import treep.language.datatypes.symbol.Symbol;

public class Symbols {
    public static final NameSpace NAMESPACE_TREEP = new NameSpace();
    //The very base language
    public static final Symbol APPEND = NAMESPACE_TREEP.intern("append");
    public static final Symbol APPLY = NAMESPACE_TREEP.intern("apply");
    public static final Symbol CONS = NAMESPACE_TREEP.intern("cons");
    public static final Symbol CONS_HEAD = NAMESPACE_TREEP.intern("cons:head");
    public static final Symbol CONS_TAIL = NAMESPACE_TREEP.intern("cons:tail");
    public static final Symbol CONSTANT = NAMESPACE_TREEP.intern("constant");
    public static final Symbol DO = NAMESPACE_TREEP.intern("do");
    public static final Symbol ENVIRONMENT_EXTEND = NAMESPACE_TREEP.intern("environment:extend");
    public static final Symbol ENVIRONMENT_GLOBAL = NAMESPACE_TREEP.intern("environment:global");
    public static final Symbol ENVIRONMENT_LOCAL = NAMESPACE_TREEP.intern("environment:local");
    public static final Symbol ERROR = NAMESPACE_TREEP.intern("error");
    public static final Symbol EQ = NAMESPACE_TREEP.intern("eq");
    public static final Symbol EVAL = NAMESPACE_TREEP.intern("eval");
    public static final Symbol FUNCTION = NAMESPACE_TREEP.intern("function");
    public static final Symbol IF = NAMESPACE_TREEP.intern("if");
    public static final Symbol LOOP = NAMESPACE_TREEP.intern("loop");
    public static final Symbol MACRO = NAMESPACE_TREEP.intern("macro");
    public static final Symbol MACRO_BODY = NAMESPACE_TREEP.intern("macro:body");
    public static final Symbol MACRO_ENVIRONMENT = NAMESPACE_TREEP.intern("macro:environment");
    public static final Symbol MACRO_EXPAND = NAMESPACE_TREEP.intern("macro:expand");
    public static final Symbol MACRO_FORM = NAMESPACE_TREEP.intern("macro:form");
    public static final Symbol NIL = NAMESPACE_TREEP.intern("nil");
    public static final Symbol QUIT = NAMESPACE_TREEP.intern("quit");
    public static final Symbol QUOTE = NAMESPACE_TREEP.intern("quote");
    public static final Symbol RETURN = NAMESPACE_TREEP.intern("return");
    public static final Symbol SET = NAMESPACE_TREEP.intern("set!");
    public static final Symbol SOURCE_TEXT = NAMESPACE_TREEP.intern("source-text");
    public static final Symbol T = NAMESPACE_TREEP.intern("T");
    public static final Symbol LOWERCASE_T = NAMESPACE_TREEP.intern("t"); //alias
    public static final Symbol TEMPLATE = NAMESPACE_TREEP.intern("template");
    public static final Symbol TEMPLATE_INSERT = NAMESPACE_TREEP.intern("template:insert");
    public static final Symbol TEMPLATE_SPLICE = NAMESPACE_TREEP.intern("template:splice");
    public static final Symbol VARIABLE = NAMESPACE_TREEP.intern("variable");
    public static final Symbol WITH_ENVIRONMENT = NAMESPACE_TREEP.intern("with-environment");
}
