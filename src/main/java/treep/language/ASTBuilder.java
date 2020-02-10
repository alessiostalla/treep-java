package treep.language;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.pcollections.Empty;
import org.pcollections.PStack;
import treep.language.datatypes.tree.Cons;
import treep.language.datatypes.tree.Nothing;
import treep.language.datatypes.tree.Tree;
import treep.language.read.DatumParser;
import treep.parser.TreepBaseVisitor;
import treep.parser.TreepParser;

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
    public Object visitTerminal(TerminalNode node) {
        Token token = node.getSymbol();
        return datumParser.parse(token.getText());
    }

}
