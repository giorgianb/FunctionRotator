import Functions.Function;

public class Parametric3DFunction
{
    private Function xFunc, yFunc, zFunc;
    
    public Parametric2DFunction (final Function xFunc,
				 final Function yFunc,
				 final Function zFunc)
    {
	if (xFunc.getArity () != 1 || yFunc.getArity () != 1 || zFunc.getArity () != 1)
	    throw new FunctionMisMatchError ("Parametric3DFunctions must have an arity of 1!");

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

    public Function getZFunction ()
    {
	return zFunc;
    }
}
