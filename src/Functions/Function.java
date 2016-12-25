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
	throws UndefinedReferenceError
    {
	this.name = name;
	this.parameters = new ArrayList<> (parameters);
	this.value = value;
	this.namespace = namespace;

	namespace.define (name, parameters.size (), value);
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

    public int getArity ()
    {
	return parameters.size ();
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
	throws UndefinedReferenceError
    {
	switch (value.getType ())
	    {
	    case OPERATOR:
		if (!Arrays.asList (operators).contains (value.getOperator ())
		    && !namespace.isDeclared (value.getOperator (), value.getArity ()))
		    throw new UndefinedReferenceError ("Function references undeclared operator "
							   + value.getOperator ()
							   + "(" + value.getArity () + ").");
		values.stream ().forEach (operand -> checkIfAllDefined (operand));
		break;
	    case NAME:
		if (!parameters.contains (value.getName ())
		    && !namespace.isDeclared (value.getName ()))
		    throw new UndefinedReferenceError ("Function references undeclared variable "
						       + value.getName ());
	    }
    }
}

