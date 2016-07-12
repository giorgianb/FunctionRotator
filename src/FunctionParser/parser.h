/* parser.h - interface to parser.c */
#ifndef PARSER_HEADER_INCLUDED
#define PARSER_HEADER_INCLUDED
#include "ast.h"

struct parser_error
{
  int error_type;
  int first_line;
  int first_column;
  int last_line;
  int last_column;
};

enum parser_error_number
{ PARSER_NO_ERROR, PARSER_SYNTAX_ERROR, PARSER_INVALID_TOKEN,
    PARSER_OUT_OF_MEMORY, PARSER_UNKNOWN_ERROR };

struct ast **parser_parse (const char *expr, size_t * ntrees,
			   struct parser_error *error);
struct ast *parser_parse_single_line (FILE * in, struct parser_error *error);

#endif /* PARSER_HEADER_INCLUDED */
