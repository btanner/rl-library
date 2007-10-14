
RL-GLUE = ../../libs/rl-glue/src
SRC_PATH = .
BIN_PATH = .

all: 
	javac $(SRC_PATH)/*.java -d $(BIN_PATH) -cp $(RL-GLUE)/Java/RL-Glue.jar

RL_agent:
	javac $(SRC_PATH)/OLAgent.java -d $(BIN_PATH)/ -cp $(RL-GLUE)/Java/RL-Glue.jar

RL_experiment:
	javac $(SRC_PATH)/OLExperiment.java -d $(BIN_PATH)/ -cp $(RL-GLUE)/Java/RL-Glue.jar

OLJ:
	javac $(SRC_PATH)/Parameters.java -d $(BIN_PATH)

clean:
	-rm -rf *.class
