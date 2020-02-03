package treep.read;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;
import treep.builtin.datatypes.RealNumber;
import treep.Object;
import treep.ObjectFactory;
import treep.parser.TreepBaseVisitor;
import treep.parser.TreepParser;
import treep.builtin.datatypes.symbol.Symbol;
import treep.builtin.datatypes.tree.Cons;
import treep.builtin.datatypes.tree.Nothing;
import treep.builtin.datatypes.tree.Tree;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class ASTBuilder extends TreepBaseVisitor<Object> {

    protected final Map<Integer, ObjectFactory> objectFactoryMap = new HashMap<>();

    public ASTBuilder(ObjectFactory<Symbol> symbolResolutionStrategy) {
        objectFactoryMap.put(TreepParser.NUMBER, literal -> new RealNumber(new BigDecimal(literal)));
        setSymbolResolutionStrategy(symbolResolutionStrategy);
    }

    @Override
    public Object visitTopLevelTree(TreepParser.TopLevelTreeContext ctx) {
        return visit(ctx.tree());
    }

    @Override
    public Object visitTree(TreepParser.TreeContext ctx) {
        Tree result = (Tree) super.visitTree(ctx);
        if(result.getTail() == Nothing.AT_ALL) {
            return result.getHead();
        } else {
            return (Object) result;
        }
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
    protected Object defaultResult() {
        return Nothing.AT_ALL;
    }

    @Override
    protected Object aggregateResult(Object aggregate, Object nextResult) {
        if(nextResult != null) {
            if(nextResult instanceof Cons) {
                return (Object) ((Tree) aggregate).with((Tree) new Cons(nextResult));
            } else {
                return (Object) ((Tree) aggregate).with(nextResult);
            }
        } else {
            return aggregate;
        }
    }

    @Override
    public Object visitTerminal(TerminalNode node) {
        Token token = node.getSymbol();
        return visitToken(token);
    }

    protected Object visitToken(Token token) {
        ObjectFactory objectFactory = objectFactoryMap.get(token.getType());
        if(objectFactory != null) {
            return objectFactory.get(token.getText());
        } else {
            return null;
        }
    }

    public Map<Integer, ObjectFactory> getObjectFactoryMap() {
        return objectFactoryMap;
    }

    public void setSymbolResolutionStrategy(ObjectFactory<Symbol> strategy) {
        objectFactoryMap.put(TreepParser.SYMBOL, strategy);
    }
}
