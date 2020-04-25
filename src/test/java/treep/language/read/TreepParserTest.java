package treep.language.read;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.TerminalNode;
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
        assertEquals(TreepLexer.DATUM, tree.getStart().getType());
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
        assertEquals(TreepLexer.DATUM, tree.getStart().getType());
    }

    @Test
    public void stringSimple() {
        TreepLexer lexer = new TreepLexer(CharStreams.fromString("\"aaa\""));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TreepParser parser = new TreepParser(tokens);
        TreepParser.TreeContext tree = parser.tree();
        assertNotNull(tree);
        assertEquals(0, parser.getNumberOfSyntaxErrors());
        assertEquals(TreepLexer.DATUM_BEGIN, tree.getStart().getType());
        assertEquals(TreepLexer.DATUM_END, tree.getStop().getType());
        assertEquals("\"aaa\"", tree.getText());
    }

    @Test
    public void stringInterpolatedSimple() {
        TreepLexer lexer = new TreepLexer(CharStreams.fromString("\"aaa${42}bbb\""));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TreepParser parser = new TreepParser(tokens);
        TreepParser.TreeContext tree = parser.tree();
        assertNotNull(tree);
        assertEquals(0, parser.getNumberOfSyntaxErrors());
        assertEquals(TreepLexer.DATUM_BEGIN, tree.getStart().getType());
        assertEquals(TreepLexer.DATUM_END, tree.getStop().getType());
        assertEquals("\"aaa${42}bbb\"", tree.getText());
    }


    @Test
    public void treeEmpty() {
        TreepLexer lexer = new TreepLexer(CharStreams.fromString("()"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TreepParser parser = new TreepParser(tokens);
        TreepParser.TreeContext tree = parser.tree();
        assertEquals(0, parser.getNumberOfSyntaxErrors());
        assertEquals(1, tree.children.size());
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
    public void treeVaryingIndentationTabs() {
        TreepLexer lexer = new TreepLexer(CharStreams.fromString("a b c\n\td e\n\t\tf\n\tg h\ni"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TreepParser parser = new TreepParser(tokens);
        TreepParser.TopLevelTreeContext tree = parser.topLevelTree();
        assertNotNull(tree);
        assertEquals(0, parser.getNumberOfSyntaxErrors());
        assertEquals(3, tree.tree().node().size());
        assertEquals(2, tree.tree().tree().size());
    }

    @Test
    public void treeVaryingIndentationSpaces() {
        TreepLexer lexer = new TreepLexer(CharStreams.fromString("a b c\n  d e\n    f\n  g h\ni"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TreepParser parser = new TreepParser(tokens);
        TreepParser.TopLevelTreeContext tree = parser.topLevelTree();
        assertNotNull(tree);
        assertEquals(0, parser.getNumberOfSyntaxErrors());
        assertEquals(3, tree.tree().node().size());
        assertEquals(2, tree.tree().tree().size());
    }

    @Test
    public void treeVaryingIndentationHole() {
        TreepLexer lexer = new TreepLexer(CharStreams.fromString("a\n\tb\n\t\tc\n\t\t\td\n\te f"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TreepParser parser = new TreepParser(tokens);
        TreepParser.TopLevelTreeContext tree = parser.topLevelTree();
        assertNotNull(tree);
        assertEquals(0, parser.getNumberOfSyntaxErrors());
        assertEquals(1, tree.tree().node().size());
        assertEquals(2, tree.tree().tree().size());
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
        TreepParser.ListContext list = parser.list();
        assertEquals(0, parser.getNumberOfSyntaxErrors());
        assertNotNull(list);

        lexer = new TreepLexer(CharStreams.fromString("(one\ntwo)"));
        tokens = new CommonTokenStream(lexer);
        parser = new TreepParser(tokens);
        TreepParser.TopLevelTreeContext tree = parser.topLevelTree();
        assertEquals(0, parser.getNumberOfSyntaxErrors());
        assertNotNull(tree);
    }

    @Test
    public void treeVaryingIndentationTabsWithList() {
        TreepLexer lexer = new TreepLexer(CharStreams.fromString("a (b c\n\td e)\n\t\tf\n\tg h"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TreepParser parser = new TreepParser(tokens);
        TreepParser.TopLevelTreeContext tree = parser.topLevelTree();
        assertNotNull(tree);
        assertEquals(0, parser.getNumberOfSyntaxErrors());
        assertEquals(2, tree.tree().node().size());
        assertEquals(2, tree.tree().tree().size());
    }

    @Test
    public void treeVaryingIndentationSpacesWithList() {
        TreepLexer lexer = new TreepLexer(CharStreams.fromString("a (b c\n    d e)\n        f\n  g h"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TreepParser parser = new TreepParser(tokens);
        TreepParser.TopLevelTreeContext tree = parser.topLevelTree();
        assertNotNull(tree);
        assertEquals(0, parser.getNumberOfSyntaxErrors());
        assertEquals(2, tree.tree().node().size());
        assertEquals(2, tree.tree().tree().size());
    }

    @Test
    public void templateSimple() {
        TreepLexer lexer = new TreepLexer(CharStreams.fromString("`a"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TreepParser parser = new TreepParser(tokens);
        TreepParser.TopLevelTreeContext tree = parser.topLevelTree();
        assertNotNull(tree);
        assertEquals(0, parser.getNumberOfSyntaxErrors());
    }

}
