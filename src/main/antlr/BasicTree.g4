grammar BasicTree;

@header {
    package treep.parser;

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
  (list | atom)+
  ({checkIndentation()}? INDENT{pushIndentation();} tree{popIndentation();})*;

list: LPAREN (atom | list | INDENT)* RPAREN; //TODO use modes to remove indent token
atom: SYMBOL | ANY;

SYMBOL: ('a'..'z' | 'A'..'Z') ('a'..'z' | 'A'..'Z' | '0'..'9')*; //TODO more characters
INDENT: ('\r'? '\n' | '\r') (' ' | '\t')*;
LPAREN: '(';
RPAREN: ')';
ANY: .+?;