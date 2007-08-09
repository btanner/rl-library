RLGLUE = ../../../rl-glue/RL-Glue
BIN = ../../../bin

CXX = g++
CXXFLAGS = -ansi -pedantic -Wall -I$(RLGLUE)
LDXFLAGS = 

trainCpp: trainCpp.o ParameterHolder.o RL_client_experiment.o RL_network.o
	$(CXX) $(BIN)/trainCpp.o $(BIN)/ParameterHolder.o $(BIN)/RL_client_experiment.o $(BIN)/RL_network.o -o $@ $(LDXFLAGS)

trainCpp.o: trainCpp.cpp 
	$(CXX) -c $(CXXFLAGS) $< -o $(BIN)/$@

ParameterHolder.o: ParameterHolder.cpp ParameterHolder.h
	$(CXX) -c $(CXXFLAGS) $< -o $(BIN)/$@

RL_client_experiment.o: $(RLGLUE)/Network/Experiment/RL_client_experiment.c
	$(CXX) -c $(CXXFLAGS) $< -o $(BIN)/$@

RL_network.o: $(RLGLUE)/Network/RL_network.c $(RLGLUE)/Network/RL_network.h
	$(CXX) -c $(CXXFLAGS) $< -o $(BIN)/$@

clean:
	rm -fr trainCpp
