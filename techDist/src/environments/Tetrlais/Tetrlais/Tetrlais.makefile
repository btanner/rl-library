#Makefile for compiling agents, enviroment and experiments

BIN = ../../../../bin/
CLASSPATH = $(BIN)/RL-Train.jar
JCC = javac

Tetrlais:
	$(JCC) -cp $(CLASSPATH) *.java
	cd .. && jar cvf ../../../bin/environments/Tetrlais.jar ./Tetrlais/*.class
	rm -f *.class
