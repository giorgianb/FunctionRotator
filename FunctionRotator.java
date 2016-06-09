import java.awt.*;
import java.awt.event.*;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;
import java.util.Formatter;

class PlotEngine {
	public static final String STYLES[] = {"points", "linespoints", "filledcurves", "lines", "vectors", "pm3d"};

	private Process plotter;
	private PrintWriter fin;
	private InputStream fout;
	private InputStream ferr;
	private Set<String> funcNames;
	
	private static int min(int a, int b) {
		return (a < b) ? a : b;
	}

	private static int max(int a, int b) {
		return (a > b) ? a : b;
	}

	PlotEngine() throws IOException {
		plotter = new ProcessBuilder("gnuplot").start();
		fin = new PrintWriter(plotter.getOutputStream());
		fout = plotter.getInputStream();
		ferr = plotter.getErrorStream();
		funcNames = new TreeSet<>();

		fin.println("set parametric");
	}

	void plot3D(String... functionNames) {
		StringBuilder command = new StringBuilder("splot ");

		for (String functionName: functionNames) {
			Formatter formatter = new Formatter(new StringBuilder());
			formatter.format("u, %s(u)*cos(v), %s(u)*sin(v),", functionName, functionName);
			command.append(formatter.toString());
			formatter.close();
		}

		command.deleteCharAt(command.length() - 1); // Remove trailing comma
		fin.println(command.toString()); fin.flush(); }

	void plot3DAll() { 
		plot3D(Arrays.copyOf(funcNames.toArray(), funcNames.size(), String[].class));
	}

	void defineFunction(String name, String func, String varName) {
		fin.format("%s(%s)=%s\n", name, varName, func);
		funcNames.add(name);
	}

	void setXRange(int xmin, int xmax) {
		fin.format("set xrange [%d:%d]\n", min(xmin, xmax), max(xmin, xmax));
		fin.format("set urange [%d:%d]\n", min(xmin, xmax), max(xmin, xmax));
	}

	void setYRange(int ymin, int ymax) {
		fin.format("set yrange [%d:%d]\n", min(ymin, ymax), max(ymin, ymax));
		fin.format("set vrange [%d:%d]\n", min(ymin, ymax), max(ymin, ymax));
	}
	
	void setZRange(int zmin, int zmax) {
		fin.format("set zrange [%d:%d]\n", min(zmin, zmax), max(zmin, zmax));
	}

	void setFunctionStyle(String style) {
		fin.format("set style function %s\n", style);
	}	

	InputStream getErrorConsole() {
		return ferr;
	}
}

class FunctionBar extends Panel {
	private String funcName;
	private String varName;
	private TextArea func;
	private TextArea rotate;
	private Button delete;
	private Button plot3D;
	private Button plot2D;

	FunctionBar(String funcName, String varName, ActionListener plotter, ActionListener deleter) {
		this.funcName = funcName;
		this.varName = varName;
		setLayout(new FlowLayout());


		String tmpText = funcName + "(" + varName + ")=";
		TextArea tmp = new TextArea(tmpText, 1, tmpText.length(), TextArea.SCROLLBARS_NONE);
		tmp.setEditable(false);
		add(tmp);

		func = new TextArea("", 1, 10, TextArea.SCROLLBARS_NONE);
		add(func);

		tmpText = "x=";
		tmp = new TextArea(tmpText, 1, tmpText.length(), TextArea.SCROLLBARS_NONE);
		tmp.setEditable(false);
		add(tmp);

		rotate = new TextArea("", 1, 10, TextArea.SCROLLBARS_NONE);
		add(rotate);

		delete = new Button("Delete");
		delete.addActionListener(deleter);
		delete.setActionCommand(funcName);
		add(delete);

		plot3D = new Button("Plot3D");
		plot3D.addActionListener(plotter);
		plot3D.setActionCommand(funcName);
		add(plot3D);
	}

	String getFuncName() {
		return funcName;
	}

	String getVarName() {
		return varName;
	}

	String getFunc() {
		return func.getText();
	}

	String getRotate() {
		return rotate.getText();
	}

	public void revalidate() {
		super.revalidate();
		func.revalidate();
		rotate.revalidate();
		delete.revalidate();
		plot3D.revalidate();
	}

	public void repaint() {
		super.repaint();
		func.repaint();
		rotate.repaint();
		delete.repaint();
		plot3D.repaint();
	}
}

public class FunctionRotator {
	public static final String PROGRAM_NAME = "Function Rotator";
	private static final int FUNCTIONBAR_WIDTH = 5;
	private static final int FUNCTIONBAR_HEIGHT = 1;

	private static Frame appWindow;
	private static PlotEngine engine;
	private static List<FunctionBar> functionBars; 
	private static int curFunc = 0;

	private static GridBagConstraints makeConstraints(int gridx, int gridy, int width, int height) {
		GridBagConstraints c = new GridBagConstraints();

		c.gridx = gridx;
		c.gridy = gridy;
		c.gridwidth = width;
		c.gridheight = height;

		return c;
	}

	private static void setRanges(String var) {
		Frame prompt = new Frame("Set " + var + " range.");
		prompt.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				prompt.dispose();
			}
		});

		prompt.setLayout(new FlowLayout());
		TextArea t;
		TextArea min, max;

		String header = "min " + var + ":";
		t = new TextArea(header, 1, header.length(), TextArea.SCROLLBARS_NONE);
		t.setEditable(false);
		prompt.add(t);

		min = new TextArea("", 1, 5, TextArea.SCROLLBARS_NONE);
		prompt.add(min);

		header = "max " + var + ":";
		t = new TextArea(header, 1, header.length(), TextArea.SCROLLBARS_NONE);
		t.setEditable(false);
		prompt.add(t);

		max = new TextArea("", 1, 5, TextArea.SCROLLBARS_NONE);
		prompt.add(max);

		Button b;
		b = new Button("Done");
		b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int minRange = Integer.parseInt(min.getText());
				int maxRange = Integer.parseInt(max.getText());

				switch (var) {
					case "x":
						engine.setXRange(minRange, maxRange);
						break;
					case "y":
						engine.setYRange(minRange, maxRange);
						break;
					case "z":
						engine.setZRange(minRange, maxRange);
						break;
				}

				prompt.dispose();
			}
		});

		prompt.add(b);

		prompt.setSize(150, 150);
		prompt.setVisible(true);
	}

	private static void addFunctionBar() {
		ActionListener plotter = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (FunctionBar fb: functionBars)
					if (fb.getFuncName() == e.getActionCommand()) {
						engine.defineFunction(
								fb.getFuncName(),
								fb.getFunc() + "-" + fb.getRotate(),
								fb.getVarName()
								);
						break;
					}

				engine.plot3D(e.getActionCommand());
			}
		};

		ActionListener deleter = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeFunctionBar(e.getActionCommand());
			}
		};

		FunctionBar fb = new FunctionBar("f" + curFunc++, "x", plotter, deleter);
		functionBars.add(fb);

		appWindow.add(fb, makeConstraints(0, functionBars.size() - 1, FUNCTIONBAR_WIDTH, FUNCTIONBAR_HEIGHT));
		appWindow.revalidate();
		appWindow.repaint();
	}

	private static void removeFunctionBar(String funcName) {
		for (FunctionBar fb: functionBars)
			if (fb.getFuncName() == funcName) {
				functionBars.remove(fb);
				appWindow.remove(fb);
				break;
			}

		for (FunctionBar fb: functionBars) {
			fb.revalidate();
			fb.repaint();
		}

		appWindow.revalidate();
		appWindow.repaint();
	}

	public static void main(String args[]) throws IOException {
		appWindow = new Frame(PROGRAM_NAME);
		appWindow.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		appWindow.setLayout(new GridBagLayout());

		MenuBar mb = new MenuBar();
		Menu m = new Menu("Function");
		MenuItem mi = new MenuItem("New Function...");
		m.add(mi);
		mi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addFunctionBar();
			}
		});

		mi = new MenuItem("Plot All...");
		m.add(mi);
		mi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (FunctionBar fb: functionBars)
					engine.defineFunction(
							fb.getFuncName(),
							fb.getFunc() + "-" + fb.getRotate(),
							fb.getVarName()
							);


				engine.plot3DAll();
			}
		});

		mb.add(m);

		m = new Menu("Graph");
		Menu sm = new Menu("Style");
		for (String style: PlotEngine.STYLES) {
			mi = new MenuItem(style);

			mi.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					engine.setFunctionStyle(((MenuItem) e.getSource()).getLabel());
				}
			});
			sm.add(mi);
		}
		m.add(sm);

		mi = new MenuItem("Set X Range");
		mi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setRanges("x");
			}
		});
		m.add(mi);

		mi = new MenuItem("Set Y Range");
		mi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setRanges("y");
			}
		});
		m.add(mi);

		mi = new MenuItem("Set Z Range");
		mi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setRanges("z");
			}
		});
		m.add(mi);

		mb.add(m);

		Menu hm = new Menu("Help");
		mi = new MenuItem("View Error log...");
		mi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Frame errorLogWindow = new Frame("Error Log");
				errorLogWindow.addWindowListener(new WindowAdapter() {
					public void windowClosing(WindowEvent e) {
						errorLogWindow.dispose();
					}
				});

				TextArea errorLog = new TextArea();
				StringBuilder s = new StringBuilder();
				BufferedReader ferr = new BufferedReader(new InputStreamReader(engine.getErrorConsole()));
				
				try {
					while (ferr.ready())
						s.append(ferr.readLine() + '\n');
				} catch (IOException ie) {
					s.append("Internal Error: " + ie + '\n');
				}

				errorLog.setText(s.toString());
				errorLogWindow.add(errorLog);
				errorLogWindow.setSize(250, 250);
				errorLogWindow.setVisible(true);
			}
		});

		hm.add(mi);
		mb.add(hm);

		appWindow.setMenuBar(mb);

		engine = new PlotEngine();
		functionBars = new ArrayList<>();

		appWindow.setSize(500, 500);
		appWindow.setVisible(true);
	}
}
