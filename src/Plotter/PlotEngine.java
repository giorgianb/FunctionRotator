package Plotter;

import java.io.PrintWriter;
import java.io.IOException;
import java.io.InputStream;
import Plotter.UndefinedReferenceError;
import java.util.Formatter;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

public class PlotEngine
{
    private Process plotter;
    private PrintWriter in;
    private InputStream out;
    private InputStream err;

    public PlotEngine ()
	throws IOException
    {
	plotter = new ProcessBuilder ("gnuplot").start ();
	in = new PrintWriter (plotter.getOutputStream ());
	out = plotter.getInputStream ();
	err = plotter.getErrorStream ();
	type = FunctionType.NORMAL;
    }

    public void plot (final Normal2DSettings settings,
		      final Normal2DFunction... funcs)
    {
	plot (settings, Arrays.asList (func));
    }

    public void plot (final Normal2DSettings settings,
		      final List<Normal2DFunction> funcs)
	throws UndefinedReferenceError
    
    {
	final List<String> functionNames = initializeSession (settings, funcs);
	final StringBuilder command = new StringBuilder ("plot");
	final Formatter formatter = new Formatter (command);

	functionNames.stream ().forEach (name -> formatter.format (" %s(x),", name));
	command.setCharAt (command.length () - 1, '\n');
	sendCommand (command.toString ());
    }

    private void initializeSession (final Normal2DSettings settings,
				    final List<Normal2DFunction> funcs)
    {
	StringBuilder command = new StringBuider ();
	final Formatter formatter = new Formatter (command);

	formatter.format ("set xrange [%d:%d]\n", settings.getMinX (), settings.getMaxX ());
	formatter.format ("set yrange [%d:%d]\n", settings.getMinY (), settings.getMaxY ());
	formatter.format ("set %s\n", settings.getStyle ());
    }
	

	
	
