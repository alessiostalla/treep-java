package treep.language.read;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.pcollections.Empty;
import org.pcollections.PStack;
import treep.language.Object;
import treep.language.Symbols;
import treep.language.datatypes.tree.Cons;
import treep.language.datatypes.tree.Nothing;
import treep.language.datatypes.tree.Tree;

import java.util.List;

public class ASTBuilder extends TreepBaseVisitor<Object> {

    protected final DatumParser datumParser;

    public ASTBuilder(DatumParser datumParser) {
        this.datumParser = datumParser;
    }

    @Override
    public Object visitTopLevelTree(TreepParser.TopLevelTreeContext ctx) {
        return visit(ctx.tree());
    }

    @Override
    public Object visitTree(TreepParser.TreeContext ctx) {
        PStack<Object> children = Empty.stack();
        if(ctx.node().size() == 1 && ctx.tree().isEmpty()) {
            Object node = ctx.node(0).accept(this);
            node = applyModifiers(node, ctx.modifier);
            return node;
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
        tree = (Tree) applyModifiers((Object) tree, ctx.modifier);
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
        Object object = super.visitNode(ctx);
        object = applyModifiers(object, ctx.modifier);
        return object;
    }

    public Object applyModifiers(Object object, List<Token> modifier) {
        for(int i = modifier.size() - 1; i >= 0; i--) {
            object = applyModifier(object, modifier.get(i));
        }
        return object;
    }

    public Cons applyModifier(Object object, Token modifier) {
        if(modifier.getType() == TreepParser.INSERT) {
            return new Cons(Symbols.INSERT, new Cons(object));
        } else if(modifier.getType() == TreepParser.QUOTE) {
            return new Cons(Symbols.QUOTE, new Cons(object));
        } else if(modifier.getType() == TreepParser.SPLICE_INSERT) {
            return new Cons(Symbols.SPLICE, new Cons(object));
        } else if(modifier.getType() == TreepParser.TEMPLATE) {
            return new Cons(Symbols.TEMPLATE, new Cons(object));
        } else {
            throw new RuntimeException("Unsupported modifier: " + modifier);
        }
    }

    @Override
    public Object visitTerminal(TerminalNode node) {
        Token token = node.getSymbol();
        return datumParser.parse(token.getText());
    }

}
