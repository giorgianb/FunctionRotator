package ParserTest;
import Parser.ExpressionStream;
import Parser.Expression;
import Lexer.TokenStream;
import Parser.ParseError;
import java.io.IOException;

public class ParserTest
{
    public static void main (final String args[])
	throws ParseError, IOException
    {
	TokenStream tok = new TokenStream (System.in);
	ExpressionStream in = new ExpressionStream (tok);
	for (;;)
	    System.out.println (in.read ());
    }
}
		
