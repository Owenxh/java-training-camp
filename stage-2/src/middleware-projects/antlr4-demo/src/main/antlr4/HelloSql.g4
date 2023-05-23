grammar HelloSql;

options { caseInsensitive = true; }

SPACE:                               [ \t\r\n]+    -> channel(HIDDEN);
COMMENT_INPUT:                       '/*' .*? '*/' -> channel(HIDDEN);
LINE_COMMENT:                        (
                                       ('--' [ \t]* | '#') ~[\r\n]* ('\r'? '\n' | EOF)
                                       | '--' ('\r'? '\n' | EOF)
                                     ) -> channel(HIDDEN);
// Keywords
AND:                                 'AND';
AS:                                  'AS';
ASC:                                 'ASC';
BEFORE:                              'BEFORE';
BETWEEN:                             'BETWEEN';
BY:                                  'BY';
COLUMN:                              'COLUMN';
DEFAULT:                             'DEFAULT';
DESC:                                'DESC';
DISTINCT:                            'DISTINCT';
ESCAPE:                              'ESCAPE';
FALSE:                               'FALSE';
FROM:                                'FROM';
GROUP:                               'GROUP';
IF:                                  'IF';
IN:                                  'IN';
IS:                                  'IS';
JOIN:                                'JOIN';
LEFT:                                'LEFT';
LIKE:                                'LIKE';
LIMIT:                               'LIMIT';
MATCH:                               'MATCH';
NOT:                                 'NOT';
NULL_LITERAL:                        'NULL';
NUMBER:                              'NUMBER';
OFFSET:                              'OFFSET';
ON:                                  'ON';
OR:                                  'OR';
ORDER:                               'ORDER';
OUT:                                 'OUT';
REGEXP:                              'REGEXP';
RETURN:                              'RETURN';
SELECT:                              'SELECT';
TABLE:                               'TABLE';
TO:                                  'TO';
TRUE:                                'TRUE';
UNION:                               'UNION';
UNKNOWN:                             'UNKNOWN';
WHERE:                               'WHERE';
XOR:                                 'XOR';

// Operators Operators. Assigns
VAR_ASSIGN:                          ':=';
PLUS_ASSIGN:                         '+=';
MINUS_ASSIGN:                        '-=';
MULT_ASSIGN:                         '*=';
DIV_ASSIGN:                          '/=';
MOD_ASSIGN:                          '%=';
AND_ASSIGN:                          '&=';
XOR_ASSIGN:                          '^=';
OR_ASSIGN:                           '|=';

// Constructors symbols
DOT:                                 '.';
LR_BRACKET:                          '(';
RR_BRACKET:                          ')';
COMMA:                               ',';
SEMI:                                ';';
AT_SIGN:                             '@';
ZERO_DECIMAL:                        '0';
ONE_DECIMAL:                         '1';
TWO_DECIMAL:                         '2';
SINGLE_QUOTE_SYMB:                   '\'';
DOUBLE_QUOTE_SYMB:                   '"';
REVERSE_QUOTE_SYMB:                  '`';
COLON_SYMB:                          ':';


// Operators. Arithmetics
STAR:                                '*';
DIVIDE:                              '/';
MODULE:                              '%';
PLUS:                                '+';
MINUS:                               '-';
DIV:                                 'DIV';
MOD:                                 'MOD';

// Operators. Comparation
EQUAL_SYMBOL:                        '=';
GREATER_SYMBOL:                      '>';
LESS_SYMBOL:                         '<';
EXCLAMATION_SYMBOL:                  '!';

fragment EXPONENT_NUM_PART:          'E' [-+]? DEC_DIGIT+;
fragment ID_LITERAL:                 [A-Z_$0-9\u0080-\uFFFF]*?[A-Z_$\u0080-\uFFFF]+?[A-Z_$0-9\u0080-\uFFFF]*;
fragment DQUOTA_STRING:              '"' ( '\\'. | '""' | ~('"'| '\\') )* '"';
fragment SQUOTA_STRING:              '\'' ('\\'. | '\'\'' | ~('\'' | '\\'))* '\'';
fragment BQUOTA_STRING:              '`' ( '\\'. | '``' | ~('`'|'\\'))* '`';
fragment HEX_DIGIT:                  [0-9A-F];
fragment DEC_DIGIT:                  [0-9];
fragment BIT_STRING_L:               'B' '\'' [01]+ '\'';

BIT_STRING: BIT_STRING_L;
DOT_ID:     '.' ID_LITERAL;
ID:         ID_LITERAL;
LOCAL_ID:
	'@' (
		[A-Z0-9._$]+
		| SQUOTA_STRING
		| DQUOTA_STRING
		| BQUOTA_STRING
	);
GLOBAL_ID: '@' '@' ( [A-Z0-9._$]+ | BQUOTA_STRING);

DECIMAL_LITERAL:
    DEC_DIGIT+
    ;

REAL_LITERAL: (DEC_DIGIT+)? '.' DEC_DIGIT+
	| DEC_DIGIT+ '.' EXPONENT_NUM_PART
	| (DEC_DIGIT+)? '.' (DEC_DIGIT+ EXPONENT_NUM_PART)
	| DEC_DIGIT+ EXPONENT_NUM_PART;

STRING_LITERAL:
    DQUOTA_STRING
    | SQUOTA_STRING
    | BQUOTA_STRING
    ;

NULL_SPEC_LITERAL: '\\' 'N';

simpleId:           ID;
uid:                simpleId;

decimalLiteral:
	DECIMAL_LITERAL
	| ZERO_DECIMAL
	| ONE_DECIMAL
	| TWO_DECIMAL
	| REAL_LITERAL;

dottedId: DOT_ID | '.' uid;

// Operators
logicalOperator:    AND | '&' '&' | XOR | OR | '|' '|';
bitOperator:        '<' '<' | '>' '>' | '&' | '^' | '|';
mathOperator:       '*' | '/' | '%' | DIV | MOD | '+' | '-';
comparisonOperator:
	'='
	| '>'
	| '<'
	| '<' '='
	| '>' '='
	| '<' '>'
	| '!' '='
	| '<' '=' '>';

stringLiteral:
    STRING_LITERAL+
	;

booleanLiteral: TRUE | FALSE;

constant:
	stringLiteral
	| decimalLiteral
	| '-' decimalLiteral
	| booleanLiteral
	| REAL_LITERAL
	| BIT_STRING
	| NOT? nullLiteral = (NULL_LITERAL | NULL_SPEC_LITERAL)
	;

fullId:             uid (DOT_ID | '.' uid)?;
tableName:          fullId;
fullColumnName:
	uid (dottedId dottedId?)?
	| .? dottedId dottedId?
	;

// Top Level Description
root: sqlStatements? (MINUS MINUS)? EOF;

sqlStatements: (
		sqlStatement (MINUS MINUS)? SEMI?
		| emptyStatement_
	)* (sqlStatement ((MINUS MINUS)? SEMI)? | emptyStatement_);

sqlStatement
  : dmlStatement
  ;

emptyStatement_: SEMI;

dmlStatement
  : selectStatement
  ;

//  Select Statement's Details

queryExpression:
	'(' querySpecification ')'
	| '(' queryExpression ')';

selectStatement
    : querySpecification    #simpleSelect
    ;

querySpecification:
	SELECT selectElements fromClause groupByClause? orderByClause? limitClause?
	;

selectElements: (star = '*' | selectElement) (',' selectElement)*;

selectElement:
	fullId '.' '*'									              # selectStarElement
	| fullColumnName (AS? uid)?						              # selectColumnElement;

fromClause
    : (FROM tableSources)?
      (WHERE whereExpr=expression)?
    ;

groupByClause
    :  GROUP BY
        groupByItem (',' groupByItem)*
    ;

groupByItem
    : expression order=(ASC | DESC)?
    ;

limitClause
    : LIMIT
    (
      (offset=limitClauseAtom ',')? limit=limitClauseAtom
      | limit=limitClauseAtom OFFSET offset=limitClauseAtom
    )
    ;

limitClauseAtom: decimalLiteral | mysqlVariable | simpleId;

mysqlVariable: LOCAL_ID | GLOBAL_ID;

orderByClause:
	ORDER BY orderByExpression (',' orderByExpression)*;

orderByExpression: expression order = (ASC | DESC)?;

tableSources: tableSource (',' tableSource)*;

tableSource:
	tableSourceItem			                                    # tableSourceBase
	;

tableSourceItem:
	tableName (AS? alias = uid)?                                # atomTableItem
	| '(' tableSources ')'	                                    # tableSourcesItem;

// Simplified approach for expression

expressions: expression (',' expression)*;

expression:
	notOperator = (NOT | '!') expression						# notExpression
	| expression logicalOperator expression						# logicalExpression
	| predicate IS NOT? testValue = (TRUE | FALSE | UNKNOWN)	# isExpression
	| predicate													# predicateExpression;

predicate:
	predicate NOT? IN '(' (selectStatement | expressions) ')'	# inPredicate
	| left = predicate comparisonOperator right = predicate		# binaryComparisonPredicate
	| predicate NOT? BETWEEN predicate AND predicate			# betweenPredicate
	| predicate NOT? LIKE predicate (ESCAPE STRING_LITERAL)?	# likePredicate
	| (LOCAL_ID VAR_ASSIGN)? expressionAtom						# expressionAtomPredicate;

// Add in ASTVisitor nullNotnull in constant
expressionAtom:
	constant													# constantExpressionAtom
	| fullColumnName											# fullColumnNameExpressionAtom
	| mysqlVariable												# mysqlVariableExpressionAtom
	| '(' selectStatement ')'									# subqueryExpressionAtom
	| left = expressionAtom bitOperator right = expressionAtom	# bitExpressionAtom
	| left = expressionAtom mathOperator right = expressionAtom	# mathExpressionAtom;





