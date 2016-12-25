package LexerTest;
import Lexer.TokenStream;
import java.io.IOException;

public class LexerTest
{
    public static void main (final String args[])
	throws IOException
    {
	TokenStream in = new TokenStream (System.in);
	for (;;)
	    System.out.println (in.read ());
    }
}
		
