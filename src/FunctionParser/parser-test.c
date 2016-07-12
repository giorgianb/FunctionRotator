#define _GNU_SOURCE
#include <getopt.h>
#include "estdio.h"
#include "parser.h"
#include "ast.h"

void ast_print (const struct ast *t, FILE * out);
void test_parser_parse_single_line (FILE * in, FILE * out);

int
main (int argc, char *argv[])
{
  FILE *out = NULL;
  FILE *in = NULL;
  void (*test) (FILE * in, FILE * out) = NULL;

  static struct option long_options[] = {
    {"file", required_argument, NULL, 'f'},
    {"output", required_argument, NULL, 'o'},
    {"test", required_argument, NULL, 't'},
    {NULL, 0, NULL, 0},
  };
  int option_index = 0;
  
  while ((opt =
	  getopt_long (argc, argv, "f:o:t:", long_options,
		       &option_index)) != -1)
    {
      switch (opt)
	{
	case 'f':
	  if (in)
	    {
	      efprintf ("error: cannot specify more than one in file.", stderr);
	      exit (EXIT_FAILURE);
	    }
	  
	  in = efopen (optarg, "r");
	  break;
	case 'o':
	  if (in)
	    {
	      efprintf ("error: cannot specify more than one out file.", stderr);
	      exit (EXIT_FAILURE);
	    }
	  
	  out = efopen (optarg, "w");
	  break;
	case 't':
	  if (test)
	    {
	      efputs ("error: cannot specify more that one test.", stderr);
	      exit (EXIT_FAILURE);
	    }
	  
	  test = test_lookup (optarg);
	  if (!test)
	    {
	      efprintf (stderr, "error: invalid test '%s'.\n", optarg);
	      exit (EXIT_FAILURE);
	    }
	  break;
	default:
	  efputs ("error: unknown option in argument list.\n", stderr);
	  exit (EXIT_FAILURE);
	  break;
	}
    }

  (*test) (in, out);
  
  return 0;
}

void
test_parser_parse_single_line (FILE * const out)
{
  while (!efeof (stdin))
    {
      struct parser_error error;
      struct ast *ret = parser_parse_single_line (stdin, &error);
      if (ret)
	{
	  ast_print (ret);
	  efputc ('\n', out);
	  ast_release (ret);
	}
      else
	{
	  switch (error.error_type)
	    {
	    case PARSER_NO_ERROR:
	      efputs ("PARSER_NO_ERROR", out);
	      break;
	    case PARSER_SYNTAX_ERROR:
	      efputs ("PARSER_SYNTAX_ERROR", out);
	      break;
	    case PARSER_INVALID_TOKEN:
	      efputs ("PARSER_INVALID_TOKEN", out);
	      break;
	    case PARSER_OUT_OF_MEMORY:
	      efputs ("PARSER_OUT_OF_MEMORY", out);
	      break;
	    case PARSER_UNKNOWN_ERROR:
	      efputs ("PARSER_UNKNOWN_ERROR", out);
	      break;
	    default:
	      efputs ("PARSER_UNKNOWN_ERROR", out);
	      break;
	    }

	  efprintf (out, "first_line: %d\n", error.first_line);
	  efprintf (out, "first_column: %d\n", error.first_column);
	  efprintf (out, "last_line: %d\n", error.last_line);
	  efprintf (out, "last_column: %d\n", error.last_column);
	}
    }
}

void
ast_print (FILE * const out, const struct ast *const t)
{
  if (t->operator != AST_NONE)
    {
      if (t->operator != AST_CALL)
	efprintf (out, "(%c ", t->operator);
      else
	efprintf (out, "(call ");
      ast_print (out, t->left);
      efputc (' ', out);
      ast_print (out, t->right);
      efputc (')', out);
    }
  else
    {
      if (t->operand.type == AST_NUMBER)
	efprintf (out, "%f", t->operand.value);
      else
	efprintf (out, "%s", t->operand.id);
    }
}
