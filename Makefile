JAVAC=javac
all:
	cd src && \
	$(JAVAC) FunctionRotator/FunctionRotator.java && \
	jar cfm FunctionRotator.jar Manifest.txt FunctionRotator/ GNUPlot/ Lexer/ Parser/
	mv src/FunctionRotator.jar ./
