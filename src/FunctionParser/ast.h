/* ast.h - interface to ast.c */
enum operators
  { ADD = '+', SUBTRACT = '-', MULTIPLY = '*', DIVIDE = '/', EXPONENT = '^',
    EQUAL = '=', COMMA = ',', CALL, NONE
};
enum terminal_types
{ NUMBER, ID };

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
