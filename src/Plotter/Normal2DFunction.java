import Functions.Function;

public class Normal2DFunction
{
    private Function func;
    
    public Normal2DFunction (final Function func)
    {
	if (func.getArity () != 1)
	    throw new FunctionMisMatchError ("Normal2DFunctions must have an arity of 1!");

	this.func = func;
    }

    public Function getFunction ()
    {
	return func;
    }
}
