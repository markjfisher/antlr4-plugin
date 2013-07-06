// Example grammar from antlr4 definitive reference
grammar Expr;

prog:   stat+ ;

stat:   expr NEWLINE
    |   ID '=' expr NEWLINE
    |   NEWLINE
    ;

expr:   expr ('*'|'/') expr
    |   expr ('+'|'-') expr
    |   INT
    |   ID
    |   '(' expr ')'
    ;

ID  :  [a-zA-Z]+ ;
INT :  [0-9]+ ;
NEWLINE: '\r'? '\n' ;
WS  :  [ \t]+ -> skip ;
