
RLVizPath = ../../system/libraries
SRC_PATH = src
BIN_PATH = bin


all: 
	javac $(SRC_PATH)/*.java -d $(BIN_PATH) -cp $(RLVizPath)/RLVizLib.jar

RL_experiment:
	javac $(SRC_PATH)/realTimeStrategyTrainerJava.java -d $(BIN_PATH)/ -cp $(RLVizPath)/RLVizLib.jar

clean:
	-rm -rf $(BIN_PATH)/*.class
