package FunctionRotator;

import GNUPlot.GNUPlot;
import Parser.Expression;
import Parser.ExpressionStream;
import Parser.ParseError;
import Lexer.TokenStream;

import java.util.logging.Logger;
import java.util.logging.StreamHandler;
import java.util.logging.SimpleFormatter;
import java.util.logging.Level;
import java.util.Formatter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.awt.Frame;
import java.awt.Dialog;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.TextField;
import java.awt.TextArea;
import java.awt.Choice;
import java.awt.Label;
import java.awt.Button;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Arrays;

public final class FunctionRotator
{
    private static Choice plotStyle;
    private static Map<String, TextField> functions;
    private static Map<String, TextField> axes;
    
    private static TextField xMin, xMax;
    private static TextField yMin, yMax;
    private static TextField zMin, zMax;


    private static GNUPlot gnuplot;
    private static Frame appWindow;
    private static ByteArrayOutputStream log;
    private static StreamHandler logHandler;

    private static final int NUMBER_FUNCTIONS = 10;
    private static final String PROGRAM_NAME = "Function Rotator";

    public static void main (final String args[])
    {
	try
	    {
		gnuplot = new GNUPlot ("gnuplot");
	    }
	catch (final IOException e)
	    {
		alertUser (e.getMessage ());
	    }

	log = new ByteArrayOutputStream ();
	logHandler = new StreamHandler (log, new SimpleFormatter ());
	logHandler.setLevel (Level.ALL);
	Logger.getLogger ("gnuplot").addHandler (logHandler);
	
	appWindow = new Frame (PROGRAM_NAME);
	appWindow.addWindowListener (new WindowAdapter ()
	    {
		public void windowClosing (final WindowEvent e)
		{
		    System.exit (0);
		}
	    });
	appWindow.setLayout (new GridBagLayout ());

	Button b;
	int x, y;
	x = y= 0;
	b = new Button ("Plot");
	b.addActionListener (new ActionListener ()
	    {
		public void actionPerformed (final ActionEvent e)
		{
		    plot ();
		}
	    });
	appWindow.add (b, makeConstraints (x, y, 1, 1));
	x += 1;

	b = new Button ("Rotate");
	b.addActionListener (new ActionListener ()
	    {
		public void actionPerformed (final ActionEvent e)
		{
		    rotate ();
		}
	    });
	appWindow.add (b, makeConstraints (x, y,  1, 1));
	x += 1;


	b = new Button ("View Log");
	b.addActionListener (new ActionListener ()
	    {
		public void actionPerformed (final ActionEvent e)
		{
		    viewLog ();
		}
	    });
	appWindow.add (b, makeConstraints (x, y, 2, 1));
	x += 2;
	
	// Style selector
	Label l = new Label ("Style:");
	appWindow.add (l, makeConstraints (x, y, 1, 1));
	x += 1;

	plotStyle = new Choice ();
	final String styles[] =
	    {"lines", "points", "linespoints", "filledcurves", "vectors", "pm3d"};
	for (final String style: styles)
	    plotStyle.add (style);
	appWindow.add (plotStyle, makeConstraints (x, y, 3, 1));
	x += 3;
	
	++y;
	x = 0;
	// Add x range
	l = new Label ("x:");
	appWindow.add (l, makeConstraints (x, y, 1, 1));
	x += 1;

	xMin = new TextField ("-10", 3);
	appWindow.add (xMin, makeConstraints (x, y, 1, 1));
	x += 1;
	
	l = new Label ("-");
	appWindow.add (l, makeConstraints (x, y, 1, 1));
	x += 1;
	
	xMax = new TextField ("10", 3);
	appWindow.add (xMax, makeConstraints (x, y, 1, 1));
	x += 1;
	
	++y;
	x = 0;
	// Add y range
	l = new Label ("y:");
	appWindow.add (l, makeConstraints (x, y, 1, 1));
	x += 1;

	yMin = new TextField ("-10", 3);
	appWindow.add (yMin, makeConstraints (x, y, 1, 1));
	x += 1;
	
	l = new Label ("-");
	appWindow.add (l, makeConstraints (x, y, 1, 1));
	x += 1;
	
	yMax = new TextField ("10", 3);
	appWindow.add (yMax, makeConstraints (x, y, 1, 1));
	x += 1;

	++y;
	x = 0;
	// Add z range
	l = new Label ("z:");
	appWindow.add (l, makeConstraints (x, y, 1, 1));
	x += 1;

	zMin = new TextField ("-10", 3);
	appWindow.add (zMin, makeConstraints (x, y, 1, 1));
	x += 1;
	
	l = new Label ("-");
	appWindow.add (l, makeConstraints (x, y, 1, 1));
	x += 1;
	
	zMax = new TextField ("10", 3);
	appWindow.add (zMax, makeConstraints (x, y, 1, 1));
	x += 1;

	functions = new HashMap<> ();
	axes = new HashMap<> ();
	for (int i = 0; i < NUMBER_FUNCTIONS; ++i)
	    {
		x = 0;
		++y;
		final String name = "Y" + i;
		l = new Label (name + "=");
		appWindow.add (l, makeConstraints (x, y, 1, 1));
		x += 1;
		
		final TextField function = new TextField ("", 20);
		functions.put (name, function);
		appWindow.add (function, makeConstraints (x, y, 4, 1));
		x += 4;

		l = new Label ("y=");
		appWindow.add (l, makeConstraints (x, y, 1, 1));
		x += 1;
				
		final TextField axis = new TextField ("0", 3);
		axes.put (name, axis);
		appWindow.add (axis, makeConstraints (x, y, 1, 1));
		x += 1;
	    }

	appWindow.setSize (500, 500);
	appWindow.pack ();
	appWindow.setVisible (true);
    }

    private static void plot ()
    {
	
	final Set<String> toProcess = getToProcess ();


	try
	    {
		assertFunctionsValid (toProcess);
		defineFunctions (toProcess);
		assertRangesValid ();
		defineRanges ();

		final StringBuilder command = new StringBuilder ();
		final Formatter fmt = new Formatter (command);

		fmt.format ("unset parametric\n");
		fmt.format ("set style function %s\n", plotStyle.getSelectedItem ());
		fmt.format ("plot");
		toProcess.stream ().forEach (funcName -> fmt.format (" %s(x),", funcName));
		command.setCharAt (command.length () - 1, '\n');

		gnuplot.send (command.toString ());
	    }
	catch (final Exception e)
	    {
		alertUser (e.getMessage ());
		return;
	    }
    }

    private static void rotate ()
    {
	final Set<String> toProcess = getToProcess ();

	try
	    {
		assertFunctionsValid (toProcess);
		defineFunctions (toProcess);
		assertAxesValid (toProcess);
		assertRangesValid ();
		defineRanges ();
		final StringBuilder command = new StringBuilder ();
		final Formatter fmt = new Formatter (command);

		fmt.format ("set parametric\n");
		fmt.format ("set style function %s\n", plotStyle.getSelectedItem ());
		fmt.format ("splot");
		
		final String format = " u, (%s(u)+%f)*cos(v), (%s(u)+%f)*sin(v)+%f title \"%s\",";
		for (final String funcName: toProcess)
		    {
			final double axisValue = Double.parseDouble (axes.get (funcName)
								     .getText ());
			fmt.format (format,
				    funcName,
				    axisValue,
				    funcName,
				    axisValue,
				    axisValue,
				    funcName);
		    }
		
		command.setCharAt (command.length () - 1, '\n');

		gnuplot.send (command.toString ());
		
	    }
	catch (final Exception e)
	    {
		alertUser (e.getMessage ());
		return;
	    }
    }

    private static void viewLog ()
    {
	final Frame logWindow = new Frame ("Log");
	logWindow.addWindowListener (new WindowAdapter ()
	    {
		public void windowClosing (final WindowEvent we)
		{
		    logWindow.dispose ();
		}
	    });

	logHandler.flush ();
	final TextArea messageArea = new TextArea (log.toString ());
	messageArea.setEditable (false);

	logWindow.add (messageArea);
	logWindow.pack ();
	logWindow.setVisible (true);
    }

    private static Set<String> getToProcess ()
    {
	final Set<String> toProcess = new HashSet<> ();
	for (final Map.Entry<String, TextField> entry: functions.entrySet ())
	    if (!entry.getValue ().getText ().equals (""))
		toProcess.add (entry.getKey ());

	return toProcess;
    }
	
    private static void assertFunctionsValid (final Set<String> toProcess)
	throws Exception
    {
	for (final String funcName: toProcess)
	    {
		final String function = functions.get (funcName).getText () + ";";
		// Make sure function contains one expression
		if (function.indexOf (";") != function.length () - 1)
		    throw new Exception ("Unexpected character ';' in " + funcName + ".");
		// Make sure function does not assign
		if (function.indexOf ("=") != -1)
		    throw new Exception ("Unexpected character '=' in " + funcName + ".");
		
		final ByteArrayInputStream in =
		    new ByteArrayInputStream (function.getBytes ());
		final ExpressionStream expIn = new ExpressionStream (new TokenStream (in));
		try
		    {
			final Expression exp = expIn.read ();
			assertNoInvalidReferences (exp, toProcess);
		    }
		catch (final ParseError pe)
		    {
			throw new Exception (funcName + ": " + pe.getMessage (), pe);
		    }
		catch (final Exception e)
		    {
			throw new Exception (funcName + ": " + e.getMessage (), e);
		    }
	    }
    }

    private static void assertNoInvalidReferences (final Expression exp,
						   final Set<String> toProcess)
	throws Exception
    {
	final Set<String> preDef = new HashSet<> (Arrays.asList (new String[]
		{
	    "+", "-", "*", "/", "^", "sin", "cos", "tan", "log", "atan", "acos", "asin", "exp"
		}));

	switch (exp.getType ())
	    {
	    case NAME:
		// Only allowed predefined names
		if (!exp.getName ().equals ("pi") && !exp.getName ().equals ("x"))
		    throw new Exception ("reference to undefined name '" + exp.getName () + "'.");
		break;
	    case OPERATOR:
		final String op = exp.getOperator ();
		if (!toProcess.contains (op) && !preDef.contains (op))
		    throw new Exception ("reference to undefined operator " + "'.");

		for (final Expression e: exp.getOperands ())
		    assertNoInvalidReferences (e, toProcess);
		
		break;
	    }
    }

    private static void assertRangesValid ()
	throws Exception
    {
	final String names[] = {
	    "x lower bound", "x upper bound",
	    "y lower bound", "y upper bound",
	    "z lower bound", "z upper bound"
	};

	final String values[] = {
	    xMin.getText (), xMax.getText (),
	    yMin.getText (), yMax.getText (),
	    zMin.getText (), xMax.getText ()
	};
	    

	for (int i = 0; i < names.length; ++i)
	    try
		{
		    Double.parseDouble (values[i]);
		}
	    catch (final Exception e)
		{
		    throw new Exception ("Syntax error in " + names[i] + " (must be a number)");
		}
    }

    private static void assertAxesValid (final Set<String> toProcess)
	throws Exception
    {
	for (final String funcName: toProcess)
	    try
		{
		    Double.parseDouble (axes.get (funcName).getText ());
		}
	    catch (final Exception e)
		{
		    throw new Exception ("Syntax error in " + funcName + "axis (must be a number)");
		}
    }
    
    private static void defineFunctions (final Set<String> toProcess)
	throws IOException
    {
	final StringBuilder command = new StringBuilder ();
	final Formatter fmt = new Formatter (command);
	for (final String funcName: toProcess)
	    {
		final String value = functions.get (funcName).getText ().replace ("^", "**");
		fmt.format ("%s(x) = (%s)\n", funcName, value);
	    }

	gnuplot.send (command.toString ());
    }

    private static void defineRanges ()
	throws IOException
    {
	final StringBuilder command = new StringBuilder ();
	final Formatter fmt = new Formatter (command);

	final double minX = Double.parseDouble (xMin.getText ());
	final double maxX = Double.parseDouble (xMax.getText ());

	final double minY = Double.parseDouble (yMin.getText ());
	final double maxY = Double.parseDouble (yMax.getText ());

	final double minZ = Double.parseDouble (zMin.getText ());
	final double maxZ = Double.parseDouble (zMax.getText ());

	fmt.format ("set xrange [%f:%f]\n", minX, maxX);
	fmt.format ("set yrange [%f:%f]\n", minY, maxY);
	fmt.format ("set zrange [%f:%f]\n", minZ, maxZ);
	gnuplot.send (command.toString ());
    }

    private static void alertUser (final String message)
    {
	final Dialog dialog = new Dialog (appWindow, true);

	dialog.addWindowListener (new WindowAdapter ()
	    {
		public void windowClosing (final WindowEvent we)
		{
		    dialog.dispose ();
		}
	    });
				  
	dialog.setTitle ("Error");
	dialog.add (new Label (message));
	dialog.pack ();
	dialog.setVisible (true);
    }

    private static GridBagConstraints makeConstraints(final int x,
						      final int y,
						      final int width,
						      final int height)
    {
	final GridBagConstraints c = new GridBagConstraints ();

	c.gridx = x;
	c.gridy = y;
	c.gridwidth = width;
	c.gridheight = height;

	return c;
    }
						
}
