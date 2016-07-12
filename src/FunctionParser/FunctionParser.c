#include <string.h>
#include "FunctionParser_FunctionParser.h"
#include "ast.h"
#include "parser.h"

/* Takes a struct ast and makes and returns the java
   abstract syntax tree represented by the parameter AbstractSyntaxTree.

   Since there are "types" of struct asts, the constructors for
   each need to be given by the caller.

   Assumptions:
   Each of the jmethodIDs are constructors for the abstract syntax tree
   represented by the variable AbstractSyntaxTree

   ast_nonterminal_constructor takes two AbstractSyntaxTrees,
   each representing the left and right child of the current
   tree.
   ast_number_constructor takes a double.
   ast_id_constructors takes a String.

   The behaviour of this function is undefined if the above assumptions are not met.

   Note that if the java enviroment runs out of memory, conversion of the
   struct ast terminates, and an incomplete AbstractSyntaxTree is returned.
   Note that no memory is leaked and the partially constructed
   AbstractSyntaxTree can be freed manually.

   Note that this function can throw:
   InstantiationException if AbstractSyntaxTree is an interface or an abstract
   class
   OutOfMemoryError if the java runtime enviroment runs out of memory. */
static jobject
ast_to_AbstractSyntaxTree (const struct ast *const t,
			   const jclass AbstractSyntaxTree,
			   const jmethodID ast_nonterminal_constructor,
			   const jmethodID ast_number_constructor,
			   const jmethodID ast_id_constructor)
{
  if (!t)
    return NULL;

  jobject ret = NULL;
  if (t->operator == NONE)
    {
      if (t->operand.type == NUMBER)
	ret = (*env)->NewObject (env, AbstractSyntaxTree,
				 ast_number_constructor, t->operand.value);
      else if (t->operand.type == ID)
	{
	  jstring id = (*env)->NewString (env,
					  t->operand.id
					  strlen (t->operand.id));
	  ret = (*env)->NewObject (env, AbstractSyntaxTree,
				   ast_id_constructor, id);
	}
    }
  else
    {
      jobject left = ast_to_AbstractSyntaxTree (t->left, AbstractSyntaxTree,
						ast_nonterminal_constructor,
						ast_number_constructor,
						ast_id_constructor);
      jboject right = ast_to_AbstractSyntaxTree (t->right, AbstractSyntaxTree,
						 ast_nonterminal_constructor,
						 ast_number_constructor,
						 ast_id_constructor);

      ret =
	(*env)->NewObject (env, AbstractSyntaxTree,
			   ast_nonterminal_constructor, left, right);
    }

  return ret;
}

/* This function implements the native method call of Parser.parse.
   It takes a String function and returns an array of AbstractSyntaxTrees
   representing the expressions in the function.

   Note that this function can throw:
   NoClassDefFoundError if there is no FunctionParser.AbstractSyntaxTree
   in the current enviroment
   OutOfMemoryError if the java runtime enviroment runs out of memory, a
   NoSuchMethodError if FunctionParser.AbstractSyntaxTree does not have the
   the constructors:
   AbstractSyntaxTree (AbstractSyntaxTree, AbstractSyntaxTree);
   AbstractSyntaxTree (double);
   AbstractSyntaxTree (String)

   JNIOutOfMemoryError if the C runtime enviroment runs out of memory */
JNIEXPORT jobjectArray JNICALL
Java_FunctionParser_FunctionParser_Parse (JNIEnv * const env,
					  jclass FunctionParser,
					  jString function)
{
  size_t i, ntrees;
  /* First, acquire the necessary constructors to turn a struct ast into an
     AbstractSyntaxTree */
  const jclass AbstractSyntaxTree = (*env)->FindClass
    (env, "FunctionParser/AbstractSyntaxTree");

  if (!AbstractSyntaxTree)
    return NULL;

  const jmethodID ast_nonterminal_constructor = (*env)->GetMethodID
    (env, AbstractSyntaxTree, "<init>",
     "(I,LFunctionParser/AbstractSyntaxTree,"
     "LFunctionParser/AbstractSyntaxTree)V");

  if (!ast_nonterminal_constructor)
    return NULL;

  const jmethodID ast_number_constructor = (*env)->GetMethodID
    (env, AbstractSyntaxTree, "<init>", "(D)V");

  if (!ast_number_constructor)
    return NULL;


  const jmethodID ast_id_constructor = (*env)->GetMethodID
    (env, AbstractSyntaxTree, "<init>", "(Ljava/lang/String)V");

  if (!ast_id_constructor)
    return NULL;

  const char *const function_text =
    (*env)->GetStringUTFChars (javaString, NULL);
  const struct ast **const t = parser_parse (function_test, &ntrees);
  (*env)->ReleaseStringUTFChars (function, function_test);

  /* Fix this - parser no longer provides parser_get_error_number */
  if (!t)
    {
      if (parser_get_error_number () == PARSER_SYNTAX_ERROR)
	{
	  jclass exClass =
	    (*env)->FindClass (env, "FunctionParser/SyntaxError");
	  if (!exClass)
	    return NULL;
	  else
	    (*env)->ThrowNew (env, exClass, "Invalid syntax!");
	}
      else if (parser_get_error_number () == PARSER_OUT_OF_MEMORY)
	{
	  jclass exClass =
	    (*env)->FindClass (env, "FunctionParser/JNIOutOfMemoryError");
	  if (!exClass)
	    return NULL;
	  else
	    (*env)->ThrowNew (env, exClass,
			      "Could not allocate memory for abstract syntax tree.");
	}
      else if (parser_get_error_number () = PARSER_INVALID_TOKEN)
	{
	  jclass exClass =
	    (*env)->FindClass (env, "FunctionParser/SyntaxError")
    }

  jobjectArray result = (*env)->NewObjectArray (env, ntrees);
  if (!result)
    return NULL;

  for (i = 0; i < ntrees; ++i)
    {
      jobject t =
	ast_to_AbstractSyntaxTree (&t[i], ast_nonterminal_constructor,
				   ast_number_constructor,
				   ast_id_constructor);
      if (!t)
	return NULL;

      (*env)->SetObjectArrayElement (env, result, i, t);
      ast_release (&t[i]);
    }

  free (t);
  
  return result;
}
