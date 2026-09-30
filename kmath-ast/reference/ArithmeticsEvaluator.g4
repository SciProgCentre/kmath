grammar ArithmeticsEvaluator;

fragment DIGIT: '0'..'9';
fragment LETTER: 'a'..'z';
fragment CAPITAL_LETTER: 'A'..'Z';
fragment UNDERSCORE: '_';

ID: (LETTER | UNDERSCORE | CAPITAL_LETTER) (LETTER | UNDERSCORE | DIGIT | CAPITAL_LETTER)*;
STRING: '"' ('\\"' | ~["\r\n])* '"' | '\'' ('\\\'' | ~['\r\n])* '\'';
NUM: (DIGIT | '.')+ ([eE] [-+]? DIGIT+)?;
MUL: '*';
DIV: '/';
PLUS: '+';
MINUS: '-';
POW: '^';
COMMA: ',';
EQ: '=';
LPAR: '(';
RPAR: ')';
WS: [ \n\t\r]+ -> skip;

num
    : NUM
    ;

string
    : STRING
    ;

singular
    : ID
    ;

namedArgument
    : ID EQ subSumChain
    ;

functionCall
    : ID LPAR namedArgument (COMMA namedArgument)* RPAR
    ;

unaryFunction
    : ID LPAR subSumChain RPAR
    ;

binaryFunction
    : ID LPAR subSumChain COMMA subSumChain RPAR
    ;

term
    : num
    | string
    | functionCall
    | unaryFunction
    | binaryFunction
    | singular
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
