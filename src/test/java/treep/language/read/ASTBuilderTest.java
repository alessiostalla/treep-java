package treep.language.read;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.Test;
import treep.language.Object;
import treep.language.datatypes.symbol.NameSpace;
import treep.language.datatypes.symbol.Symbol;
import treep.language.datatypes.tree.Cons;
import treep.language.datatypes.tree.Nothing;
import treep.language.datatypes.tree.Tree;
import treep.math.RealNumber;

import static org.junit.jupiter.api.Assertions.*;

public class ASTBuilderTest {

    @Test
    public void symbol() {
        TreepLexer lexer = new TreepLexer(CharStreams.fromString("a"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TreepParser parser = new TreepParser(tokens);
        TreepParser.TreeContext tree = parser.tree();
        Object ast = new ASTBuilder(new SimpleDatumParser(new NameSpace())).visit(tree);
        assertTrue(ast instanceof Symbol);
        assertEquals("a", ((Symbol) ast).name);
    }

    @Test
    public void number() {
        TreepLexer lexer = new TreepLexer(CharStreams.fromString("1"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TreepParser parser = new TreepParser(tokens);
        TreepParser.TreeContext tree = parser.tree();
        ASTBuilder astBuilder = new ASTBuilder(new SimpleDatumParser(new NameSpace()));
        Object ast = astBuilder.visit(tree);
        assertTrue(ast instanceof RealNumber);
        assertEquals("1", ((RealNumber) ast).value.toString());
    }

    @Test
    public void emptyTree() {
        TreepLexer lexer = new TreepLexer(CharStreams.fromString("()"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TreepParser parser = new TreepParser(tokens);
        TreepParser.TopLevelTreeContext tree = parser.topLevelTree();
        Object ast = new ASTBuilder(new SimpleDatumParser(new NameSpace())).visit(tree);
        assertEquals(Nothing.AT_ALL, ast);
    }

    @Test
    public void treeOneChild() {
        TreepLexer lexer = new TreepLexer(CharStreams.fromString("a b"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TreepParser parser = new TreepParser(tokens);
        TreepParser.TopLevelTreeContext tree = parser.topLevelTree();
        Object ast = new ASTBuilder(new SimpleDatumParser(new NameSpace())).visit(tree);
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
        Object ast = new ASTBuilder(new SimpleDatumParser(new NameSpace())).visit(tree);
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
    public void treeSubtree() {
        TreepLexer lexer = new TreepLexer(CharStreams.fromString("a\n\tb c d"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TreepParser parser = new TreepParser(tokens);
        TreepParser.TopLevelTreeContext tree = parser.topLevelTree();
        Object ast = new ASTBuilder(new SimpleDatumParser(new NameSpace())).visit(tree);
        assertTrue(ast instanceof Cons);
        Cons root = (Cons) ast;
        assertTrue(root.head instanceof Symbol);
        assertEquals("a", ((Symbol) root.head).name);
        assertEquals(1, root.tailSize());
        Object child = root.tail.getHead();
        assertTrue(child instanceof Tree);
        assertEquals(2, ((Tree) child).tailSize());
        assertEquals("b", ((Symbol) ((Tree) child).getHead()).name);
        assertEquals("c", ((Symbol) ((Tree) child).getTail().getHead()).name);
        assertEquals("d", ((Symbol) ((Tree) child).getTail().getTail().getHead()).name);
    }

    @Test
    public void treeSubtreeDedent() {
        TreepLexer lexer = new TreepLexer(CharStreams.fromString("a\n\tb c\n\t\td e\n\tf g"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TreepParser parser = new TreepParser(tokens);
        TreepParser.TopLevelTreeContext tree = parser.topLevelTree();
        Object ast = new ASTBuilder(new SimpleDatumParser(new NameSpace())).visit(tree);
        assertTrue(ast instanceof Cons);
        Cons root = (Cons) ast;
        assertTrue(root.head instanceof Symbol);
        assertEquals("a", ((Symbol) root.head).name);
        assertEquals(2, root.tailSize());
        Object child = root.tail.getHead();
        assertTrue(child instanceof Tree);
        assertEquals(2, ((Tree) child).tailSize());
        assertEquals("b", ((Symbol) ((Tree) child).getHead()).name);
        assertEquals("c", ((Symbol) ((Tree) child).getTail().getHead()).name);
    }

    @Test
    public void treeSubtreeDedentWithLineComment() {
        TreepLexer lexer = new TreepLexer(CharStreams.fromString("a\n\tb c\n\t\td e #comment\n\tf g"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TreepParser parser = new TreepParser(tokens);
        TreepParser.TopLevelTreeContext tree = parser.topLevelTree();
        Object ast = new ASTBuilder(new SimpleDatumParser(new NameSpace())).visit(tree);
        assertTrue(ast instanceof Cons);
        Cons root = (Cons) ast;
        assertTrue(root.head instanceof Symbol);
        assertEquals("a", ((Symbol) root.head).name);
        assertEquals(2, root.tailSize());
        Object child = root.tail.getHead();
        assertTrue(child instanceof Tree);
        assertEquals(2, ((Tree) child).tailSize());
        assertEquals("b", ((Symbol) ((Tree) child).getHead()).name);
        assertEquals("c", ((Symbol) ((Tree) child).getTail().getHead()).name);
    }

    @Test
    public void treeSubtreeDedentTemplateWithLineComment() {
        TreepLexer lexer = new TreepLexer(CharStreams.fromString("a\n\t`b c\n\t\td e #comment\n\tf g"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TreepParser parser = new TreepParser(tokens);
        TreepParser.TopLevelTreeContext tree = parser.topLevelTree();
        Object ast = new ASTBuilder(new SimpleDatumParser(new NameSpace())).visit(tree);
        assertTrue(ast instanceof Cons);
        Cons root = (Cons) ast;
        assertTrue(root.head instanceof Symbol);
        assertEquals("a", ((Symbol) root.head).name);
        assertEquals(2, root.tailSize());
        Object child = root.tail.getHead();
        assertTrue(child instanceof Tree);
        assertEquals(1, ((Tree) child).tailSize());
        assertEquals("template", ((Symbol) ((Tree) child).getHead()).name);
    }

    @Test
    public void treeSubtreeDedentWithSpaces() {
        TreepLexer lexer = new TreepLexer(CharStreams.fromString("a\n    b c\n        d e\n    f g"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TreepParser parser = new TreepParser(tokens);
        TreepParser.TopLevelTreeContext tree = parser.topLevelTree();
        Object ast = new ASTBuilder(new SimpleDatumParser(new NameSpace())).visit(tree);
        assertTrue(ast instanceof Cons);
        Cons root = (Cons) ast;
        assertTrue(root.head instanceof Symbol);
        assertEquals("a", ((Symbol) root.head).name);
        assertEquals(2, root.tailSize());
        Object child = root.tail.getHead();
        assertTrue(child instanceof Tree);
        assertEquals(2, ((Tree) child).tailSize());
        assertEquals("b", ((Symbol) ((Tree) child).getHead()).name);
        assertEquals("c", ((Symbol) ((Tree) child).getTail().getHead()).name);
    }

    @Test
    public void treeSubtreeDedentWithSpacesAndLineComment() {
        TreepLexer lexer = new TreepLexer(CharStreams.fromString("a\n    b c\n        d e #comment\n    f g"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TreepParser parser = new TreepParser(tokens);
        TreepParser.TopLevelTreeContext tree = parser.topLevelTree();
        Object ast = new ASTBuilder(new SimpleDatumParser(new NameSpace())).visit(tree);
        assertTrue(ast instanceof Cons);
        Cons root = (Cons) ast;
        assertTrue(root.head instanceof Symbol);
        assertEquals("a", ((Symbol) root.head).name);
        assertEquals(2, root.tailSize());
        Object child = root.tail.getHead();
        assertTrue(child instanceof Tree);
        assertEquals(2, ((Tree) child).tailSize());
        assertEquals("b", ((Symbol) ((Tree) child).getHead()).name);
        assertEquals("c", ((Symbol) ((Tree) child).getTail().getHead()).name);
    }

    @Test
    public void treeSubtreeDedentTemplateWithSpacesAndLineComment() {
        TreepLexer lexer = new TreepLexer(CharStreams.fromString("a\n    `b c\n        d e #comment\n    f g"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TreepParser parser = new TreepParser(tokens);
        TreepParser.TopLevelTreeContext tree = parser.topLevelTree();
        Object ast = new ASTBuilder(new SimpleDatumParser(new NameSpace())).visit(tree);
        assertTrue(ast instanceof Cons);
        Cons root = (Cons) ast;
        assertTrue(root.head instanceof Symbol);
        assertEquals("a", ((Symbol) root.head).name);
        assertEquals(2, root.tailSize());
        Object child = root.tail.getHead();
        assertTrue(child instanceof Tree);
        assertEquals(1, ((Tree) child).tailSize());
        assertEquals("template", ((Symbol) ((Tree) child).getHead()).name);
    }

    @Test
    public void treeWithInitialIndentation() {
        TreepLexer lexer = new TreepLexer(CharStreams.fromString("\na"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TreepParser parser = new TreepParser(tokens);
        TreepParser.TopLevelTreeContext topLevel = parser.topLevelTree();
        TreepParser.TreeContext tree = topLevel.tree();
        Object ast = new ASTBuilder(new SimpleDatumParser(new NameSpace())).visit(tree);
        assertTrue(ast instanceof Symbol);
        assertEquals("a", ((Symbol) ast).name);
    }

    @Test
    public void treeVaryingIndentationWithList() {
        TreepLexer lexer = new TreepLexer(CharStreams.fromString("a (b c\n\td e)\n\t\tf\n\tg h"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TreepParser parser = new TreepParser(tokens);
        TreepParser.TopLevelTreeContext tree = parser.topLevelTree();
        Object ast = new ASTBuilder(new SimpleDatumParser(new NameSpace())).visit(tree);
        assertTrue(ast instanceof Cons);
        Cons cons = (Cons) ast;
        assertEquals("a", ((Symbol) cons.getHead()).name);
        assertTrue(cons.getTail().getHead() instanceof Cons);
        assertTrue(((Cons) cons.getTail().getHead()).getHead() instanceof Symbol);
    }

    @Test
    public void templateQuoteInsert() {
        TreepLexer lexer = new TreepLexer(CharStreams.fromString("`',x"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TreepParser parser = new TreepParser(tokens);
        TreepParser.TopLevelTreeContext tree = parser.topLevelTree();
        Object ast = new ASTBuilder(new SimpleDatumParser(new NameSpace())).visit(tree);
        assertTrue(ast instanceof Cons);
        Cons cons = (Cons) ast;
        assertEquals("template", ((Symbol) cons.getHead()).name);
        Object head = cons.getTail().getHead();
        assertTrue(head instanceof Cons);
        cons = ((Cons) head);
        head = cons.getHead();
        assertTrue(head instanceof Symbol);
        assertEquals("quote", ((Symbol) head).name);
        head = cons.getTail().getHead();
        assertTrue(head instanceof Cons);
        cons = ((Cons) head);
        head = cons.getHead();
        assertTrue(head instanceof Symbol);
        assertEquals("insert", ((Symbol) head).name);
    }

}
