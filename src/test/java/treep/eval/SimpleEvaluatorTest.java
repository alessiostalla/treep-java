package treep.eval;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.Test;
import treep.Function;
import treep.Object;
import treep.ast.ASTBuilder;
import treep.ast.Node;
import treep.ast.Number;
import treep.parser.TreepLexer;
import treep.parser.TreepParser;
import treep.symbols.NameSpace;
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
        ASTBuilder astBuilder = new ASTBuilder(SimpleEvaluator.NAMESPACE_TREEP);
        Object ast = astBuilder.visit(tree);
        try {
            new SimpleEvaluator().eval(ast);
            fail("Exception expected");
        } catch (RuntimeException e) {
            //Ok
        }
        Number value = new Number(new BigDecimal("1"));
        Symbol a = SimpleEvaluator.NAMESPACE_TREEP.get("a");
        assertEquals(value, new SimpleEvaluator().eval(ast, Environment.empty().extend(a, value)));
    }

    @Test
    public void number() {
        TreepLexer lexer = new TreepLexer(CharStreams.fromString("1"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TreepParser parser = new TreepParser(tokens);
        TreepParser.TreeContext tree = parser.tree();
        Object ast = new ASTBuilder(SimpleEvaluator.NAMESPACE_TREEP).visit(tree);
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
        ASTBuilder astBuilder = new ASTBuilder(SimpleEvaluator.NAMESPACE_TREEP);
        Object ast = astBuilder.visit(tree);
        assertTrue(ast instanceof Node);
        try {
            new SimpleEvaluator().eval(ast);
            fail("Exception expected: function not defined");
        } catch (RuntimeException e) {
            //Ok
        }

        Symbol a = SimpleEvaluator.NAMESPACE_TREEP.get("a");
        Environment withFunction = Environment.empty().extend(a, new Function() {
            @Override
            public Object apply(Object... arguments) {
                return arguments[0];
            }
        });
        try {
            new SimpleEvaluator().eval(ast, withFunction);
            fail("Exception expected: argument not defined");
        } catch (RuntimeException e) {
            //Ok
        }

        Symbol b = SimpleEvaluator.NAMESPACE_TREEP.get("b");
        Number value = new Number(new BigDecimal("1"));
        Environment withValue = withFunction.extend(b, value);

        assertEquals(value, new SimpleEvaluator().eval(ast, withValue));
    }

    @Test
    public void quote() {
        TreepLexer lexer = new TreepLexer(CharStreams.fromString("quote\n\ta\n\t\tb"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TreepParser parser = new TreepParser(tokens);
        TreepParser.TopLevelTreeContext tree = parser.topLevelTree();
        ASTBuilder astBuilder = new ASTBuilder(SimpleEvaluator.NAMESPACE_TREEP);
        Object ast = astBuilder.visit(tree);
        Object object = new SimpleEvaluator().eval(ast);
        assertTrue(object instanceof Node);
        assertEquals(SimpleEvaluator.NAMESPACE_TREEP.intern("a"), ((Node) object).head);
        assertEquals(SimpleEvaluator.NAMESPACE_TREEP.intern("b"), ((Node) object).children.get(0));
    }

    @Test
    public void function0() {
        TreepLexer lexer = new TreepLexer(CharStreams.fromString("function () 3"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TreepParser parser = new TreepParser(tokens);
        TreepParser.TopLevelTreeContext tree = parser.topLevelTree();
        ASTBuilder astBuilder = new ASTBuilder(SimpleEvaluator.NAMESPACE_TREEP);
        Object ast = astBuilder.visit(tree);
        Object object = new SimpleEvaluator().eval(ast);
        assertTrue(object instanceof Function);
        Object result = ((Function) object).apply();
        assertTrue(result instanceof Number);
        assertEquals(new BigDecimal("3"), ((Number) result).value);
    }

}
