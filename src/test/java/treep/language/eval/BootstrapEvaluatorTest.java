package treep.language.eval;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.Test;
import treep.language.Symbols;
import treep.language.datatypes.Environment;
import treep.language.datatypes.Function;
import treep.language.Object;
import treep.language.datatypes.tree.Nothing;
import treep.language.read.ASTBuilder;
import treep.language.datatypes.tree.Cons;
import treep.language.read.SimpleDatumParser;
import treep.language.read.TreepLexer;
import treep.language.read.TreepParser;
import treep.math.RealNumber;

import treep.language.datatypes.symbol.Symbol;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class BootstrapEvaluatorTest {

    @Test
    public void symbol() {
        TreepLexer lexer = new TreepLexer(CharStreams.fromString("a"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TreepParser parser = new TreepParser(tokens);
        TreepParser.TreeContext tree = parser.tree();
        ASTBuilder astBuilder = new ASTBuilder(new SimpleDatumParser(Symbols.NAMESPACE_TREEP));
        Object ast = astBuilder.visit(tree);
        try {
            new BootstrapEvaluator().apply(ast);
            fail("Exception expected");
        } catch (RuntimeException e) {
            //Ok
        }
        RealNumber value = new RealNumber(new BigDecimal("1"));
        Symbol a = Symbols.NAMESPACE_TREEP.intern("a");
        assertEquals(value, new BootstrapEvaluator().eval(ast, Environment.empty().extendWithConstant(a, value)));
    }

    @Test
    public void number() {
        TreepLexer lexer = new TreepLexer(CharStreams.fromString("1"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TreepParser parser = new TreepParser(tokens);
        TreepParser.TreeContext tree = parser.tree();
        ASTBuilder astBuilder = new ASTBuilder(new SimpleDatumParser(Symbols.NAMESPACE_TREEP));
        Object ast = astBuilder.visit(tree);
        Object result = new BootstrapEvaluator().apply(ast);
        assertTrue(result instanceof RealNumber);
        assertEquals(1, ((RealNumber) result).value.intValue());
    }

    @Test
    public void functionCall() {
        TreepLexer lexer = new TreepLexer(CharStreams.fromString("a b"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TreepParser parser = new TreepParser(tokens);
        TreepParser.TreeContext tree = parser.tree();
        ASTBuilder astBuilder = new ASTBuilder(new SimpleDatumParser(Symbols.NAMESPACE_TREEP));
        Object ast = astBuilder.visit(tree);
        assertTrue(ast instanceof Cons);
        try {
            new BootstrapEvaluator().apply(ast);
            fail("Exception expected: function not defined");
        } catch (RuntimeException e) {
            //Ok
        }

        Symbol a = Symbols.NAMESPACE_TREEP.intern("a");
        Environment withFunction = Environment.empty().extendWithFunction(a, new Function(Nothing.AT_ALL) {
            @Override
            public Object apply(Object... arguments) {
                return arguments[0];
            }
        });
        try {
            new BootstrapEvaluator().eval(ast, withFunction);
            fail("Exception expected: argument not defined");
        } catch (RuntimeException e) {
            //Ok
        }

        Symbol b = Symbols.NAMESPACE_TREEP.intern("b");
        RealNumber value = new RealNumber(new BigDecimal("1"));
        Environment withValue = withFunction.extendWithConstant(b, value);

        assertEquals(value, new BootstrapEvaluator().eval(ast, withValue));
    }

    @Test
    public void quote() {
        TreepLexer lexer = new TreepLexer(CharStreams.fromString("quote\n\ta\n\t\tb"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TreepParser parser = new TreepParser(tokens);
        TreepParser.TopLevelTreeContext tree = parser.topLevelTree();
        ASTBuilder astBuilder = new ASTBuilder(new SimpleDatumParser(Symbols.NAMESPACE_TREEP));
        Object ast = astBuilder.visit(tree);
        Object object = new BootstrapEvaluator().apply(ast);
        assertTrue(object instanceof Cons);
        assertEquals(Symbols.NAMESPACE_TREEP.intern("a"), ((Cons) object).head);
        assertEquals(Symbols.NAMESPACE_TREEP.intern("b"), ((Cons) object).tail.getHead());
    }

    @Test
    public void quoteAbbreviated() {
        TreepLexer lexer = new TreepLexer(CharStreams.fromString("'a\n\tb"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TreepParser parser = new TreepParser(tokens);
        TreepParser.TopLevelTreeContext tree = parser.topLevelTree();
        ASTBuilder astBuilder = new ASTBuilder(new SimpleDatumParser(Symbols.NAMESPACE_TREEP));
        Object ast = astBuilder.visit(tree);
        Object object = new BootstrapEvaluator().apply(ast);
        assertTrue(object instanceof Cons);
        assertEquals(Symbols.NAMESPACE_TREEP.intern("a"), ((Cons) object).head);
        assertEquals(Symbols.NAMESPACE_TREEP.intern("b"), ((Cons) object).tail.getHead());
    }

    @Test
    public void cons() {
        TreepLexer lexer = new TreepLexer(CharStreams.fromString("cons 'a 'b"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TreepParser parser = new TreepParser(tokens);
        TreepParser.TopLevelTreeContext tree = parser.topLevelTree();
        ASTBuilder astBuilder = new ASTBuilder(new SimpleDatumParser(Symbols.NAMESPACE_TREEP));
        Object ast = astBuilder.visit(tree);
        Object object = new BootstrapEvaluator().apply(ast);
        assertTrue(object instanceof Cons);
        assertEquals(Symbols.NAMESPACE_TREEP.intern("a"), ((Cons) object).head);
        assertEquals(Symbols.NAMESPACE_TREEP.intern("b"), ((Cons) object).tail.getHead());
    }


    @Test
    public void function0() {
        TreepLexer lexer = new TreepLexer(CharStreams.fromString("function () 3"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TreepParser parser = new TreepParser(tokens);
        TreepParser.TreeContext tree = parser.tree();
        ASTBuilder astBuilder = new ASTBuilder(new SimpleDatumParser(Symbols.NAMESPACE_TREEP));
        Object ast = astBuilder.visit(tree);
        Object object = new BootstrapEvaluator().apply(ast);
        assertTrue(object instanceof Function);
        Object result = ((Function) object).apply();
        assertTrue(result instanceof RealNumber);
        assertEquals(new BigDecimal("3"), ((RealNumber) result).value);
    }

    @Test
    public void function1() {
        TreepLexer lexer = new TreepLexer(CharStreams.fromString("function (x) x"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TreepParser parser = new TreepParser(tokens);
        TreepParser.TreeContext tree = parser.tree();
        ASTBuilder astBuilder = new ASTBuilder(new SimpleDatumParser(Symbols.NAMESPACE_TREEP));
        Object ast = astBuilder.visit(tree);
        Object object = new BootstrapEvaluator().apply(ast);
        assertTrue(object instanceof Function);
        Object result = ((Function) object).apply(new RealNumber(new BigDecimal("3")));
        assertTrue(result instanceof RealNumber);
        assertEquals(new BigDecimal("3"), ((RealNumber) result).value);
    }

    @Test
    public void bindValue() {
        TreepLexer lexer = new TreepLexer(CharStreams.fromString(
                "with-environment (environment:extend environment:local 'constant 'x 3)\n\tx"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TreepParser parser = new TreepParser(tokens);
        TreepParser.TreeContext tree = parser.tree();
        ASTBuilder astBuilder = new ASTBuilder(new SimpleDatumParser(Symbols.NAMESPACE_TREEP));
        Object ast = astBuilder.visit(tree);
        Object object = new BootstrapEvaluator().apply(ast);
        assertTrue(object instanceof RealNumber);
        assertEquals(new BigDecimal("3"), ((RealNumber) object).value);
    }

    @Test
    public void bindVariable() {
        TreepLexer lexer = new TreepLexer(CharStreams.fromString(
                "with-environment (environment:extend environment:local 'variable 'x (variable 3))\n\tcons x (set! x 4)"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TreepParser parser = new TreepParser(tokens);
        TreepParser.TreeContext tree = parser.tree();
        ASTBuilder astBuilder = new ASTBuilder(new SimpleDatumParser(Symbols.NAMESPACE_TREEP));
        Object ast = astBuilder.visit(tree);
        Object object = new BootstrapEvaluator().apply(ast);
        assertTrue(object instanceof Cons);
        assertEquals(new BigDecimal("3"), ((RealNumber) ((Cons) object).getHead()).value);
        assertEquals(new BigDecimal("4"), ((RealNumber) ((Cons) object).getTail().getHead()).value);
    }

    @Test
    public void bindFunction() {
        TreepLexer lexer = new TreepLexer(CharStreams.fromString("with-environment (environment:extend environment:local 'function 'f (function (x) x))\n\tf 3"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TreepParser parser = new TreepParser(tokens);
        TreepParser.TreeContext tree = parser.tree();
        ASTBuilder astBuilder = new ASTBuilder(new SimpleDatumParser(Symbols.NAMESPACE_TREEP));
        Object ast = astBuilder.visit(tree);
        Object object = new BootstrapEvaluator().apply(ast);
        assertTrue(object instanceof RealNumber);
        assertEquals(new BigDecimal("3"), ((RealNumber) object).value);
    }

    @Test
    public void bindInvalid() {
        TreepLexer lexer = new TreepLexer(CharStreams.fromString("bind (x)\n\tx"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TreepParser parser = new TreepParser(tokens);
        TreepParser.TreeContext tree = parser.tree();
        ASTBuilder astBuilder = new ASTBuilder(new SimpleDatumParser(Symbols.NAMESPACE_TREEP));
        Object ast = astBuilder.visit(tree);
        try {
            new BootstrapEvaluator().apply(ast);
            fail("Invalid binding exception expected");
        } catch (Exception e) {
            //Ok
        }
    }

    @Test
    public void bindForbidden() {
        TreepLexer lexer = new TreepLexer(CharStreams.fromString("bind ((nil 42))\n\tnil"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TreepParser parser = new TreepParser(tokens);
        TreepParser.TreeContext tree = parser.tree();
        ASTBuilder astBuilder = new ASTBuilder(new SimpleDatumParser(Symbols.NAMESPACE_TREEP));
        Object ast = astBuilder.visit(tree);
        try {
            new BootstrapEvaluator().apply(ast);
            fail("Forbidden binding exception expected");
        } catch (Exception e) {
            //Ok
        }
    }

    @Test
    public void bindMacro() {
        TreepLexer lexer = new TreepLexer(CharStreams.fromString(
                "with-environment (environment:extend environment:local 'macro 'm (macro (x macro:body) `(,@macro:body ,x)))\n\tm 4 cons 3"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TreepParser parser = new TreepParser(tokens);
        TreepParser.TreeContext tree = parser.tree();
        ASTBuilder astBuilder = new ASTBuilder(new SimpleDatumParser(Symbols.NAMESPACE_TREEP));
        Object ast = astBuilder.visit(tree);
        Object object = new BootstrapEvaluator().apply(ast);
        assertTrue(object instanceof Cons);
        Object head = ((Cons) object).head;
        assertTrue(head instanceof RealNumber);
        assertEquals(new BigDecimal("3"), ((RealNumber) head).value);
    }

    @Test
    public void ifTrue() {
        TreepLexer lexer = new TreepLexer(CharStreams.fromString("if 1 2 3"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TreepParser parser = new TreepParser(tokens);
        TreepParser.TreeContext tree = parser.tree();
        ASTBuilder astBuilder = new ASTBuilder(new SimpleDatumParser(Symbols.NAMESPACE_TREEP));
        Object ast = astBuilder.visit(tree);
        Object object = new BootstrapEvaluator().apply(ast);
        assertTrue(object instanceof RealNumber);
        assertEquals(new BigDecimal("2"), ((RealNumber) object).value);
    }

    @Test
    public void ifFalse() {
        TreepLexer lexer = new TreepLexer(CharStreams.fromString("if nil 1 2"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TreepParser parser = new TreepParser(tokens);
        TreepParser.TreeContext tree = parser.tree();
        ASTBuilder astBuilder = new ASTBuilder(new SimpleDatumParser(Symbols.NAMESPACE_TREEP));
        Object ast = astBuilder.visit(tree);
        Object object = new BootstrapEvaluator().apply(ast);
        assertTrue(object instanceof RealNumber);
        assertEquals(new BigDecimal("2"), ((RealNumber) object).value);
    }

    @Test
    public void loopReturn() {
        TreepLexer lexer = new TreepLexer(CharStreams.fromString("loop\n\treturn 3"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TreepParser parser = new TreepParser(tokens);
        TreepParser.TreeContext tree = parser.tree();
        ASTBuilder astBuilder = new ASTBuilder(new SimpleDatumParser(Symbols.NAMESPACE_TREEP));
        Object ast = astBuilder.visit(tree);
        Object object = new BootstrapEvaluator().apply(ast);
        assertTrue(object instanceof RealNumber);
        assertEquals(new BigDecimal("3"), ((RealNumber) object).value);
    }

    @Test
    public void nestedLoopReturn() {
        TreepLexer lexer = new TreepLexer(CharStreams.fromString("loop (loop (return 2)) (return 3)"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TreepParser parser = new TreepParser(tokens);
        TreepParser.TreeContext tree = parser.tree();
        ASTBuilder astBuilder = new ASTBuilder(new SimpleDatumParser(Symbols.NAMESPACE_TREEP));
        Object ast = astBuilder.visit(tree);
        Object object = new BootstrapEvaluator().apply(ast);
        assertTrue(object instanceof RealNumber);
        assertEquals(new BigDecimal("3"), ((RealNumber) object).value);
    }

    @Test
    public void error1() {
        TreepLexer lexer = new TreepLexer(CharStreams.fromString("error 'oops"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TreepParser parser = new TreepParser(tokens);
        TreepParser.TreeContext tree = parser.tree();
        ASTBuilder astBuilder = new ASTBuilder(new SimpleDatumParser(Symbols.NAMESPACE_TREEP));
        Object ast = astBuilder.visit(tree);
        try {
            new BootstrapEvaluator().apply(ast);
            fail("Exception expected");
        } catch (Exception e) {
            //ignore
        }
    }

    @Test
    public void templateSimple() {
        TreepLexer lexer = new TreepLexer(CharStreams.fromString("`a"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TreepParser parser = new TreepParser(tokens);
        TreepParser.TopLevelTreeContext tree = parser.topLevelTree();
        ASTBuilder astBuilder = new ASTBuilder(new SimpleDatumParser(Symbols.NAMESPACE_TREEP));
        Object ast = astBuilder.visit(tree);
        Object object = new BootstrapEvaluator().apply(ast);
        assertEquals(Symbols.NAMESPACE_TREEP.intern("a"), object);
    }

    @Test
    public void templateDouble() {
        TreepLexer lexer = new TreepLexer(CharStreams.fromString("``a"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TreepParser parser = new TreepParser(tokens);
        TreepParser.TopLevelTreeContext tree = parser.topLevelTree();
        ASTBuilder astBuilder = new ASTBuilder(new SimpleDatumParser(Symbols.NAMESPACE_TREEP));
        Object ast = astBuilder.visit(tree);
        Object object = new BootstrapEvaluator().apply(ast);
        object = new BootstrapEvaluator().apply(object);
        assertEquals(Symbols.NAMESPACE_TREEP.intern("a"), object);
    }

    @Test
    public void templateInsert() {
        TreepLexer lexer = new TreepLexer(CharStreams.fromString("`,a"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TreepParser parser = new TreepParser(tokens);
        TreepParser.TreeContext tree = parser.tree();
        ASTBuilder astBuilder = new ASTBuilder(new SimpleDatumParser(Symbols.NAMESPACE_TREEP));
        Object ast = astBuilder.visit(tree);
        RealNumber value = new RealNumber(new BigDecimal("1"));
        Symbol a = Symbols.NAMESPACE_TREEP.intern("a");
        BootstrapEvaluator eval = new BootstrapEvaluator();
        assertEquals(value, eval.eval(ast, eval.getGlobalEnvironment().extendWithConstant(a, value)));
    }

    @Test
    public void templateSplice() {
        TreepLexer lexer = new TreepLexer(CharStreams.fromString("`(,@a)"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TreepParser parser = new TreepParser(tokens);
        TreepParser.TreeContext tree = parser.tree();
        ASTBuilder astBuilder = new ASTBuilder(new SimpleDatumParser(Symbols.NAMESPACE_TREEP));
        Object ast = astBuilder.visit(tree);
        Cons value = new Cons(new RealNumber(new BigDecimal("1")), new RealNumber(new BigDecimal("2")));
        Symbol a = Symbols.NAMESPACE_TREEP.intern("a");
        BootstrapEvaluator eval = new BootstrapEvaluator();
        assertEquals(value, eval.eval(ast, eval.getGlobalEnvironment().extendWithConstant(a, value)));
    }

    @Test
    public void templateQuoteInsert() {
        TreepLexer lexer = new TreepLexer(CharStreams.fromString("`',a"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TreepParser parser = new TreepParser(tokens);
        TreepParser.TreeContext tree = parser.tree();
        ASTBuilder astBuilder = new ASTBuilder(new SimpleDatumParser(Symbols.NAMESPACE_TREEP));
        Object ast = astBuilder.visit(tree);
        RealNumber value = new RealNumber(new BigDecimal("1"));
        Symbol a = Symbols.NAMESPACE_TREEP.intern("a");
        BootstrapEvaluator eval = new BootstrapEvaluator();
        Object quote = eval.eval(ast, eval.getGlobalEnvironment().extendWithConstant(a, value));
        assertEquals(value, eval.apply(quote));
    }

}
