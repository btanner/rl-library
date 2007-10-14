
RL-GLUE = ../../RL-Glue/RL-Glue
SRC_PATH = .
BIN_PATH = .

all: 
	javac $(SRC_PATH)/*.java -d $(BIN_PATH) -cp $(RL-GLUE)/Java/RL-Glue.jar

RL_experiment:
	javac $(SRC_PATH)/OLExperiment.java -d $(BIN_PATH)/ -cp $(RL-GLUE)/Java/RL-Glue.jar

clean:
	-rm -rf *.class
