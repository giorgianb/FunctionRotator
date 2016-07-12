package FunctionParser;

/**
 * A basic AbstractSyntaxTree suitable for basic mathematical expressions.
 * This can be used to represent expressions where each operator has at most
 * two operands, and the non-terminal types of an expression are either numbers
 * or identifiers (an alphanumeric string not beginning with a number).
 *
 * @author Giorgian Borca-Tasciuc
 * @version 0.2
 * @since 0.2
 * @see Parser
 */
public class AbstractSyntaxTree
{
    public static final int NONE = 0;
    
    public static final int ADD = '+';
    public static final int SUBTRACT = '-';
    public static final int MULTIPLY = '*';
    public static final int DIVIDE = '/';
    public static final int EXPONENT = '^';
    public static final int EQUAL = '=';
    public static final int COMMA = ',';
    public static final int CALL = 1;

    private static final int NUMBER = 1;
    private static final int ID = 2;

    private int operator;
    private AbstractSyntaxTree left;
    private AbstractSyntaxTree right;
    
    private int type;
    private double value;
    private String id;

    private AbstractSyntaxTree (final int operator,
				final AbstractSyntaxTree left,
				final AbstractSyntaxTree right,
				final int type, final double value,
				final String id)

    {
	this.operator = operator;
	this.left = left;
	this.right = right;
	this.type = type;
	this.value = value;
	this.id = id;
    }

    /**
     *
    public AbstractSyntaxTree (final int operator,
			       final AbstractSyntaxTree left,
			       final AbstractSyntaxTree right)

    {
	this(operator, left, right, NONE, 0, null);
	
	if (operator == NONE)
	    throw new
		IllegalArgumentException ("operator must not be AbstractSyntaxTree.NONE");
    }

    AbstractSyntaxTree (final String id)
    {
	this(NONE, null, null, ID, 0, id);
    }

    public AbstractSyntaxTree (final double value)
    {
	this(NONE, null, null, NUMBER, value, null);
    }

    public boolean isTerminal ()
    {
	return operator == NONE;
    }

    public boolean isNonTerminal ()
    {
	return operator != NONE;
    }

    public boolean isID ()
    {
	return type == ID;
    }

    public boolean isNumber ()
    {
	return type == NUMBER;
    }

    public int getOperator ()
    {
	return operator;
    }

    public AbstractSyntaxTree getLeftChild ()
    {
	if (operator == NONE)
	    throw new
		UnsupportedOperationException ("AbstractSyntaxTree is not non-terminal.");

	return left;
    }

    public AbstractSyntaxTree getRightChild ()
    {
	if (operator == NONE)
	    throw new
		UnsupportedOperationException ("AbstractSyntaxTree is not non-terminal.");

	return right;
    }

    public int getType ()
    {
	return type;
    }

    public double getValue ()
    {
	if (operator != NONE || type != NUMBER)
	    throw new
		UnsupportedOperationException ("AbstractSyntaxTree not a number.");

	return value;
    }
	    
    public String getID ()
    {
	if (operator != NONE || type != ID)
	    throw new
		UnsupportedOperationException ("AbstractSyntaxTree not an ID.");

	return id;
    }

}
