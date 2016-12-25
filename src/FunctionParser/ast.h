/* ast.h - interface to ast.c */
#ifndef AST_HEADER_INCLUDED
#define AST_HEADER_INCLUDED
enum operators
  { AST_ADD = '+', AST_SUBTRACT = '-', AST_MULTIPLY = '*', AST_DIVIDE = '/', AST_EXPONENT = '^',
    AST_EQUAL = '=', AST_COMMA = ',', AST_CALL, AST_NONE
};
enum terminal_types
{ AST_NUMBER, AST_ID };

struct terminal_type
{
  union
  {
    double value;
    char *id;
  };

  int type;
};

struct ast
{
  int operator;

  union
  {
    struct
    {
      struct ast *left;
      struct ast *right;
    };

    struct terminal_type operand;
  };
};

struct ast *ast_nonterminal_acquire (int operator, struct ast *left,
				     struct ast *right);

struct ast *ast_terminal_acquire (int type, double value, const char *id);

void ast_release (struct ast *t);
#endif	/* AST_HEADER_INCLUDED */
