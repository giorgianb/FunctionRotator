package Plotter;

import java.io.PrintWriter;
import java.io.IOException;
import java.io.InputStream;

public class Plot
{
    enum Styles
    {
	POINTS, LINESPOINTS, FILLEDCURVES, LINES, VECTORS, PM3D
    }

    enum Types
    {
	NORMAL_2D, NORMAL_3D, PARAMETRIC_2D, PARAMETRIC_3D, POLAR
    }
    
    private Process plotter;
    private PrintWriter in;
    private InputStream out;
    private InputStream err;

    public Plot ()
	throws IOException
    {
	plotter = new ProcessBuilder ("gnuplot").start ();
	in = new PrintWriter (plotter.getOutputStream ());
	out = plotter.getInputStream ();
	err = plotter.getErrorStream ();
    }

    public void plot (final PlotSettings ps, final List<Function> functions)
    {
	StringBuilder sb;
	switch (ps.getPlotType ())
	    {
		
