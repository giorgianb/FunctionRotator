import Functions.Function;

public class Parametric3DFunction
{
    private Function xFunc, yFunc, zFunc;
    
    public Parametric2DFunction (final Function xFunc,
				 final Function yFunc,
				 final Function zFunc)
    {
	if (xFunc.getArity () != 2 || yFunc.getArity () != 2 || zFunc.getArity () != 2)
	    throw new FunctionMisMatchError ("Parametric3DFunctions must have an arity of 2!");

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
