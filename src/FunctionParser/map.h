i/* symbol_map.h - interface to symbol_map.c  */
struct symbol_map
{
  char *id;
  double value;
  struct map *left;
  struct map *right;
};

struct symbol_map *symbol_map_acquire (const char *id, double value);

void symbol_map_release (struct map *sm);

double *symbol_map_lookup (struct map *sm, const char *id);

struct map *symbol_map_insert (struct map *sm, const char *id, double value);
