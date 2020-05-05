package treep.language.read;

import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.Interval;
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

public class ASTBuilder extends TreepParserBaseVisitor<Object> {

    protected final DatumParser datumParser;

    public ASTBuilder(DatumParser datumParser) {
        this.datumParser = datumParser;
    }

    public Object visitGeneric(ParseTree parseTree, Parser parser) {
        if(parseTree instanceof TerminalNode) {
            return datumParser.parse(reconstructText((TerminalNode) parseTree));
        } else if(parseTree instanceof ParserRuleContext) {
            ParserRuleContext ctx = (ParserRuleContext) parseTree;
            String ruleName = parser.getRuleNames()[ctx.getRuleIndex()];
            Object head = datumParser.parse(ruleName);
            PStack<Object> children = Empty.stack();
            for(ParseTree child : ctx.children) {
                children = children.plus(visitGeneric(child, parser));
            }
            Cons tree = new Cons(head);
            for(Object child : children) {
                tree = new Cons(child, tree);
            }
            recordSourceText(ctx, tree);
            return tree;
        } else {
            throw new IllegalArgumentException("Not supported: " + parseTree);
        }
    }

    @Override
    public Object visitTopLevelTree(TreepParser.TopLevelTreeContext ctx) {
        return visit(ctx.tree());
    }

    @Override
    public Object visitTree(TreepParser.TreeContext ctx) {
        if(ctx.node().size() == 1 && ctx.tree().isEmpty()) {
            Object node = ctx.node(0).accept(this);
            node = applyModifiers(node, ctx.modifier);
            recordSourceText(ctx, node);
            return node;
        }
        PStack<Object> children = Empty.stack();
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
        recordSourceText(ctx, (Object) tree);
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
        recordSourceText(ctx, (Object) list);
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
            return new Cons(Symbols.TEMPLATE_INSERT, new Cons(object));
        } else if(modifier.getType() == TreepParser.QUOTE) {
            return new Cons(Symbols.QUOTE, new Cons(object));
        } else if(modifier.getType() == TreepParser.SPLICE_INSERT) {
            return new Cons(Symbols.TEMPLATE_SPLICE, new Cons(object));
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

    public static String reconstructText(TerminalNode terminalNode) {
        Token symbol = terminalNode.getSymbol();
        Interval interval = new Interval(symbol.getStartIndex(), symbol.getStopIndex());
        return symbol.getInputStream().getText(interval);
    }

    public static String reconstructText(ParserRuleContext parserRule) {
        return parserRule.start.getInputStream().getText(textInterval(parserRule));
    }

    public static Interval textInterval(ParserRuleContext parserRule) {
        return new Interval(parserRule.start.getStartIndex(), parserRule.stop.getStopIndex());
    }

    protected void recordSourceText(ParserRuleContext ctx, Object node) {
        if(node instanceof Cons) {
            Cons cons = (Cons) node;
            cons.addMetadata(Symbols.SOURCE_TEXT, new treep.language.datatypes.String(reconstructText(ctx)));
        }
    }

}
