JCC = javac
BIN = ../../../bin
CLASSPATH = $(BIN)/RL-Train.jar

Experiment:
	$(JCC) -cp $(CLASSPATH) *.java -d $(BIN)/trainingExperiments/trainJava/
