package Functions;

import Parser.Expression;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class Function
{
    private static String operators = {"^", "*", "/", "+", "-", "="};
    private String name;
    private List<String> parameters;
    private Expression value;
    private Namespace namespace;
    
    public Function (final String name,
		     final List<String> parameters,
		     final Expression value,
		     final Namespace namespace)
    {
	this.name = name;
	this.parameters = new ArrayList<> (parameters);
	this.value = value;
	this.namespace = namespace;

	checkIfAllDefined (value);
    }

    public String getName ()
    {
	return name;
    }

    public List<String> getParameters ()
    {
	return new ArrayList<> (parameters);
    }

    public Expression getValue ()
    {
	return value;
    }

    public Namespace getNamespace ()
    {
	return namespace;
    }

    private void checkIfAlldefined (final Expression value)
    {
	switch (value.getType ())
	    {
	    case OPERATOR:
		if (!Arrays.asList (operators).contains (value.getOperator ())
		    && !namespace.isDeclared (value.getOperator (), value.getArity ()))
		    throw new UndefinedReferenceException ("Function references undeclared operator "
							   + value.getOperator () + "(" + value.getArity ()
							   +").");
		    for (Expression operand: value.getOperands ())
			checkIfAllDefined (operand);
		    break;
	    case NAME:
		if (!parameters.contains (value.getName ())
		    && !namespace.isDeclared (value.getName ()))
		    throw new UndefinedReferenceException ("Function references undeclared variable "
							   + value.getName ());
	    }
    }
}

