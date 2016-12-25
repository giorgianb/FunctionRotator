package Lexer;

import java.util.Deque;
import java.util.ArrayDeque;
import java.io.InputStream;
import java.io.IOException;

public class TokenStream
{
    private InputStream in;
    private Deque<Token> tokenPushBack;
    private Deque<Integer> characterPushBack;

    public TokenStream (final InputStream in)
    {
	this.in = in;
	tokenPushBack = new ArrayDeque<> ();
	characterPushBack = new ArrayDeque<> ();
    }


    private int getCharacter ()
	throws IOException
    {
	return (characterPushBack.size () > 0) ? characterPushBack.removeLast () : in.read ();
    }

    private void pushBackCharacter (final int character)
    {
	characterPushBack.addLast (character);
    }

    public Token read ()
	throws IOException
    {
	if (tokenPushBack.size () > 0)
	    return tokenPushBack.removeLast ();

	/* Skip spaces */
	int c;
	while (Character.isWhitespace (c = getCharacter ()))
	    ;

	/* If reached end of file, treat it as a terminator */
	if (c == -1)
	    return new Token (Token.Type.TERMINATOR, "");
	else if (c == ';')
	    return new Token (Token.Type.TERMINATOR, ";");

	if (Character.isDigit (c) || c == '.')
	    {
		pushBackCharacter (c);

		StringBuilder number = new StringBuilder ();
		for (c = getCharacter ();
		     c != ',' && Character.isDigit (c);
		     c = getCharacter ())
		    number.append ((char) c);

		if (c == '.')
		    {
			number.append ((char) c);
			for (c = getCharacter (); Character.isDigit (c); c = getCharacter ())
			    number.append ((char) c);
			pushBackCharacter (c);
		    }
		else
		    pushBackCharacter (c);

		return new Token (Token.Type.NUMBER, number.toString ());
	    }
	else if (Character.isAlphabetic (c))
	    {
		pushBackCharacter (c);

		StringBuilder name = new StringBuilder ();
		for (c = getCharacter ();
		     Character.isAlphabetic (c) || Character.isDigit (c);
		     c = getCharacter ())
		    name.append ((char) c);

		pushBackCharacter (c);

		return new Token (Token.Type.NAME, name.toString ());
	    }
	else
	    return new Token (Token.Type.OPERATOR, Character.toString ((char) c));
    }
		    
    public void pushBack (final Token token)
    {
	tokenPushBack.addLast (token);
    }
}
