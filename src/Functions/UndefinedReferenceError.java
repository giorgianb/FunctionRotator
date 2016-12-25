package Parser;

public class UndefinedReferenceError extends Exception
{
    public UndefinedReferenceError (final String message)
    {
	super (message);
    }

    public UndefinedReferenceError (final String message, final Throwable cause)
    {
	super (message, cause);
    }
}
		       
