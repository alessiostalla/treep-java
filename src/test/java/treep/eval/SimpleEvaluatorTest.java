package treep.eval;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.Test;
import treep.Object;
import treep.ast.ASTBuilder;
import treep.ast.Node;
import treep.ast.Nothing;
import treep.ast.Number;
import treep.parser.TreepLexer;
import treep.parser.TreepParser;
import treep.symbols.Symbol;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class SimpleEvaluatorTest {

    @Test
    public void symbol() {
        TreepLexer lexer = new TreepLexer(CharStreams.fromString("a"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TreepParser parser = new TreepParser(tokens);
        TreepParser.TreeContext tree = parser.tree();
        ASTBuilder astBuilder = new ASTBuilder();
        Object ast = astBuilder.visit(tree);
        assertEquals(Nothing.AT_ALL, new SimpleEvaluator().eval(ast));
        Number value = new Number(new BigDecimal("1"));
        Symbol a = (Symbol) astBuilder.getObjectFactoryMap().get(TreepParser.SYMBOL).get("a");
        assertEquals(value, new SimpleEvaluator().eval(ast, Environment.empty().extend(a, value)));
    }

    @Test
    public void number() {
        TreepLexer lexer = new TreepLexer(CharStreams.fromString("1"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TreepParser parser = new TreepParser(tokens);
        TreepParser.TreeContext tree = parser.tree();
        Object ast = new ASTBuilder().visit(tree);
        Object result = new SimpleEvaluator().eval(ast);
        assertTrue(result instanceof Number);
        assertEquals(1, ((Number) result).value.intValue());
    }

    @Test
    public void functionCall() {
        TreepLexer lexer = new TreepLexer(CharStreams.fromString("a b"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TreepParser parser = new TreepParser(tokens);
        TreepParser.TopLevelTreeContext tree = parser.topLevelTree();
        Object ast = new ASTBuilder().visit(tree);
        assertTrue(ast instanceof Node);
        try {
            new SimpleEvaluator().eval(ast);
            fail("Exception expected");
        } catch (RuntimeException e) {
            //Ok
        }
    }

}
