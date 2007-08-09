BIN = ../../../../bin/
CLASSPATH = $(BIN)/RL-Train.jar
JCC = javac

Helicopter:
	$(JCC) -cp $(CLASSPATH) *.java
	cd .. && jar cvf ../../../bin/environments/Helicopter.jar ./Helicopter/*.class

