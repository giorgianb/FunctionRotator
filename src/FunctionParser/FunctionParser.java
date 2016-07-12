package FunctionParser;

public class FunctionParser
{
    public static native AbstractSyntaxTree[] parse (final String function)
	throws SyntaxError;
}
	
