package FunctionPlotter;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Formatter;

import FunctionRotator.Normal2DFunction;
import FunctionRotator.Normal3DFunction;
import FunctionRotator.Parametric2DFunction;
import FunctionRotator.Parametric3DFunction;
import FunctionRotator.PolarFunction;

/**
 * A low-level interface to gnuplot.
 * Can be used to plot functions of different types while controlling
 * the range of values the function is plotted over. PlotEngine can plot both
 * 3D and 2D functions, and functions can also be parametric and polar.
 * PlotEngine also controls the style in which these functions are plotted.
 * All plotting is done through the gnuplot program.
 * <p>
 * Note that instances of this class are not thread-safe. Also, unless
 * otherwise noted, passing a null argument to a constructor or method in
 * this class will cause a NullPointerException to be thrown.
 * <p> 
 * To function properly, this class relies on the gnuplot program
 * being properly installed.
 *
 * @author Giorgian Borca-Tasciuc
 * @version 0.2
 * @since 0.1
 * @see Function
 * @see Normal2DFunction
 * @see Normal3DFunction
 * @see Parametric2DFunction
 * @see Parametric3DFunction
 * @see PolarFunction
 * @see <a href="http://gnuplot.info">gnuplot</a>
 */
public final class PlotEngine
{
    public static final string FUNCTION_STYLES[] =
    { "points", "linespoints", "filledcurves", "lines", "vectors", "pm3d" };

    private Process plotter;
    private PrintWriter pin;
    private BufferedReader pout;
    private BufferedReader perr;
    private Logger log;
    private Formatter formatter;

    private static int min (final int a, final int b)
    {
	return (a < b) ? a : b;
    }

    private static int max (final int a, final int b)
    {
	return (a > b) ? a : b;
    }

    private static void logOutput ()
    {
	while (perr.ready ())
	    log.log (Level.WARNING, perr.readLine ());

	while (pout.ready ())
	    log.log (Level.INFO, pout.readLine ());
    }

    private static void sendCommand (final String command)
    {
	logOutput ();
	log.fine (command);
	pin.println (command);
	pin.flush ();
    }

    private static boolean validDouble (final double d)
    {
	return (d != Double.NaN && d != Double.POSITIVE_INFINITY
		&& d != Double.NEGATIVE_INFINITY);
    }

    /**
     * Constructs a PlotEngine by starting a seperate gnuplot process.
     *
     * @author Giorgian Borca-Tasciuc
     * @throws IOException if an I/O error occurs.
     * @throws SecurityExceptionIOException if an I/O error occurs.
     * @throws SecurityExceptionIOException if an I/O error occurs.
     * @throws SecurityExceptionIOException if an I/O error occurs.
     * @throws SecurityException if the security manager doesn't allow 
     * 	   the creation of a subprocess.
     * @since 0.1
     */
    public PlotEngine () throws IOException
    {
	plotter = new ProcessBuilder ("gnuplot").start ();
	pin = new PrintWriter (plotter.getOutputStream ());
	pout =
	    new BufferedReader (new InputStreamReader (plotter.getInputStream ()));
	perr =
	    new BufferedReader (new InputStreamReader (plotter.getErrorStream ()));

	log = logger.getLogger ("gnuplot");
	log.setLevel (Level.ALL);
    }

    /**
     * Plots funcs using the the gnuplot command 'plot'.
     * The methods setXRange() and setYRange() can be used to control the
     * domian and range over which the functions are plotted. Note that
     * plotted functions may not contain references other user-defined
     * functions or variables other than 'x'.
     *
     * @author Giorgian Borca-Tasciuc
     * @param funcs The 2D functions to plot, where y = f(x).
     * @throws NullPointerException if any of funcs is null.
     * @see Normal2DFunction
     * @since 0.2
     */
    public void plot (final Normal2DFunction ... funcs)
    {
	if (funcs.length == 0)
	    return;

	final StringBuilder command = new StringBuilder ("plot ");

	for (Normal2DFunction func:funcs)
	    {
		command.append (func.getFunction ());
		command.append (" title ");
		command.append (func.getName ());
		command.append (',');
	    }

	command.deleteCharAt (command.length () - 1);
	sendCommand ("unset parametric");
	sendCommand ("unset polar");
	sendCommand (command.toString ());
    }

    /**
     * Plots funcs using the gnuplot command 'splot'.
     * This methods setXRange(), setYRange(), and setZRange() can be used to
     * control the domain and range over which the functions are plotted.
     * Note that plotted functions may not contain references to other
     * user-defined functions or variables other than 'x', and 'y'.
     *
     * @author Giorgian Borca-Tasciuc
     * @param funcs The 3D functions to plot, where z = f(x, y).
     * @see Normal3DFunction
     * @throws NullPointerException if any of funcs is null.
     * @since 0.2
     */
    public void plot (final Normal3DFunction ... funcs)
    {
	if (funcs.length == 0)
	    return;

	final StringBuilder command = new StringBuilder ("splot ");

	for (Normal3D func:funcs)
	    {
		command.append (func.getFunction ());
		command.append (" title ");
		command.append (func.getName ());
		command.append (',');
	    }

	command.deleteCharAt (command.length () - 1);
	sendCommand ("unset parametric");
	sendCommand ("unset polar");
	sendCommand (command.toString ());
    }

    /**
     * Plots funcs using the gnuplot command 'plot'.
     * This methods setXRange(), setYRange(), and setTRange() can
     * be used to control the domain and range over which the functions
     * are plotted. Note that plotted functions may not contain references
     * to other user-defined functions or variables other than 't'.
     *
     * @author Giorgian Borca-Tasciuc
     * @param funcs The 2D functions to plot, where
     * 	  x = f(t)
     * 	  y = g(t)
     * @see Parametric2DFunction
     * @throws NullPointerException if any of funcs is null.
     * @since 0.2
     */
    public void plot (final Parametric2DFunction ... funcs)
    {
	final StringBuilder command = new StringBuilder ("plot ");

	for (Parametric2DFunction func:funcs)
	    {
		command.append (func.getXFunction ());
		command.append (',');
		command.append (func.getYFunction ());
		command.append (" title ");
		command.append (func.getName ());
		command.append (',');
	    }

	command.deleteCharAt (command.length () - 1);
	sendCommand ("set parametric");
	sendCommand (command.toString ());
    }

    /**
     * Plots funcs using the gnuplot command 'splot'.
     * This method uses setXRange(), setYRange(), setZRange(),
     * setURange(), and setVRange() to control the domain and range over
     * which the functions are plotted. Note that plotted functions may not
     * contain references to other user-defined functions or variables than
     * 'u' and 'v'.
     *
     * @author Giorgian Borca-Tasciuc
     * @param funcs The 3D Functions to plot, where
     * 	  x = f(u, v)
     * 	  y = g(u, v)
     * 	  z = h(u, v)
     * @see Parametric3DFunction
     * @throws NullPointerException if any of funcs is null.
     * @since 0.2
     */
    public void plot (final Parametric3DFunction ... funcs)
    {
	final StringBuilder command = new StringBuilder ("splot ");

	for (Parametric3DFunction func:funcs)
	    {
		command.append (func.getXFunction ());
		command.append (',');
		command.append (func.getYFunction ());
		command.append (',');
		command.append (func.getZFunction ());
		command.append (" title ");
		command.append (func.getName ());
		command.append (',');
	    }

	command.deleteCharAt (command.length () - 1);
	sendCommand ("set parametric");
	sendCommand (command.toString ());
    }

    /**
     * Plots funcs using the gnuplot command 'plot'.
     * This method uses setXRange(), setYRange(), setRRange(),
     * setTRange(), to control the domain and range over which the
     * functions are plotted. Note that the plotted functions may not
     * contain references to variables other than 't'.
     *
     * @author Giorgian Borca-Tasciuc
     * @param funcs The Polar functions to plot, where
     * 	  r = f(t)
     * @see PolarFunction
     * @throws NullPointerException if any of funcs is null.
     * @since 0.2
     */
    public void plot (final PolarFunction ... funcs)
    {
	final StringBuilder command = new StringBuilder ("plot ");

	for (PolarFunction func:funcs)
	    {
		command.append (func.getFunction ());
		command.append (" title ");
		command.append (func.getName ());
		command.append (',');
	    }

	command.deleteCharAt (command.length () - 1);
	sendCommand ("set polar");
	sendCommand (command.toString ());
    }

    /**
     * Returns the log in which all input set into gnuplot and all output
     * from gnuplot is logged.
     *
     * @author Giorgian Borca-Tasciuc
     * @return a logger to which all input into gnuplot and output from
     * 	   gnuplot.
     * @since 0.2
     */
    public Logger getLogger ()
    {
	return logger;
    }

    /**
     * Sets the title of the plot window created whenever a 'plot' or
     * 'splot' command is executed by gnuplot.
     *
     * @author Giorgian Borca-Tasciuc
     * @throws NullPointerException if title is null
     * @since 0.2
     */
    public void setTitle (final String title)
    {
	final String command =
	    formatter.format ("set title %s", title).toString ();

	sendCommand (command);
    }

    /**
     * Sets the style of the functions rendered whenever a 'plot' or
     * 'splot' command is executed by gnuplot.
     *
     * @author Giorgian Borca-Tasciuc
     * @param style One of the strings in FUNCTION_STYLES,
     * 	  representing the plotting style to be used by
     * 	  gnuplot.
     * @throws NullPointerException if style is null.
     * @throws IllegalArgumentException if style is not in
     * 	   PlotEngine.FUNCTION_STYLES.
     * @since 0.1
     */
    public void setFunctionStyle (final String style)
    {
	if (!Arrays.asList (FUNCTION_STYLES).contains (style))
	    throw new IllegalArgumentException ("\"" + style + "\"" +
						" is not a valid function style.");

	final String command =
	    formatter.format ("set style function %s", style).toString ();
	sendCommand (command);
    }

    /**
     * Sets the xrange for functions plotted with 'plot' or 'splot'.
     * For Normal2DFunctions and Normal3DFunctions, this method restricts
     * the x-domain.
     * Otherwise, this method restricts the x-range.
     *
     * @author Giorgian Borca-Tasciuc
     * @param xMin start of range of x values, inclusive.
     * @param xMax end of range of xvalues, inclusive.
     * @throws IllegalArgumentException if xMin or xmax are not rational
     * 	   numbers.
     * @since 0.1
     */
    public void setXRange (final double xMin, final double xMax)
    {
	if (!validDouble (xMin) || !validDouble (xMax))
	    throw new
		IllegalArgumentException ("xMin and xMax must be rational numbers.");

	final String command =
	    formatter.format ("set xrange [%f:%f]", min (xMin, xMax),
			      max (xMin, xMax)).toString ();

	sendCommand (command);
    }

    /** 
     * Sets the yrange for functions plotted with 'plot' or 'splot'.
     * For Normal3DFunctions, this method restricts the y-domain.
     * Otherwise, this method restricst the y-range.
     *
     * @author Giorgian Borca-Tasciuc
     * @param yMin start of range of y values, inclusive.
     * @param yMax end of range of y values, inclusive.
     * @throws IllegalArgumentException if yMin or yMax are not rational
     * 	   numbers.
     * @since 0.1
     */
    public void setYRange (final double yMin, final double yMax)
    {
	if (!validDouble (yMin) || !validDouble (yMax))
	    throw new
		IllegalArgumentException ("yMin and yMax must be rational numbers.");

	final String command =
	    formatter.format ("set yrange [%f:%f]", min (yMin, yMax),
			      max (yMin, yMax)).toString ();

	sendCommand (command);
    }

    /**
     * Sets the zrange for functions plotted with 'plot' or 'splot'.
     * For Normal3DFunctions and Normal3DFunctions, this method restricts
     * the z-range.
     *
     * @author Giorgian Borca-Tasciuc
     * @param zMin start of range of z values, inclusive.
     * @param zMax end of range of z values, inclusive.
     * @throws IllegalArgumentException if zMin or zMax are not rational
     * 	   numbers.
     * @since 0.1
     */
    public void setZRange (final double zMin, final double zMax)
    {
	if (!validDouble (zMin) || !validDouble (zMax))
	    throw new
		IllegalArgumentException ("zMin and zMax must be rational numbers.");

	final String command =
	    formatter.format ("set zrange [%f:%f]", min (zMin, zMax),
			      max (zMin, zMax)).toString ();

	sendCommand (command);
    }

    /**
     * Sets the urange for functions plotted with 'plot' or 'splot'.
     * For Parametric3DFunctions, this method restrics the u-domain.
     *
     * @author Giorgian Borca-Tasciuc
     * @param uMin start of range of u values, inclusive.
     * @param uMax end of range of u values, inclusive.
     * @throws IllegalArgumentException if uMin or uMax are not rational
     * 	   numbers.
     * @since 0.2
     */
    public void setURange (final double uMin, final double uMax)
    {
	if (!validDouble (uMin) || !validDouble (uMax))
	    throw new
		IllegalArgumentException ("uMin and uMax must be rational numbers.");

	final String command =
	    formatter.format ("set urange [%f:%f]", min (uMin, uMax),
			      max (uMin, uMax)).toString ();

	sendCommand (command);
    }

    /**
     * Sets the vrange for functions plotted with 'plot' or 'splot'.
     * For Parametric3DFunctions, this method restricts the v-domain.
     *
     * @author Giorgian Borca-Tasciuc
     * @param vMin start of range of u values, inclusive.
     * @param vMax end of range of u values, inclusive.
     * @throws IllegalArgumentException if vMin or vMax are not rational
     * 	   numbers.
     * @since 0.2
     */
    public void setVRange (final double vMin, final double vMax)
    {
	if (!validDouble (vMin) || !validDouble (vMax))
	    throw new
		IllegalArgumentException ("vMin and vMax must be rational numbers.");

	final String command =
	    formatter.format ("set vrange [%f:%f]", min (vMin, vMax),
			      max (vMin, vMax)).toString ();

	sendCommand (command);
    }

    /**
     * Sets the trange for functions plotted with 'plot' or 'splot'.
     * For Parametric2DFunctions, this method restricts the t-domain.
     *
     * @author Giorgian Borca-Tasciuc
     * @param tMin start of range of t values, inclusive.
     * @param tMax end of range of t values, exlusive.
     * @throws IllegalArgumentException if tMin and tMax are not rational
     * 	   numbers.
     * @since 0.2
     */
    public void setTRange (final double tMin, final double tMax)
    {
	if (!validDouble (tMin) || !validDouble (vMax))
	    throw new
		IllegalArgumentException ("tMin and tMax must be rational numbers.");
	
	final String command =
	    formatter.format ("set trange [%f:%f]", min (tMin, tMax),
			      max (tMin, tMax)).toString ();

	sendCommand (command);
    }

    /**
     * Sets the rrange for functions plotted with 'plot' or 'splot'.
     * For PolarFunctions, this method restricts the r-range.
     *
     * @author Giorgian Borca-Tasciuc
     * @param rMin start of range of r values, inclusive.
     * @param rMax end of range of r values, inclusive.
     * @throws IllegalArgumentException if rMin or rMax are not rational
     * 	   numbers.
     * @since 0.2
     */
    public void setRRange (final double rMin, final double rMax)
    {
	if (!validDouble (rMin) || !validDouble (rMax))
	    throw new
		IllegalArgumentException ("rMin and rMax must be rational numbers.");
	
	final String command =
	    formatter.format ("set rrange [%f:%f]", min (rMin, rMax),
			      max (rMin, rMax)).toString ();

	sendCommand (command);
    }
}
