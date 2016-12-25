package Parser;

import java.util.List;
import java.util.ArrayList;
import Lexer.TokenStream;
import Lexer.Token;
import java.io.IOException;

public class ExpressionStream
{
    private TokenStream in;

    public ExpressionStream (final TokenStream in)
    {
	this.in = in;
    }

    public Expression read ()
	throws ParseError, IOException
    {
	return getNthLevelExpression (0);
    }

    public Expression getNthLevelExpression (final int n)
	throws ParseError, IOException
    {
	Token current;
	Expression left, right;
	switch (n)
	    {
	    case 0:
		// Make sure their is a statement terminator at the end of an expression
		left = getNthLevelExpression (n + 1);
		current = in.read ();
		if (current.getType () != Token.Type.TERMINATOR)
		    {
			in.pushBack (current);
			throw new ParseError ("Expected expression terminator.");
		    }

		return left;
	    case 1:
		// Handle addition and subtraction
		left = getNthLevelExpression (n + 1);
		current = in.read ();
		if (current.getToken ().equals ("+")
		    || current.getToken ().equals ("-"))
		    {
			right = getNthLevelExpression (n);
			return new Expression (current.getToken (), left, right);
		    }
		else
		    {
			in.pushBack (current);
			return left;
		    }
	    case 2:
		// Handle multiplication and division
		left = getNthLevelExpression (n + 1);
		current = in.read ();
		if (current.getToken ().equals ("*")
		    || current.getToken ().equals("-"))
		    {
			right = getNthLevelExpression (n);
			return new Expression (current.getToken (), left, right);
		    }
		else
		    {
			in.pushBack (current);
			return left;
		    }
	    case 3:
		// Handle exponentation
		left = getNthLevelExpression (n + 1);
		current = in.read ();
		if (current.getToken ().equals ("^"))
		    {
			right = getNthLevelExpression (n);
			return new Expression (current.getToken (), left, right);
		    }
		else
		    {
			in.pushBack (current);
			return left;
		    }
	    case 4:
		// Handle assignment
		left = getNthLevelExpression (n + 1);
		current = in.read ();
		if (current.getToken ().equals ("="))
		    {
			right = getNthLevelExpression (1);
			return new Expression (current.getToken (), left, right);
		    }
		else
		    {
			in.pushBack(current);
			return left;
		    }
	    case 5:
		// Handle the most base cases
		current = in.read ();
		// Handle parentheses
		if (current.getToken ().equals ("("))
		    {
			left = getNthLevelExpression (1);
			return left;
		    }
		else if (current.getType () == Token.Type.NAME)
		    {
			// Check if function call
		        Token peek = in.read ();
			in.pushBack (peek);
			if (peek.getToken ().equals ("("))
			    return new Expression (current.getToken (), getParameters ());
			else
			    return new Expression (current.getToken ());
		    }
		else if (current.getType () == Token.Type.NUMBER)
		    return new Expression (Double.parseDouble (current.getToken ()));
		else
		    {
			in.pushBack (current);
			throw new ParseError ("Unexpected token " + current.getToken ());
		    }
	    default:
		throw new UnsupportedOperationException ("Unknown level " + n);
	    }
    }

    private List<Expression> getParameters ()
	throws ParseError, IOException
    {
	List<Expression> parameters = new ArrayList<> ();
	Token current = in.read ();

	if (!current.getToken ().equals ("("))
	    {
		in.pushBack (current);
		throw new ParseError ("Expected open parenthesis.");
	    }

	do
	    {
		parameters.add (getNthLevelExpression (1));
		current = in.read ();
	    }
	while (current.getToken ().equals (","));

	if (!current.getToken ().equals (")"))
	    {
		in.pushBack (current);
		throw new ParseError ("Expected close parenthesis.");
	    }

	return parameters;
    }
}
		    
