JAVAC=javac
win32:
	cd src && \
	$(JAVAC) -cp . FunctionRotator/FunctionRotator.java && \
	jar cfm FunctionRotator.jar Manifest.txt FunctionRotator/ GNUPlot/ Lexer/ Parser/
	mv src/FunctionRotator.jar ./
	mv gnuplot/win32 gnuplot/gnuplot
	jar uf FunctionRotator.jar -C gnuplot gnuplot
	mv gnuplot/gnuplot gnuplot/win32

win64:
	cd src && \
	$(JAVAC) -cp . FunctionRotator/FunctionRotator.java && \
	jar cfm FunctionRotator.jar Manifest.txt FunctionRotator/ GNUPlot/ Lexer/ Parser/
	mv src/FunctionRotator.jar ./
	mv gnuplot/win64 gnuplot/gnuplot
	jar uf FunctionRotator.jar -C gnuplot gnuplot
	mv gnuplot/gnuplot gnuplot/win64
clean:
	rm -f FunctionRotator.jar
	find . -name '*.class' -exec rm {} +
