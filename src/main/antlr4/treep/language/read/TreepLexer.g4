lexer grammar TreepLexer;

tokens {
    DATUM_BEGIN, DATUM_END
}

LPAREN: '(';
RPAREN: ')';
INDENT: EMPTY_LINE+ (' ' | '\t')*;
QUOTE: '\'';
TEMPLATE: '`';
SPLICE_INSERT: ',@';
INSERT: ',';
STRING_BEGIN: '"' -> type(DATUM_BEGIN), pushMode(string);
INTERPOLATION_START: '{' -> pushMode(DEFAULT_MODE);
INTERPOLATION_END: '}' -> popMode;
DATUM: (~(' ' | '\t' | '\r' | '\n' | '(' | ')' | '\'' | '`' | ',' | '"' | '{' | '}'))+;
WS: (' ' | '\t') -> channel(HIDDEN);

fragment EMPTY_LINE: ((' ' | '\t' | NEWLINE)* (NEWLINE | LINE_COMMENT)+)+;
fragment LINE_COMMENT: '#' .*? NEWLINE+;
fragment NEWLINE: '\r' |'\n';

mode string;
STRING_END: '"' -> type(DATUM_END), popMode;
STRING_INTERPOLATION_START: '${' -> type(INTERPOLATION_START), pushMode(DEFAULT_MODE);
STRING_ESCAPE_SINGLE_CHAR: '\\' -> type(DATUM), pushMode(stringEscape);
STRING_CONTENTS: (~('"' | '\\' | '$'))+ -> type(DATUM);
DOLLAR_NO_INTERPOLATION: '$' -> type(DATUM);

mode stringEscape;
DATUM_ESCAPE_CHAR: . -> type(DATUM), popMode;
