grammar ArithmeticsEvaluator;

fragment DIGIT: '0'..'9';
fragment LETTER: 'a'..'z';
fragment CAPITAL_LETTER: 'A'..'Z';
fragment UNDERSCORE: '_';

ID: (LETTER | UNDERSCORE | CAPITAL_LETTER) (LETTER | UNDERSCORE | DIGIT | CAPITAL_LETTER)*;
NUM: (DIGIT | '.')+ ([eE] [-+]? DIGIT+)?;
MUL: '*';
DIV: '/';
PLUS: '+';
MINUS: '-';
POW: '^';
COMMA: ',';
LPAR: '(';
RPAR: ')';
WS: [ \n\t\r]+ -> skip;

num
    : NUM
    ;

singular
    : ID
    ;

unaryFunction
    : ID LPAR subSumChain RPAR
    ;

binaryFunction
    : ID LPAR subSumChain COMMA subSumChain RPAR
    ;

term
    : num
    | singular
    | unaryFunction
    | binaryFunction
    | MINUS term
    | LPAR subSumChain RPAR
    ;

powChain
    : term (POW term)*
    ;

divMulChain
    : powChain ((DIV | MUL) powChain)*
    ;

subSumChain
    : divMulChain ((PLUS | MINUS) divMulChain)*
    ;

rootParser
    : subSumChain EOF
    ;
