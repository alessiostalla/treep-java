package treep.read;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.Test;
import treep.builtin.datatypes.RealNumber;
import treep.Object;
import treep.parser.TreepLexer;
import treep.parser.TreepParser;
import treep.builtin.datatypes.symbol.NameSpace;
import treep.builtin.datatypes.symbol.Symbol;
import treep.builtin.datatypes.tree.Cons;
import treep.builtin.datatypes.tree.Nothing;

import static org.junit.jupiter.api.Assertions.*;

public class ASTBuilderTest {

    @Test
    public void symbol() {
        TreepLexer lexer = new TreepLexer(CharStreams.fromString("a"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TreepParser parser = new TreepParser(tokens);
        TreepParser.TreeContext tree = parser.tree();
        Object ast = new ASTBuilder(new NameSpace()).visit(tree);
        assertTrue(ast instanceof Symbol);
        assertEquals("a", ((Symbol) ast).name);
    }

    @Test
    public void number() {
        TreepLexer lexer = new TreepLexer(CharStreams.fromString("1"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TreepParser parser = new TreepParser(tokens);
        TreepParser.TreeContext tree = parser.tree();
        Object ast = new ASTBuilder(new NameSpace()).visit(tree);
        assertTrue(ast instanceof RealNumber);
        assertEquals("1", ((RealNumber) ast).value.toString());
    }

    @Test
    public void emptyTree() {
        TreepLexer lexer = new TreepLexer(CharStreams.fromString("()"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TreepParser parser = new TreepParser(tokens);
        TreepParser.TopLevelTreeContext tree = parser.topLevelTree();
        Object ast = new ASTBuilder(new NameSpace()).visit(tree);
        assertEquals(Nothing.AT_ALL, ast);
    }

    @Test
    public void treeOneChild() {
        TreepLexer lexer = new TreepLexer(CharStreams.fromString("a b"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TreepParser parser = new TreepParser(tokens);
        TreepParser.TopLevelTreeContext tree = parser.topLevelTree();
        Object ast = new ASTBuilder(new NameSpace()).visit(tree);
        assertTrue(ast instanceof Cons);
        Cons root = (Cons) ast;
        assertTrue(root.head instanceof Symbol);
        assertEquals("a", ((Symbol) root.head).name);
        assertEquals(1, root.tailSize());
        Object child = root.tail.getHead();
        assertTrue(child instanceof Symbol);
        assertEquals("b", ((Symbol) child).name);
    }

    @Test
    public void treeTwoChildren() {
        TreepLexer lexer = new TreepLexer(CharStreams.fromString("a b c"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TreepParser parser = new TreepParser(tokens);
        TreepParser.TopLevelTreeContext tree = parser.topLevelTree();
        Object ast = new ASTBuilder(new NameSpace()).visit(tree);
        assertTrue(ast instanceof Cons);
        Cons root = (Cons) ast;
        assertTrue(root.head instanceof Symbol);
        assertEquals("a", ((Symbol) root.head).name);
        assertEquals(2, root.tailSize());
        Object child = root.tail.getHead();
        assertTrue(child instanceof Symbol);
        assertEquals("b", ((Symbol) child).name);
        child = root.tail.getTail().getHead();
        assertTrue(child instanceof Symbol);
        assertEquals("c", ((Symbol) child).name);
    }

    @Test
    public void treeWithInitialIndentation() {
        TreepLexer lexer = new TreepLexer(CharStreams.fromString("\na"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TreepParser parser = new TreepParser(tokens);
        TreepParser.TopLevelTreeContext topLevel = parser.topLevelTree();
        TreepParser.TreeContext tree = topLevel.tree();
        Object ast = new ASTBuilder(new NameSpace()).visit(tree);
        assertTrue(ast instanceof Symbol);
        assertEquals("a", ((Symbol) ast).name);
    }

}
