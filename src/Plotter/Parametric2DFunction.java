import Functions.Function;

public class Parametric2DFunction
{
    private Function xFunc, yFunc;
    
    public Parametric2DFunction (final Function xFunc, final Function yFunc)
    {
	if (xFunc.getArity () != 1 || yFunc.getArity () != 1)
	    throw new FunctionMisMatchError ("Parametric2DFunctions must have an arity of 1!");

	this.xFunc = xFunc;
	this.yFunc = yFunc;
    }

    public Function getXFunction ()
    {
	return xFunc;
    }

    public Function getYFunction ()
    {
	return yFunc;
    }
}
