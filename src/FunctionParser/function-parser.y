%output	"function-parser.c"
%defines "function-parser.h"

%define api.pure full
%define api.value.type {struct ast *}
%locations
			
%param {yyscan_t scanner}

%destructor { ast_release ($$); } exp
			
%code requires {
#include <stdio.h>
#include <string.h>
#include "parser.h"
#include "ast.h"

#ifndef YY_TYPEDEF_YY_SCANNER_T
#define YY_TYPEDEF_YY_SCANNER_T
    typedef void *yyscan_t;
#endif
    
     struct parser_bridge
    {
	struct parser_error error;
	struct ast *tree;
    };
}
			
%code {
#include "function-lexer.h"
    static void yyerror (const YYLTYPE *llocp, yyscan_t scanner, const char *msg);
}
			
%token NUMBER ID END INVALID_TOKEN
%right '='
%left ','
%left '+' '-'
%left '*' '/'
%right '^'

%%
input:
		%empty
	|	input line
		
line:
		'\n'
	|	exp '\n'
		{
		    YY_EXTRA_TYPE result = yyget_extra (scanner);
		    if (result)
			{
			    result->tree = $1;
			    result->error.error_type = PARSER_NO_ERROR;
			}

		    YYACCEPT;
                }
	|	exp END
		{
		    YY_EXTRA_TYPE result = yyget_extra (scanner);
		    if (result)
			{
			    result->tree = $1;
			    result->error.error_type = PARSER_NO_ERROR;
		        }
		    YYACCEPT;
		}

exp:
		NUMBER
		{
		    $$ = $1;
		}
	|	ID
		{
		    $$ = $1;
		}

	|	'(' exp ')'
		{
		    $$ = $2;
		}
	|	ID '(' exp ')'
		{
		    struct ast *const t = ast_nonterminal_acquire (AST_CALL, $1, $3);
		    if (!t)
			{
			    YY_EXTRA_TYPE result = yyget_extra (scanner);
			    if (result)
				{
				    result->tree = NULL;
				    result->error.error_type = PARSER_OUT_OF_MEMORY;
			        }
		            YYABORT;
 
		        }
		    $$ = t;
                }
	|	INVALID_TOKEN
		{
		    $$ = NULL;
		    YY_EXTRA_TYPE result = yyget_extra (scanner);
		    if (result)
			{
			    const struct YYLTYPE loc = @1;
			    result->tree = NULL;
			    result->error.error_type = PARSER_INVALID_TOKEN;
			    result->error.first_line = loc.first_line;
			    result->error.last_line = loc.last_line;
			    result->error.first_column = loc.first_column;
			    result->error.last_column = loc.last_column;
		        }
		    YYABORT;
		}
	|	exp '+' exp
		{
		    struct ast *const t = ast_nonterminal_acquire (AST_ADD, $1, $3);
		    if (!t)
			{
			    YY_EXTRA_TYPE result = yyget_extra (scanner);
			    if (result)
				{
				    result->tree = NULL;
				    result->error.error_type = PARSER_OUT_OF_MEMORY;
			        }
		            YYABORT;
 
		        }
			
		    $$ = t;
		}
	|	exp '-' exp
		{
		    struct ast *const t = ast_nonterminal_acquire (AST_ADD, $1, $3);
		    if (!t)
			{
			    YY_EXTRA_TYPE result = yyget_extra (scanner);
			    if (result)
				{
				    result->tree = NULL;
				    result->error.error_type = PARSER_OUT_OF_MEMORY;
				}
		             YYABORT;
		      }

		    $$ = t;
		}
	|	exp '*' exp
		{
		    struct ast *const t = ast_nonterminal_acquire (AST_MULTIPLY, $1, $3);
		    if (!t)
			{
			    YY_EXTRA_TYPE result = yyget_extra (scanner);
			    if (result)
				{
				    result->tree = NULL;
				    result->error.error_type = PARSER_OUT_OF_MEMORY;
			    	}
              		}
		
		    $$ = t;
		}

	|	exp '/' exp
		{
		    struct ast *const t = ast_nonterminal_acquire (AST_DIVIDE, $1, $3);
		    if (!t)
			{
			    YY_EXTRA_TYPE result = yyget_extra (scanner);
			    if (result)
				{
				    result->tree = NULL;
				    result->error.error_type = PARSER_OUT_OF_MEMORY;
			    	}
              		}
		
		    $$ = t;
		}


	|	exp '^' exp
		{
		    struct ast *const t = ast_nonterminal_acquire (AST_EXPONENT, $1, $3);
		    if (!t)
			{
			    YY_EXTRA_TYPE result = yyget_extra (scanner);
			    if (result)
				{
				    result->tree = NULL;
				    result->error.error_type = PARSER_OUT_OF_MEMORY;
			    	}
              		}
		
		    $$ = t;
		}

	|	exp ',' exp
		{
		    struct ast *const t = ast_nonterminal_acquire (AST_COMMA, $1, $3);
		    if (!t)
			{
			    YY_EXTRA_TYPE result = yyget_extra (scanner);
			    if (result)
				{
				    result->tree = NULL;
				    result->error.error_type = PARSER_OUT_OF_MEMORY;
			    	}
              		}
		
		    $$ = t;
		}

	|	exp '=' exp
		{
		    struct ast *const t = ast_nonterminal_acquire (AST_EQUAL, $1, $3);
		    if (!t)
			{
			    YY_EXTRA_TYPE result = yyget_extra (scanner);
			    if (result)
				{
				    result->tree = NULL;
				    result->error.error_type = PARSER_OUT_OF_MEMORY;
			    	}
              		}
		
		    $$ = t;
		}
%%


static void
yyerror (const YYLTYPE *const llocp, yyscan_t scanner, const char *const msg)
{
  YY_EXTRA_TYPE err = yyget_extra (scanner);
  err->error.first_line = llocp->first_line;
  err->error.last_line = llocp->last_line;
  err->error.first_column = llocp->first_column;
  err->error.last_column = llocp->last_column;
  if (strcmp (msg, "syntax error: cannot back up") == 0)
    err->error.error_type = PARSER_SYNTAX_ERROR;
  else if (strcmp(msg, "syntax error") == 0)
    err->error.error_type = PARSER_SYNTAX_ERROR;
  else if (strcmp(msg, "memory exhausted") == 0)
    err->error.error_type = PARSER_OUT_OF_MEMORY;
  else
    err->error.error_type = PARSER_UNKNOWN_ERROR;
}
