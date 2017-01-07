package GNUPlot;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.IOException;

public final class GNUPlot
{
    private Process gnuplot;
    private PrintWriter pin;
    private BufferedReader pout;
    private BufferedReader perr;
    private Logger log;

    public GNUPlot (final String sessionName)
	throws IOException
    {
	try
	    {
		gnuplot = GNUPlotProcessFactory.getInstance ();
	    }
	catch (final Exception e)
	    {
		throw new IOException ("Couldn't open gnuplot", e);
	    }
	
	pin = new PrintWriter (gnuplot.getOutputStream ());
	pout =
	    new BufferedReader (new InputStreamReader (gnuplot.getInputStream ()));
	perr =
	    new BufferedReader (new InputStreamReader (gnuplot.getErrorStream ()));
	log = Logger.getLogger (sessionName);
	log.setLevel (Level.ALL);
    }

    public void send (final String command)
	throws IOException
    {
	logOutput ();
	log.fine (command);
	pin.println (command);
	pin.flush ();
	logOutput ();
    }

    private void logOutput ()
	throws IOException
    {
	while (perr.ready ())
	    log.warning (perr.readLine ());

	while (pout.ready ())
	    log.info (pout.readLine ());
    }
}
	
