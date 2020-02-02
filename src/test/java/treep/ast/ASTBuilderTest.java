package treep.ast;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.Test;
import treep.Object;
import treep.parser.TreepLexer;
import treep.parser.TreepParser;
import treep.symbols.Symbol;

import static org.junit.jupiter.api.Assertions.*;

public class ASTBuilderTest {

    @Test
    public void symbol() {
        TreepLexer lexer = new TreepLexer(CharStreams.fromString("a"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TreepParser parser = new TreepParser(tokens);
        TreepParser.TreeContext tree = parser.tree();
        Object ast = new ASTBuilder().visit(tree);
        assertTrue(ast instanceof Symbol);
        assertEquals("a", ((Symbol) ast).name);
    }

    @Test
    public void number() {
        TreepLexer lexer = new TreepLexer(CharStreams.fromString("1"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TreepParser parser = new TreepParser(tokens);
        TreepParser.TreeContext tree = parser.tree();
        Object ast = new ASTBuilder().visit(tree);
        assertTrue(ast instanceof Number);
        assertEquals("1", ((Number) ast).value.toString());
    }

    @Test
    public void treeOneChild() {
        TreepLexer lexer = new TreepLexer(CharStreams.fromString("a b"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TreepParser parser = new TreepParser(tokens);
        TreepParser.TopLevelTreeContext tree = parser.topLevelTree();
        Object ast = new ASTBuilder().visit(tree);
        assertTrue(ast instanceof Node);
        Node root = (Node) ast;
        assertTrue(root.head instanceof Symbol);
        assertEquals("a", ((Symbol) root.head).name);
        assertEquals(1, root.children.size());
        Object child = root.children.get(0);
        assertTrue(child instanceof Symbol);
        assertEquals("b", ((Symbol) child).name);
    }

    @Test
    public void treeTwoChildren() {
        TreepLexer lexer = new TreepLexer(CharStreams.fromString("a b c"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TreepParser parser = new TreepParser(tokens);
        TreepParser.TopLevelTreeContext tree = parser.topLevelTree();
        Object ast = new ASTBuilder().visit(tree);
        assertTrue(ast instanceof Node);
        Node root = (Node) ast;
        assertTrue(root.head instanceof Symbol);
        assertEquals("a", ((Symbol) root.head).name);
        assertEquals(2, root.children.size());
        Object child = root.children.get(0);
        assertTrue(child instanceof Symbol);
        assertEquals("b", ((Symbol) child).name);
        child = root.children.get(1);
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
        Object ast = new ASTBuilder().visit(tree);
        assertTrue(ast instanceof Symbol);
        assertEquals("a", ((Symbol) ast).name);
    }

}
