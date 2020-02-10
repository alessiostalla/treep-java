package treep.language.eval;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.Test;
import treep.language.datatypes.Environment;
import treep.language.datatypes.Function;
import treep.language.Object;
import treep.language.ASTBuilder;
import treep.language.datatypes.tree.Cons;
import treep.language.read.SimpleDatumParser;
import treep.math.RealNumber;
import treep.parser.TreepLexer;
import treep.parser.TreepParser;
import treep.language.datatypes.symbol.Symbol;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class SimpleEvaluatorTest {

    @Test
    public void symbol() {
        TreepLexer lexer = new TreepLexer(CharStreams.fromString("a"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TreepParser parser = new TreepParser(tokens);
        TreepParser.TreeContext tree = parser.tree();
        ASTBuilder astBuilder = new ASTBuilder(new SimpleDatumParser(SimpleEvaluator.NAMESPACE_TREEP));
        Object ast = astBuilder.visit(tree);
        try {
            new SimpleEvaluator().apply(ast);
            fail("Exception expected");
        } catch (RuntimeException e) {
            //Ok
        }
        RealNumber value = new RealNumber(new BigDecimal("1"));
        Symbol a = SimpleEvaluator.NAMESPACE_TREEP.intern("a");
        assertEquals(value, new SimpleEvaluator().eval(ast, Environment.empty().extend(a, value)));
    }

    @Test
    public void number() {
        TreepLexer lexer = new TreepLexer(CharStreams.fromString("1"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TreepParser parser = new TreepParser(tokens);
        TreepParser.TreeContext tree = parser.tree();
        ASTBuilder astBuilder = new ASTBuilder(new SimpleDatumParser(SimpleEvaluator.NAMESPACE_TREEP));
        Object ast = astBuilder.visit(tree);
        Object result = new SimpleEvaluator().apply(ast);
        assertTrue(result instanceof RealNumber);
        assertEquals(1, ((RealNumber) result).value.intValue());
    }

    @Test
    public void functionCall() {
        TreepLexer lexer = new TreepLexer(CharStreams.fromString("a b"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TreepParser parser = new TreepParser(tokens);
        TreepParser.TreeContext tree = parser.tree();
        ASTBuilder astBuilder = new ASTBuilder(new SimpleDatumParser(SimpleEvaluator.NAMESPACE_TREEP));
        Object ast = astBuilder.visit(tree);
        assertTrue(ast instanceof Cons);
        try {
            new SimpleEvaluator().apply(ast);
            fail("Exception expected: function not defined");
        } catch (RuntimeException e) {
            //Ok
        }

        Symbol a = SimpleEvaluator.NAMESPACE_TREEP.intern("a");
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

        Symbol b = SimpleEvaluator.NAMESPACE_TREEP.intern("b");
        RealNumber value = new RealNumber(new BigDecimal("1"));
        Environment withValue = withFunction.extend(b, value);

        assertEquals(value, new SimpleEvaluator().eval(ast, withValue));
    }

    @Test
    public void quote() {
        TreepLexer lexer = new TreepLexer(CharStreams.fromString("quote\n\ta\n\t\tb"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TreepParser parser = new TreepParser(tokens);
        TreepParser.TopLevelTreeContext tree = parser.topLevelTree();
        ASTBuilder astBuilder = new ASTBuilder(new SimpleDatumParser(SimpleEvaluator.NAMESPACE_TREEP));
        Object ast = astBuilder.visit(tree);
        Object object = new SimpleEvaluator().apply(ast);
        assertTrue(object instanceof Cons);
        assertEquals(SimpleEvaluator.NAMESPACE_TREEP.intern("a"), ((Cons) object).head);
        assertEquals(SimpleEvaluator.NAMESPACE_TREEP.intern("b"), ((Cons) object).tail.getHead());
    }

    @Test
    public void function0() {
        TreepLexer lexer = new TreepLexer(CharStreams.fromString("function () 3"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TreepParser parser = new TreepParser(tokens);
        TreepParser.TreeContext tree = parser.tree();
        ASTBuilder astBuilder = new ASTBuilder(new SimpleDatumParser(SimpleEvaluator.NAMESPACE_TREEP));
        Object ast = astBuilder.visit(tree);
        Object object = new SimpleEvaluator().apply(ast);
        assertTrue(object instanceof Function);
        Object result = ((Function) object).apply();
        assertTrue(result instanceof RealNumber);
        assertEquals(new BigDecimal("3"), ((RealNumber) result).value);
    }

}