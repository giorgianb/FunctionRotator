package FunctionParser;

public class FunctionParser
{
    public static AbstractSyntaxTree[] parse (final String function)
	throws SyntaxError
    {
	if (function.charAt (function.length () - 1) != '\n')
	    return yaccParse (function + '\n');
	else
	    return yaccParse (function);
    }

    private static native AbstractSyntaxTree[] yaccParse (final String function)
	throws SyntaxError;
}
	
