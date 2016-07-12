#ifndef ELIB_STDIO_HEADER_INCLUDED
#define ELIB_STDIO_HEADER_INCLUDED

#include <stdio.h>

/* File access */
#define EFOPEN (...) elib_efopen (__FILE__, __LINE__, #__VAR_ARGS__, __VAR_ARGS__)
#define EFREOPEN (...)
#define EFFLUSH (...)
#define EFCLOSE (...)
#define ESETBUF (...)
#define ESETVBUF (...)
#define EFWIDE (...)

/* Direct input/output */
#define EFREAD (...)
#define EFWRITE (...)

/* Unformatted input/output */
#define EFGETC (...)
#define EFGETWC (...)
#define EGETC (...)
#define EGETWC (...)
#define EFGETS (...)
#define EFGETWS (...)
#define EFPUTC (...)
#define EFPUTWC (...)
#define EPUTC (...)
#define EPUTWC (...)
#define EFPUTS (...)
#define EFPUTWS (...)
#define EGETCHAR (...)
#define EGETWCHAR (...)
#define EGETS (...)
#define EPUTCHAR (...)
#define EPUTWCHAR (...)
#define EPUTS (...)
#define EUNGETC (...)
#define EUNGETWC (...)

/* Formatted input/output */
#define ESCANF (...)
#define EWSCANF (...)
#define EFSCANF (...)
#define EFWSCANF (...)
#define ESSCANF (...)
#define ESWSCANF (...)
#define EVSCANF (...)
#define EVWSCANF (...)
#define EVFSCANF (...)
#define EVFWSCANF (...)
#define EVSSCANF (...)
#define EVSWSCANF (...)
#define EPRINTF (...)
#define EWPRINTF (...)
#define EFPRINTF (...)
#define EFWPRINTF (...)
#define ESPRINTF (...)
#define ESWPRINTF (...)
#define ESNPRINTF (...)
#define EVPRINTF (...)
#define EVWPRINTF (...)
#define EVFPRINTF (...)
#define EVFWPRINTF (...)
#define EVSPRINTF (...)
#define EVSWPRINTF (...)
#define EVNSPRINTF (...)
#define PERROR (...)

/* File positioning */
#define EFSEEK (...)
#define EFSEEKO (...)
#define EFGETPOS (...)
#define EFSETPOS (...)
#define EREWIND (...)
#define ECLEARERR (...)
#define EFEOF (...)
#define EFERROR (...)
#define EREMOVE (...)
#define ERENAME (...)
#define ETMPFILE (...)
#define ETMPNAM (...)

FILE *elib_efopen (const char *srcfile, const char *srcline, const char *callargs, const char *path, const char *mode);
int elib_efprintf (const char *srcfile, const char *srcline, const char *callargs, FILE *stream, const char *format, ...);
int elib_fputs (const char *srcfile, const char *srcline, const char *callargs, const char *s, FILE *stream);
int elib_fputc (const char *srcfile, const char *srcline, const char *callargs, int c, FILE *stream);
#endif
