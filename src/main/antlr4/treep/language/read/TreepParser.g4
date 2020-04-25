parser grammar TreepParser;

options { tokenVocab=TreepLexer; }

@header {
    import java.util.*;
}

@members {
    Deque<Integer> indentation = new ArrayDeque<>();

    int countIndentation(String spaces) {
        int count = 0;
        int start = Math.max(spaces.lastIndexOf('\r'), spaces.lastIndexOf('\n'));
        for(int i = start + 1; i < spaces.length(); i++) {
            if(spaces.charAt(i) == ' ') {
                count++;
            } else if(spaces.charAt(i) == '\t') {
                count += 4;
            }
        }
        return count;
    }

    boolean checkIndentation() {
        Token t = _input.LT(1);
        if(t.getType() == INDENT) {
            int prevIndent = indentation.isEmpty() ? 0 : indentation.peek();
            int currIndent = countIndentation(t.getText());
            if(currIndent > prevIndent) {
                return true;
            }
        }
        return false;
    }

    void pushIndentation() {
        indentation.push(countIndentation(_input.LT(-1).getText()));
    }

    void popIndentation() {
        indentation.pop();
    }

    void resetIndentation() {
        indentation.clear();
    }
}

topLevelTree: (INDENT* INDENT{pushIndentation();})? tree{resetIndentation();};

tree:
  modifier+=(INSERT | QUOTE | SPLICE_INSERT | TEMPLATE)* node node*
  ({checkIndentation()}? INDENT{pushIndentation();} tree{popIndentation();})*;

node: modifier+=(INSERT | QUOTE | SPLICE_INSERT | TEMPLATE)* (atom | list);

atom: DATUM | DATUM_BEGIN (DATUM | INTERPOLATION_START node INTERPOLATION_END)* DATUM_END;

list: LPAREN (node | INDENT)* RPAREN; //TODO use modes to remove indent token?
