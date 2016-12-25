package Parser;

public class Operand
{
    public enum Type
    {
	NUMBER, NAME, EXPRESSION
    }

    private double number;
    private String name;
    private Expression expression;
    private Type type;
    
    public Operand (final double number)
    {
	type = Type.NUMBER;
	this.number = number;
    }

    public Operand (final String name)
    {
	type = Type.NAME;
	this.name = name;
    }

    public Operand (final Expression expression)
    {
	type = Type.EXPRESSION;
	this.expression = expression;
    }

    public Type getType ()
    {
	return type;
    }

    public double getNumber ()
    {
	if (type != Type.NUMBER)
	    throw new UnsupportedOperationException ("Operand not a number.");

	return number;
    }

    public String getName ()
    {
	if (type != Type.NAME)
	    throw new UnsupportedOperationException ("Operand not a name.");

	return name;
    }

    public Expression getExpression ()
    {
	if (type != Type.EXPRESSION)
	    throw new UnsupportedOperationException ("Operand not an Expression.");

	return expression;
    }
}
