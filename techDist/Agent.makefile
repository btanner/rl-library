#Makefile for compiling agents, enviroment and experiments

BIN = ./bin
SRC = ./src
CLASSPATH = $(BIN)/RL-Train.jar

GenericSarsaLambdaPath = agents/GenericSarsaLambdaJava/

JCC = javac

GenericSarsaLambda: GenericSarsaLambda.class

GenericSarsaLambda.class: $(SRC)/$(GenericSarsaLambdaPath)/GenericSarsaLambda/GenericSarsaLambda.java
	$(JCC) -cp $(CLASSPATH) $< -d $(BIN)/$(GenericSarsaLambdaPath)

	cd ./src/agents/RandomAgentCPP && make


