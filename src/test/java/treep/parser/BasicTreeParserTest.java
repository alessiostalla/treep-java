package treep.parser;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BasicTreeParserTest {

    @Test
    public void minimalTest() {
        BasicTreeLexer lexer = new BasicTreeLexer(CharStreams.fromString("a"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        BasicTreeParser.TreeContext tree = new BasicTreeParser(tokens).tree();
        assertNotNull(tree);
    }

    @Test
    public void treeNewlineTest() {
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
    public void treeTest() {
        BasicTreeLexer lexer = new BasicTreeLexer(CharStreams.fromString("a b c\n\td e\n\t\tf\n\tg h\ni"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        BasicTreeParser parser = new BasicTreeParser(tokens);
        BasicTreeParser.TopLevelTreeContext tree = parser.topLevelTree();
        assertNotNull(tree);
        assertEquals(0, parser.getNumberOfSyntaxErrors());
    }

}
