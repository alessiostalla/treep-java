grammar Tree;

@header {
    import java.util.*;
}

@parser::members {
    Deque<Integer> indentation = new ArrayDeque<>();

    int countIndentation(String spaces) {
        int count = 0;
        for(int i = 0; i < spaces.length(); i++) {
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

topLevelTree: INDENT* (INDENT{pushIndentation();})? tree{resetIndentation();};

tree:
  node node*
  ({checkIndentation()}? INDENT{pushIndentation();} tree{popIndentation();})*;

node: SYMBOL;

INDENT: ('\r'? '\n' | '\r') (' ' | '\t')*;
SYMBOL: (~(' ' | '\t' | '\r' | '\n'))+; //TODO escaping (using modes?)
WS: (' ' | '\t') -> skip; //TODO redirect to another channel?
