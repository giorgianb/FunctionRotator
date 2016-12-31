package Parser;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

final public class Expression
{
    public enum Type
    {
	NUMBER, NAME, OPERATOR
    }

    private Type type;
    private String operator;
    private List<Expression> operands;
    private String name;
    private double number;

    public Expression (final double number)
    {
	type = Type.NUMBER;
	this.number = number;
    }

    public Expression (final String name)
    {
	type = Type.NAME;
	this.name = name;
    }

    public Expression (final String operator, final Expression... operands)
    {
	type = Type.OPERATOR;
	this.operator = operator;
	this.operands = Arrays.asList (operands);
    }

    public Expression (final String operator, final List<Expression> operands)
    {
	type = Type.OPERATOR;
	this.operator = operator;
	this.operands = new ArrayList<> (operands);
    }

    public Type getType ()
    {
  	return type;
    }

    public double getNumber ()
    {
	if (type != Type.NUMBER)
	    throw new UnsupportedOperationException ("Expression is not a number.");
	
	return number;
    }

    public String getName ()
    {
	if (type != Type.NAME)
	    throw new UnsupportedOperationException ("Expression is not a name.");
	
	return name;
    }

    public String getOperator ()
    {
	if (type != Type.OPERATOR)
	    throw new UnsupportedOperationException ("Expression is not an operator.");
	
	return operator;
    }

    public int getArity ()
    {
	if (type != Type.OPERATOR)
	    throw new UnsupportedOperationException ("Expression is not an operator.");
	
	return operands.size ();
    }

    public Expression getNthOperand (final int n)
    {
	if (type != Type.OPERATOR)
	    throw new UnsupportedOperationException ("Expression is not an operator.");
	else if (n >= operands.size ())
	    throw new IndexOutOfBoundsException ("n is greater than the amount of operands.");

	return operands.get (n);
    }

    public List<Expression> getOperands ()
    {
	if (type != Type.OPERATOR)
	    throw new UnsupportedOperationException ("Expression is not an operator.");
	
	return new ArrayList<> (operands);
    }

    public String toString ()
    {
	switch (type)
	    {
	    case NUMBER:
		return Double.toString (number);
	    case NAME:
		return name;
	    case OPERATOR:
		StringBuilder sb = new StringBuilder ("(" + operator);
		for (final Expression operand: operands)
		    {
			sb.append (" ");
			sb.append (operand.toString ());
		    }
		sb.append (")");

		return sb.toString ();
	    default:
		throw new UnsupportedOperationException ("Unknown type: " + type);
	    }
    }
}
