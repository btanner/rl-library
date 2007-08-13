# Makefile for compiling agents, enviroment and experiments
BIN = ../../../../bin
CLASSPATH = $(BIN)/RL-Train.jar
JCC = javac

MountainCar:
	$(JCC) -cp $(CLASSPATH) *.java
	cd .. && jar cvf ../../../bin/environments/MountainCar.jar ./MountainCar/*.class
	rm -f *.class
