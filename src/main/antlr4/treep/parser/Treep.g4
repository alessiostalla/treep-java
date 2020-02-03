grammar Treep;
import Number, Tree;

node: atom=(SYMBOL | NUMBER) | list;

SYMBOL: (~(' ' | '\t' | '\r' | '\n' | '(' | ')' | '0'..'9'))+; //TODO escaping (using modes?)
