%{
  #include <stdio.h>
  #include <string.h>
  #include <errno.h>
  #include "ast.h"

  int yylex (void);
  void yyerror (const char *);
  void ast_print (const struct ast *);
%}

%define api.value.type {struct ast *}
%destructor { ast_release ($$); } exp
%token REAL IDENT
%right '='
%left ','
%left '+' '-'
%left '*' '/'
%right '^'

%%
input:		%empty
	|	input line
	;

line:		'\n'
	        |	exp '\n' { ast_print ($1); putchar('\n'); ast_release ($1); }
	;

exp:		REAL        { $$ = $1; }
	|	IDENT       { $$ = $1; }
	|	'(' exp ')' { $$ = $2; }
	|	exp '+' exp
		{
		    struct ast *t = ast_nonterminal_acquire (ADD, $1, $3);
		    if (!t)
			    yyerror (strerror (errno));

		    $$ = t;
		}
	|	exp '-' exp
		{
		    struct ast *t = ast_nonterminal_acquire (ADD, $1, $3);
		    if (!t)
			yyerror (strerror (errno));

		    $$ = t;
		}
	|	exp '*' exp
		{
		    struct ast *t = ast_nonterminal_acquire (MULTIPLY, $1, $3);
		    if (!t)
			yyerror (strerror (errno));

		    $$ = t;
		}
	|	exp '/' exp
		{
		    struct ast *t = ast_nonterminal_acquire(DIVIDE, $1, $3);
		    if (!t)
			yyerror( strerror (errno));

		    $$ = t;
		}
	|	exp '^' exp
		{
		    struct ast *t = ast_nonterminal_acquire (EXPONENT, $1, $3);
		    if (!t)
			yyerror (strerror (errno));

		    $$ = t;
		}
	|	IDENT '(' exp ')'
		{
		    struct ast *t = ast_nonterminal_acquire (CALL, $1, $3);
		    if (!t)
			yyerror (strerror (errno));

		    $$ = t;
		}
	|	exp ',' exp
		{
		    struct ast *t = ast_nonterminal_acquire (COMMA, $1, $3);
		    if (!t)
			yyerror (strerror (errno));

		    $$ = t;
		}
	|	exp '=' exp
		{
		    struct ast *t = ast_nonterminal_acquire (EQUAL, $1, $3);
		    if (!t)
			yyerror (strerror (errno));

		    $$ = t;
		}
%%

void
yyerror (const char *const msg)
{
  fprintf (stderr, "error: %s\n", msg);
}

void
ast_print (const struct ast *const t)
{
  if (t->operator != NONE)
    {
      if (t->operator != CALL)
	printf ("(%c ", t->operator);
      else
	printf("(call ");
      
      ast_print (t->left);
      putchar (' ');
      ast_print (t->right);
      putchar(')');
    }
  else
    {
      if (t->operand.type == NUMBER)
	printf ("%f", t->operand.value);
      else
	printf ("%s", t->operand.id);
    }
}
	  
      
int
main (void)
{
  yyparse();

  return 0;
}
