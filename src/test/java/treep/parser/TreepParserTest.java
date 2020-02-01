package treep.parser;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TreepParserTest {

    @Test
    public void symbolSimple() {
        TreepLexer lexer = new TreepLexer(CharStreams.fromString("a"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TreepParser parser = new TreepParser(tokens);
        TreepParser.TreeContext tree = parser.tree();
        assertNotNull(tree);
        assertEquals(0, parser.getNumberOfSyntaxErrors());
        assertEquals(tree.getStart(), tree.getStop());
        assertEquals(TreepLexer.SYMBOL, tree.getStart().getType());
    }

    @Test
    public void numberSimple() {
        TreepLexer lexer = new TreepLexer(CharStreams.fromString("1"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TreepParser parser = new TreepParser(tokens);
        TreepParser.TreeContext tree = parser.tree();
        assertNotNull(tree);
        assertEquals(0, parser.getNumberOfSyntaxErrors());
        assertEquals(tree.getStart(), tree.getStop());
        assertEquals(TreepLexer.NUMBER, tree.getStart().getType());
    }

    @Test
    public void treeOneLineOneChild() {
        TreepLexer lexer = new TreepLexer(CharStreams.fromString("a b"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TreepParser parser = new TreepParser(tokens);
        TreepParser.TreeContext tree = parser.tree();
        assertEquals(0, parser.getNumberOfSyntaxErrors());
        assertEquals(2, tree.children.size());
    }

    @Test
    public void treeOneLineTwoChildren() {
        TreepLexer lexer = new TreepLexer(CharStreams.fromString("a b c"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TreepParser parser = new TreepParser(tokens);
        TreepParser.TreeContext tree = parser.tree();
        assertEquals(0, parser.getNumberOfSyntaxErrors());
        assertEquals(3, tree.children.size());
    }

    @Test
    public void treeNewline() {
        TreepLexer lexer = new TreepLexer(CharStreams.fromString("a\nb"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TreepParser parser = new TreepParser(tokens);
        TreepParser.TopLevelTreeContext tree = parser.topLevelTree();
        assertEquals(0, parser.getNumberOfSyntaxErrors());
        assertEquals(1, tree.children.size());

        tree = parser.topLevelTree();
        assertEquals(0, parser.getNumberOfSyntaxErrors());
        assertEquals(2, tree.children.size()); //2 because one is the newline and the other is the actual tree
    }

    @Test
    public void treeVaryingIndentation() {
        TreepLexer lexer = new TreepLexer(CharStreams.fromString("a b c\n\td e\n\t\tf\n\tg h\ni"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TreepParser parser = new TreepParser(tokens);
        TreepParser.TopLevelTreeContext tree = parser.topLevelTree();
        assertNotNull(tree);
        assertEquals(0, parser.getNumberOfSyntaxErrors());
    }

    @Test
    public void treeVaryingIndentationHole() {
        TreepLexer lexer = new TreepLexer(CharStreams.fromString("a\n\tb\n\t\tc\n\t\t\td\n\te f"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TreepParser parser = new TreepParser(tokens);
        TreepParser.TopLevelTreeContext tree = parser.topLevelTree();
        assertNotNull(tree);
        assertEquals(0, parser.getNumberOfSyntaxErrors());
    }

    @Test
    public void listEmpty() {
        TreepLexer lexer = new TreepLexer(CharStreams.fromString("()"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TreepParser parser = new TreepParser(tokens);
        TreepParser.TopLevelTreeContext tree = parser.topLevelTree();
        assertNotNull(tree);
        assertEquals(0, parser.getNumberOfSyntaxErrors());
    }

    @Test
    public void listOne() {
        TreepLexer lexer = new TreepLexer(CharStreams.fromString("(one)"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TreepParser parser = new TreepParser(tokens);
        TreepParser.TopLevelTreeContext tree = parser.topLevelTree();
        assertNotNull(tree);
        assertEquals(0, parser.getNumberOfSyntaxErrors());
    }

    @Test
    public void listTwo() {
        TreepLexer lexer = new TreepLexer(CharStreams.fromString("(one two)"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TreepParser parser = new TreepParser(tokens);
        TreepParser.TopLevelTreeContext tree = parser.topLevelTree();
        assertNotNull(tree);
        assertEquals(0, parser.getNumberOfSyntaxErrors());
    }

    @Test
    public void listNewline() {
        TreepLexer lexer = new TreepLexer(CharStreams.fromString("(one\ntwo)"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TreepParser parser = new TreepParser(tokens);
        TreepParser.TopLevelTreeContext tree = parser.topLevelTree();
        assertNotNull(tree);
        assertEquals(0, parser.getNumberOfSyntaxErrors());
    }

    @Test
    public void treeVaryingIndentationWithList() {
        TreepLexer lexer = new TreepLexer(CharStreams.fromString("a (b c\n\td e)\n\t\tf\n\tg h"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TreepParser parser = new TreepParser(tokens);
        TreepParser.TopLevelTreeContext tree = parser.topLevelTree();
        assertNotNull(tree);
        assertEquals(0, parser.getNumberOfSyntaxErrors());
    }

}
