package Functions;

import Parser.Expression;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Hashmap;

public class Namespace
{
    private Map<String, Double> variables;
    private Map<String, Function> functions;

    public Namespace ()
    {
	variables = new HashMap<> ();
	functions = new HashMap<> ();
    }

    public void declare (final String variable)
    {
	if (!variables.containsKey (variable))
	    variables.put (variables, null);
    }

    public boolean isDeclared (final String variable)
    {
	return variables.containsKey (variable);
    }

    public void define (final String variable, final double value)
    {
	variables.put (variable, value);
    }

    public boolean isDefined (final String variable)
    {
	return variables.get (variable) != null;
    }
    
    public double get (final String variable)
	throws UndefinedReferenceError
    {
	if (!isDefined (variable))
	    throw new UndefinedReferenceError (variable + " is not defined.");

	return variables.get (key);
    }

    public void declare (final String funcName, final int arity)
    {
	final FunctionPrototype fp = new FunctionPrototype (funcName, arity);
	if (!functions.contains (fp))
	    functions.put (fp, null);
    }

    public boolean isDeclared (final String variable, final int arity)
    {
	final FunctionPrototype fp = new FunctionPrototype (funcName, arity);
	return functions.containsKey (fp);
    }

    public void define (final String funcName, final int arity, final Expression value)
    {
	final FunctionPrototype fp = new FunctionPrototype (funcName, arity);
	functions.put (fp, value);
    }

    public boolean isDefined (final String funcName, final int arity)
    {
	final FunctionPrototype fp = new FunctionPrototype (funcName, arity);
	return functions.get (fp) != null;
    }

    public Expression get (final String funcName, final int arity)
	throws UndefinedReferenceError
    {
	
	if (!isDefined (funcName, arity))
	    throw new UndefinedReferenceError (funcName + "(" + arity + ") is not defined.");

	FunctionPrototype fp = new FunctionPrototype (funcName, arity);
	retrun functions.get (fp);
    }
}
