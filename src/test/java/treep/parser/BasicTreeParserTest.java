package treep.parser;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BasicTreeParserTest {

    @Test
    public void symbolSimple() {
        BasicTreeLexer lexer = new BasicTreeLexer(CharStreams.fromString("a"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        BasicTreeParser.TreeContext tree = new BasicTreeParser(tokens).tree();
        assertNotNull(tree);
    }

    @Test
    public void treeOneLineOneChild() {
        BasicTreeLexer lexer = new BasicTreeLexer(CharStreams.fromString("a b"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        BasicTreeParser parser = new BasicTreeParser(tokens);
        BasicTreeParser.TopLevelTreeContext tree = parser.topLevelTree();
        assertEquals(0, parser.getNumberOfSyntaxErrors());
        assertEquals(1, tree.children.size());
    }

    @Test
    public void treeOneLineTwoChildren() {
        BasicTreeLexer lexer = new BasicTreeLexer(CharStreams.fromString("a b"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        BasicTreeParser parser = new BasicTreeParser(tokens);
        BasicTreeParser.TopLevelTreeContext tree = parser.topLevelTree();
        assertEquals(0, parser.getNumberOfSyntaxErrors());
        assertEquals(1, tree.children.size());
    }

    @Test
    public void treeNewline() {
        BasicTreeLexer lexer = new BasicTreeLexer(CharStreams.fromString("a\nb"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        BasicTreeParser parser = new BasicTreeParser(tokens);
        BasicTreeParser.TopLevelTreeContext tree = parser.topLevelTree();
        assertEquals(0, parser.getNumberOfSyntaxErrors());
        assertEquals(1, tree.children.size());

        tree = parser.topLevelTree();
        assertEquals(0, parser.getNumberOfSyntaxErrors());
        assertEquals(2, tree.children.size());
    }

    @Test
    public void treeVaryingIndentation() {
        BasicTreeLexer lexer = new BasicTreeLexer(CharStreams.fromString("a b c\n\td e\n\t\tf\n\tg h\ni"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        BasicTreeParser parser = new BasicTreeParser(tokens);
        BasicTreeParser.TopLevelTreeContext tree = parser.topLevelTree();
        assertNotNull(tree);
        assertEquals(0, parser.getNumberOfSyntaxErrors());
    }

    @Test
    public void treeVaryingIndentationHole() {
        BasicTreeLexer lexer = new BasicTreeLexer(CharStreams.fromString("a\n\tb\n\t\tc\n\t\t\td\n\te f"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        BasicTreeParser parser = new BasicTreeParser(tokens);
        BasicTreeParser.TopLevelTreeContext tree = parser.topLevelTree();
        assertNotNull(tree);
        assertEquals(0, parser.getNumberOfSyntaxErrors());
    }

    @Test
    public void listEmpty() {
        BasicTreeLexer lexer = new BasicTreeLexer(CharStreams.fromString("()"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        BasicTreeParser parser = new BasicTreeParser(tokens);
        BasicTreeParser.TopLevelTreeContext tree = parser.topLevelTree();
        assertNotNull(tree);
        assertEquals(0, parser.getNumberOfSyntaxErrors());
    }

    @Test
    public void listOne() {
        BasicTreeLexer lexer = new BasicTreeLexer(CharStreams.fromString("(one)"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        BasicTreeParser parser = new BasicTreeParser(tokens);
        BasicTreeParser.TopLevelTreeContext tree = parser.topLevelTree();
        assertNotNull(tree);
        assertEquals(0, parser.getNumberOfSyntaxErrors());
    }

    @Test
    public void listTwo() {
        BasicTreeLexer lexer = new BasicTreeLexer(CharStreams.fromString("(one two)"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        BasicTreeParser parser = new BasicTreeParser(tokens);
        BasicTreeParser.TopLevelTreeContext tree = parser.topLevelTree();
        assertNotNull(tree);
        assertEquals(0, parser.getNumberOfSyntaxErrors());
    }

    @Test
    public void listNewline() {
        BasicTreeLexer lexer = new BasicTreeLexer(CharStreams.fromString("(one\ntwo)"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        BasicTreeParser parser = new BasicTreeParser(tokens);
        BasicTreeParser.TopLevelTreeContext tree = parser.topLevelTree();
        assertNotNull(tree);
        assertEquals(0, parser.getNumberOfSyntaxErrors());
    }

    @Test
    public void treeVaryingIndentationWithList() {
        BasicTreeLexer lexer = new BasicTreeLexer(CharStreams.fromString("a (b c\n\td e)\n\t\tf\n\tg h"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        BasicTreeParser parser = new BasicTreeParser(tokens);
        BasicTreeParser.TopLevelTreeContext tree = parser.topLevelTree();
        assertNotNull(tree);
        assertEquals(0, parser.getNumberOfSyntaxErrors());
    }

}
