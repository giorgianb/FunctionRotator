package GNUPlot;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.stream.Collectors;
import java.util.List;
import java.util.ArrayList;
import java.nio.file.Files;

class GNUPlotProcessFactory
{
    public static Process getInstance ()
	throws ZipException, IOException
    {
	final File gnuplot = new File(getGNUPlot (getJar ()), "bin/gnuplot.exe");
	return new ProcessBuilder (gnuplot.getAbsolutePath ()).start ();
    }
	
    private static File getJar ()
    {
	final ProtectionDomain domain;
	final CodeSource source;
	
	domain = GNUPlotProcessFactory.class.getProtectionDomain ();
	source = domain.getCodeSource ();
	return new File (source.getLocation ().getFile ());
    }

    private static File getGNUPlot (final File jarFile)
	throws IOException
    {
	final ZipFile zipFile = new ZipFile (jarFile);
	final ZipEntry entry = zipFile.getEntry ("gnuplot/");
	final File destDir = Files.createTempDirectory ("gnuplot").toFile ();

	extract (zipFile, entry, destDir);
	return destDir;
    }
	
    private static void extract (final ZipFile zipFile, final ZipEntry entry, final File destDir)
	throws IOException
    {
	final List<ZipEntry> entries;
	if (entry.isDirectory ())
	    entries = zipFile.stream ()
		.filter (e -> e.getName ().startsWith (entry.getName ()))
		.collect (Collectors.toList ());
	else
	    {
		entries = new ArrayList <>();
		entries.add (entry);
	    }

	for (final ZipEntry file: entries)
	    {
		final File destFile = new File (destDir + File.separator + entry.getName ());
		if (entry.isDirectory ())
		    destFile.mkdir ();

		else
		    {
			try (final InputStream in = new BufferedInputStream (zipFile.getInputStream (entry));
			     final OutputStream out = new BufferedOutputStream (new FileOutputStream (destFile)))
			    {
				int c;

				while ((c = in.read ()) != -1)
				    out.write (c);
			    }
		    }
	    }
    }
}
	
