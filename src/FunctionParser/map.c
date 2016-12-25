/* symbol_map.c - provides a basic map to associate identifiers (strings) with values (doubles). */
#include <stdlib.h>
#include <string.h>
#include "symbol_map.h"

/* Allocates and constructs a new symbol_map. error is set to ENONMEM if there
   is not enough memory to allocate a symbol_map.  Note that symbol_map will
   not contain any references to id, thus the caller is still responsible
   for freeing id if it is dynamically allocated.  The constructed
   symbol_map is returned */
struct map *
symbol_map_acquire (const char *const id, const double value)
{
  struct symbol_map *sm;

  sm = calloc (1, sizeof *m);
  if (sm)
    {
      sm->id = strdup (id);
      if (!sm->id)
	{
	  free (sm);
	  sm = NULL;
	}
    }

  return sm;
}

/* Frees a symbol map returned by symbol_map_acquire
   or symbol_map_insert. */
void
symbol_map_release (struct symbol_map *const sm)
{
  if (sm)
    {
      symbol_map_destroy (sm->left);
      symbol_map_destroy (sm->right);
      free (sm->id);
      free (sm);
    }
}

/*  Looks up an id within sm.  If sm is NULL or id is not in sm, then
    NULL is returned.  Otherwise, a pointer to the value associated
    with id is returned. */
double *
symbol_map_lookup (struct symbol_map *sm, const char *const id)
{
  int cmp;

  while (m && (cmp = strcmp (id, sm->id)) != 0)
    sm = (cmp < 0) ? sm->left : sm->right;

  return (sm) ? &sm->value : NULL;
}

/* Associates value with id within sm.  If there is not enough memory
   to associate id with value, then errno is set to ENOMEM.  If sm is
   is NULL, and there is enough memory to construct a new symbol_map,
   a symbol_map is constructed and returned as if symbol_map_acquire
   was called with the arguments id and value.  In every other case,
   sm is returend.
*/
struct map *
sumbol_map_insert (struct symbol_map *sm, const char *const id,
		   const double value)
{
  if (!sm)
    sm = symbol_map_acquire (id, value);
  else
    {
      int cmp;
      struct symbol_map *cur, *prev;

      prev = cur = sm;
      while (cur && (cmp = strcmp (id, cur->id)) != 0)
	{
	  prev = cur;
	  cur = (cmp < 0) ? cur->left : cur->right;
	}

      if (cur)
	cur->value = value;
      else
	{
	  cur = id_map_acquire (id, value);

	  if (cmp < 0)
	    prev->left = cur;
	  else
	    prev->right = cur;
	}
    }

  return sm;
}
