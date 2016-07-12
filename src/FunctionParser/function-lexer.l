%option outfile="function-lexer.c" header-file="function-lexer.h"
%option reentrant bison-bridge bison-locations noyywrap
%option extra-type="struct parser_bridge *"


%{
  #include <stdlib.h>
  #include "ast.h"
  #include "function-parser.h"
%}


%%
 /* Real number */
(([[:digit:]]+(\.[[:digit:]]*)?)|(\.[[:digit:]]+)) {
   *yylval = ast_terminal_acquire (AST_NUMBER, atof (yytext), NULL);
   return NUMBER;
 }

 /* Identifier */
[[:alpha:]]+[[:alnum:]]* {
   *yylval = ast_terminal_acquire (AST_ID, 0, yytext);
   return ID;
 }

 /* Operators  */
[-+,*/^=()\n] {
   return *yytext;
 }

 /* Skip whitespace */
[ \t]

 /* Any other symbol is an error */
. return INVALID_TOKEN;

 /* Deal with EOF */
<<EOF>> return END;
%%
