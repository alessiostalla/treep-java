package treep.language;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.pcollections.Empty;
import org.pcollections.PStack;
import treep.language.datatypes.symbol.Symbol;
import treep.language.datatypes.tree.Cons;
import treep.language.datatypes.tree.Nothing;
import treep.language.datatypes.tree.Tree;
import treep.parser.TreepBaseVisitor;
import treep.parser.TreepParser;

import java.util.HashMap;
import java.util.Map;

public class ASTBuilder extends TreepBaseVisitor<Object> {

    protected final Map<Integer, ObjectFactory> objectFactoryMap = new HashMap<>();

    public ASTBuilder(ObjectFactory<Symbol> symbolResolutionStrategy) {
        setSymbolResolutionStrategy(symbolResolutionStrategy);
    }

    @Override
    public Object visitTopLevelTree(TreepParser.TopLevelTreeContext ctx) {
        return visit(ctx.tree());
    }

    @Override
    public Object visitTree(TreepParser.TreeContext ctx) {
        PStack<Object> children = Empty.stack();
        if(ctx.node().size() == 1 && ctx.tree().isEmpty()) {
            return ctx.node(0).accept(this);
        }
        for(ParseTree node : ctx.node()) {
            children = children.plus(node.accept(this));
        }
        for(ParseTree subtree : ctx.tree()) {
            children = children.plus(subtree.accept(this));
        }
        Tree tree = Nothing.AT_ALL;
        for(Object child : children) {
            tree = new Cons(child, tree);
        }
        return (Object) tree;
    }

    @Override
    public Object visitList(TreepParser.ListContext ctx) {
        PStack<Object> children = Empty.stack();
        for(ParseTree node : ctx.node()) {
            children = children.plus(node.accept(this));
        }
        Tree list = Nothing.AT_ALL;
        for(Object child : children) {
            list = new Cons(child, list);
        }
        return (Object) list;
    }

    @Override
    public Object visitNode(TreepParser.NodeContext ctx) {
        if(ctx.atom != null) {
            return visitToken(ctx.atom);
        } else {
            return visit(ctx.list());
        }
    }

    @Override
    public Object visitTerminal(TerminalNode node) {
        Token token = node.getSymbol();
        return visitToken(token);
    }

    protected Object visitToken(Token token) {
        ObjectFactory<?> objectFactory = objectFactoryMap.get(token.getType());
        if(objectFactory != null) {
            return objectFactory.get(token.getText());
        } else {
            throw new IllegalArgumentException("Unsupported token: " + token);
        }
    }

    public Map<Integer, ObjectFactory> getObjectFactoryMap() {
        return objectFactoryMap;
    }

    public void setSymbolResolutionStrategy(ObjectFactory<Symbol> strategy) {
        objectFactoryMap.put(TreepParser.SYMBOL, strategy);
    }
}
