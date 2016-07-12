/* parse.c - contains functions for converting expressions into syntax trees */
#include <stdio.h>
#include <stdlib.h>
#include <errno.h>
#include "parser.h"
#include "function-parser.h"
#include "function-lexer.h"

/* Takes a string expr and returns an array of pointers to struct asts
   representing the expressions in expr. The number of individual expressions 
   found in expr is stored in *ntrees. Note that if either expr or ntrees is
   NULL, the function returns immediately, returning NULL.

   In the case of any error, *ntrees will be set to 0 and the function will
   return NULL. No memory will be leaked. If error is not NULL, it can be
   inspected to determine the error.

   If an invalid token is encountered while parsing, error->error_type will be
   set to PARSER_INVALID_TOKEN, with the corresponding location fields filled in.
   If a syntax error is encounted while parsing, error->error_type will be set
   to PARSER_SYNTAX_ERROR, witht the corresponding location fields filled in.
   If the parser ran out of memory while parsing, error->error_type will be set
   to PARSER_OUT_OF_MEMORY.
   If any other error is encountered while parsing, error->error_type will be set
   to PARSER_UNKNOWN_ERROR.

   Note that the value of errno can be inspected to obtain more detail!
   Guarantees:
   If expr or ntrees are NULL, than error is set to EINVAL.
   If the parser runs out of memory, errno will be set to ENOMEM. */
struct ast **
parser_parse (const char *const expr, size_t *const ntrees,
	      struct parser_error *const error)
{
  struct ast **ret = NULL;
  if (expr && ntrees)
    {
      if (error)
	{
	  memset (error, 0, sizeof *error);
	  error->error_type = PARSER_NO_ERROR;
	}
      
      *ntrees = 0;
      
      char *const expr_copy = strdup (expr);
      if (!expr_copy)
	return NULL;

      FILE *const in = fmemopen (expr_copy, strlen (expr_copy), "r");

      if (in)
	{
	  /* parse each expression using parser_parse_single_line */
	  while (!feof (in) && !ferror (in))
	    {
	      /* allocate memory of current expression */
	      struct ast **const tmp =
		realloc (ret, (*ntrees + 1) * sizeof *ret);
	      /* handle out of memory errors */
	      if (!tmp)
		{
		  size_t i;

		  if (error)
		    error->error_type = PARSER_OUT_OF_MEMORY;
		    
		  for (i = 0; i < *ntrees; ++i)
		    ast_release (ret[i]);

		  *ntrees = 0;
		  free (ret);
		  ret = NULL;

		  break;
		}

	      ret = tmp;
	      /* parse single line */
	      ret[(*ntrees)++] = parser_parse_single_line (in, error);
	      
	      /* handle out of memory errors  */
	      if (!ret[*ntrees - 1])
		{
		  size_t i;

		  for (i = 0; i < *ntrees - 1; ++i)
		    ast_release (ret[i]);

		  *ntrees = 0;
		  free (ret);
		  ret = NULL;

		  break;
		}
	    }

	  if (ferror (in))
	    {
	      size_t i;

	      if (error)
		error->error_type = PARSER_UNKNOWN_ERROR;

	      for (i = 0; i < *ntrees; ++i)
		ast_release (ret[i]);

	      *ntrees = 0;
	      free (ret);
	      ret = NULL;
	    }


	  if (fclose (in))
	    {
	      size_t i;

	      if (error)
		error->error_type = PARSER_UNKNOWN_ERROR;

	      for (i = 0; i < *ntrees; ++i)
		ast_release (ret[i]);

	      *ntrees = 0;
	      free (ret);
	      ret = NULL;
	    }

	  free (expr_copy);
	}
      else
	{
	  if (error)
	    {
	      error->error_type = PARSER_UNKNOWN_ERROR;
	      if (errno == ENOMEM)
		error->error_type = PARSER_OUT_OF_MEMORY;
	    }

	  *ntrees = 0;

	}
    }
  else
    errno = EINVAL;


  return ret;
}

/* Reads a single line from in and returns the struct ast representing the
   line. This function returns NULL immediately if in is NULL.

   The function will always return NULL in the event of ANY error.
   The following 3 rules apply only if error is not NULL.

   1) If the line has an invalid token, and error is not NULL, then
   error->error_type is set to PARSER_INVALID_TOKEN and the corresponding
   locations fields are set (note that the line_numbers will always be 1).

   2) If the line has a syntax error, and error is not NULL, then
   error->error_type is set to PARSER_SYNTAX_ERROR and the corresponding
   location fileds are set (again, note that the line_numbers will always be
   one).

   3) If the parser runs out of memory, error->error_type is set to
   PARSER_OUT_OF_MEMORY.

   In any other error, error->error_type is set to PARSER_UNKNOWN_ERROR.

   More information can be found by inspecting errno.
   Guarantees:
   If the parser runs out of memory, errno will be set to ENOMEM.
   If in is NULL, errno will be set to EINVAL.*/
struct ast *
parser_parse_single_line (FILE *const in, struct parser_error *const error)
{

  if (!in)
    {
      errno = EINVAL;
      return NULL;
    }

  if (error)
    {
      memset (error, 0, sizeof *error);
      error->error_type = PARSER_NO_ERROR;
    }
  
  yyscan_t scanner;
  if (yylex_init (&scanner))
    {
      if (error)
	{
	  error->error_type = PARSER_UNKNOWN_ERROR;
	  if (errno == ENOMEM)
	    error->error_type = PARSER_OUT_OF_MEMORY;
	}

      return NULL;
    }

  struct parser_bridge ret;
  memset (&ret, 0, sizeof ret);
  yyset_extra (&ret, scanner);
  
  if (yyparse (scanner) && error)
    *error = ret.error;
  
  if (yylex_destroy (scanner))
    {
      ast_release (ret.tree);
      ret.tree = NULL;

      if (error)
	  error->error_type = PARSER_UNKNOWN_ERROR;
    }
  
  return ret.tree;
}
