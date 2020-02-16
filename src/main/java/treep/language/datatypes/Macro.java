package treep.language.datatypes;

import treep.language.Object;
import treep.language.Symbols;
import treep.language.datatypes.symbol.Symbol;
import treep.language.datatypes.tree.Nothing;
import treep.language.datatypes.tree.Tree;

import java.util.ArrayList;
import java.util.List;

public class Macro extends Operator {

    protected final Function macroFunction;

    public Macro(Function macroFunction) {
        this.macroFunction = macroFunction;
    }

    @Override
    public Object apply(Tree form, Environment environment) {
        Tree args = form.getTail();
        List<Object> actualArgs = new ArrayList<>();
        Tree arglist = macroFunction.arglist;
        while(arglist != Nothing.AT_ALL) {
            Object head = arglist.getHead();
            if(head instanceof Symbol) {
                if(head == Symbols.MACRO_BODY) {
                    actualArgs.add((Object) args);
                    args = Nothing.AT_ALL;
                } else if(head == Symbols.MACRO_ENVIRONMENT) {
                    actualArgs.add(environment);
                } else if(head == Symbols.MACRO_FORM) {
                    actualArgs.add((Object) form);
                } else if(args == Nothing.AT_ALL) {
                    throw new RuntimeException("Invalid macro application: " + form); //TODO
                } else {
                    actualArgs.add(args.getHead());
                    args = args.getTail();
                }
            } else {
                throw new RuntimeException("Invalid macro arg: " + head); //TODO and also should check at instantiation time, not invocation time
            }
            arglist = arglist.getTail();
        }
        if(args != Nothing.AT_ALL) {
            throw new RuntimeException("Invalid macro application: " + form); //TODO
        }
        return macroFunction.apply(actualArgs.toArray(new Object[0]));
    }
}
