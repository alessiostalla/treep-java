package treep.parser;

import org.antlr.v4.Tool;
import org.antlr.v4.tool.Grammar;

public class AntlrToolTest {

    public static void main(String[] args) {
        Tool antlr = new Tool();
        Grammar grammar = antlr.loadGrammar("/home/alessio/projects/treep/src/main/antlr/BasicTree.g4");
        antlr.process(grammar, true);
    }

}
