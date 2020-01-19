grammar List;

list: LPAREN (node | INDENT)* RPAREN; //TODO use modes to remove indent token?

LPAREN: '(';
RPAREN: ')';
