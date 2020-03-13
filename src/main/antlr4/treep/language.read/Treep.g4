grammar Treep;

@header {
    import java.util.*;
}

@parser::members {
    Deque<Integer> indentation = new ArrayDeque<>();

    int countIndentation(String spaces) {
        int count = 0;
        boolean newLine = false;
        for(int i = 0; i < spaces.length(); i++) {
            if(newLine) {
                if(spaces.charAt(i) == ' ') {
                    count++;
                } else if(spaces.charAt(i) == '\t') {
                    count += 4;
                }
            } else if(spaces.charAt(i) == '\n' || spaces.charAt(i) == '\r') {
                newLine = true;
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

list: LPAREN (node | INDENT)* RPAREN; //TODO use modes to remove indent token?

node: modifier+=(INSERT | QUOTE | SPLICE_INSERT | TEMPLATE)* (DATUM | list);

LPAREN: '(';
RPAREN: ')';
INDENT: (NEWLINE | LINE_COMMENT) (' ' | '\t')*;
QUOTE: '\'';
TEMPLATE: '`';
SPLICE_INSERT: ',@';
INSERT: ',';
DATUM: (~(' ' | '\t' | '\r' | '\n' | '(' | ')' | '\'' | '`' | ','))+; //TODO escaping (using modes?)
WS: (' ' | '\t') -> skip;

fragment LINE_COMMENT: '#' .*? NEWLINE;
fragment NEWLINE: ('\r'? '\n' | '\r');
