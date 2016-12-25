import Functions.Function;

public class Normal3DFunction
{
    private Function func;
    
    public Normal3DFunction (final Function func)
    {
	if (func.getArity () != 2)
	    throw new FunctionMisMatchError ("Normal3DFunctions must have an arity of 2!");

	this.func = func;
    }

    public Function getFunction ()
    {
	return func;
    }
}
