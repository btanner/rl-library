RLGLUE = ../../system/RL-Glue/RL-Glue
EXTERNALSRC = ../../system/includes
BIN = bin
SRC = src

CXX = g++
CXXFLAGS = -ansi -pedantic -Wall -I$(RLGLUE) -I$(EXTERNALSRC)
LDXFLAGS = 

consoleTrainer: consoleTrainer.o ParameterHolder.o RL_client_experiment.o RL_network.o consoleTrainerHelper.o
	$(CXX) $(BIN)/consoleTrainer.o $(BIN)/consoleTrainerHelper.o $(BIN)/ParameterHolder.o $(BIN)/RL_client_experiment.o $(BIN)/RL_network.o -o $(BIN)/$@ $(LDXFLAGS)
	rm -fr $(BIN)/*.o

consoleTrainer.o: $(SRC)/consoleTrainer.cpp  $(SRC)/consoleTrainer.h
	$(CXX) -c $(CXXFLAGS) $< -o $(BIN)/$@


consoleTrainerHelper.o : $(SRC)/consoleTrainerHelper.cpp $(SRC)/consoleTrainerHelper.h
	$(CXX) -c $(CXXFLAGS) $< -o $(BIN)/$@

ParameterHolder.o: $(EXTERNALSRC)/ParameterHolder.cpp $(EXTERNALSRC)/ParameterHolder.h
	$(CXX) -c $(CXXFLAGS) $< -o $(BIN)/$@

RL_client_experiment.o: $(RLGLUE)/Network/Experiment/RL_client_experiment.c
	$(CXX) -c $(CXXFLAGS) $< -o $(BIN)/$@

RL_network.o: $(RLGLUE)/Network/RL_network.c $(RLGLUE)/Network/RL_network.h
	$(CXX) -c $(CXXFLAGS) $< -o $(BIN)/$@

clean:
	rm -fr $(BIN)/*
