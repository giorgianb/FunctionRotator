/* ast.c - implements a basic abstract syntax tree for use with a basic parser. */
#include <errno.h>
#include <string.h>
#include <stdlib.h>
#include "ast.h"

static struct ast *
ast_acquire (void)
{
  return calloc (1, sizeof (struct ast));
}

/* Allocates and constructs a new nonterminal ast.  Nonterminal asts
   are used to represent operators such as '+', '*', '^', whose
   operands can be another layer of ast trees.  left and right represent the
   left and right operand of the operator respectively.  If there is not
   enough memory to allocate an ast, NULL is returned and errno is set
   to ENOMEM.  If operator is equal to the enumeration constant NONE
   (defined in ast.h), then NULL is returned and errno is set to
   EINVAL.  Otherwise, the constructed ast is returned.  */
struct ast *
ast_nonterminal_acquire (const int operator, struct ast *const left,
			 struct ast *const right)
{
  struct ast *t = NULL;

  if (operator != NONE)
    {

      t = ast_acquire ();

      if (t)
	{
	  t->operator = operator;
	  t->left = left;
	  t->right = right;
	}
    }
  else
    errno = EINVAL;

  return t;
}

/* Allocates and construct a terminal ast.  Terminal asts have no children
   and therefore represents an expression with no operators, such as
   the number 5 or the variable 'x'. If there is not enough memory
   to allocate an ast, NULL is returned and errno is set to ENONMEM. If
   type is not either NUMBER or ID, then NULL is returned and errno is
   set to EINVAL. Otherwise, the constructed ast is returned. Note that
   the constructed ast will contain no references to id and therefore
   it is safe to deallocate id if it is dynamically allocated. */
struct ast *
ast_terminal_acquire (const int type, const double value,
		      const char *const id)
{
  struct ast *t;

  t = ast_acquire ();
  if (t)
    {
      t->operator = NONE;
      t->operand.type = type;
      switch (type)
	{
	case NUMBER:
	  t->operand.value = value;
	  break;
	case ID:
	  t->operand.id = strdup (id);
	  if (!t->operand.id)
	    {
	      free (t);
	      t = NULL;
	    }

	  break;
	default:
	  free (t);
	  t = NULL;
	}
    }
  else
    errno = EINVAL;

  return t;
}

/* Releases an ast returned by one of the ast_*_acquire functions.  */
void
ast_release (struct ast *const t)
{
  if (t->operator != NONE)
    {
      ast_release (t->left);
      ast_release (t->right);
      free (t);
    }
  else
    {
      if (t->operand.type == ID)
	free(t->operand.id);

      free(t);
    }
}
