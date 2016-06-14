%{
  #include <stdlib.h>
  #include "ast.h"
  #include "function-parser.tab.h"

  void yyerror (const char *);
%}

%%
 /* Real number */
(([0-9]+(\.[0-9]*)?)|(\.[0-9]+)) {
   yylval = ast_terminal_acquire (NUMBER, atof (yytext), NULL);
   return REAL;
 }

 /* Identifier */
([a-z]|[A-Z])+([a-z]|[A-Z]|[0-9])* {
   yylval = ast_terminal_acquire (ID, 0, yytext);
   return IDENT;
 }

 /* Operators  */
[-+,*/^=()\n] {
   return *yytext;
 }

 /* Skip whitespace */
[ \t]

 /* Any other symbol is an error */
. yyerror("unexpected token.");

%%

int
yywrap (void)
{
  return 1;
}

     
  
