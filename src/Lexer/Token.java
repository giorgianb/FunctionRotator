package Lexer;

public class Token
{
    public enum Type
    {
	OPERATOR, NAME, NUMBER, TERMINATOR
    }

    private Type type;
    private String token;

    public Token (final Type type, final String token)
    {
	this.type = type;
	this.token = token;
    }

    public Type getType ()
    {
	return type;
    }

    public String getToken ()
    {
	return token;
    }

    public String toString ()
    {
	return "Token<" + type + ">(" + token + ")";
    }
}
	
