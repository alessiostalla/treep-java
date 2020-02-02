package treep.ast;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import treep.Object;
import treep.ObjectFactory;
import treep.parser.TreepBaseVisitor;
import treep.parser.TreepParser;
import treep.symbols.NameSpace;
import treep.symbols.Symbol;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class ASTBuilder extends TreepBaseVisitor<Object> {

    protected final Map<Integer, ObjectFactory> objectFactoryMap = new HashMap<>();

    {
        objectFactoryMap.put(TreepParser.NUMBER, literal -> new Number(new BigDecimal(literal)));
        objectFactoryMap.put(TreepParser.SYMBOL, new NameSpace());
    }

    @Override
    public Object visitTopLevelTree(TreepParser.TopLevelTreeContext ctx) {
        return visit(ctx.tree());
    }

    @Override
    public Object visitTree(TreepParser.TreeContext ctx) {
        Node result = (Node) super.visitTree(ctx);
        if(result.children.size() == 0) {
            return result.head;
        } else {
            return result;
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
            return (Object) ((Tree) aggregate).with(nextResult);
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
