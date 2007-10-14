RL-GLUE = ./rl-glue/RL-Glue
BUILD_PATH = ../obj/obj.rlglue
BIN_PATH = ..
SRC_PATH = .

CC      = g++  
CFLAGS  = -I$(RL-GLUE)/ -ansi -pedantic -Wall
LDFLAGS =

EXPERIMENT_OBJECTS = rlglue_exp.o RL_client_experiment.o RL_network_experiment.o

RL_experiment: $(EXPERIMENT_OBJECTS)
	$(CC) -o $(BIN_PATH)/$@ $(addprefix $(BUILD_PATH)/, $(EXPERIMENT_OBJECTS))
	
rlglue_exp.o: $(SRC_PATH)/rlglue_exp.C
	$(CC) -c $(CFLAGS) $< -o $(BUILD_PATH)/$@

RL_client_experiment.o: $(RL-GLUE)/Network/Experiment/RL_client_experiment.c
	$(CC) -c $(CFLAGS) $< -o $(BUILD_PATH)/$@
	
RL_network_experiment.o: $(RL-GLUE)/Network/RL_network.c
	$(CC) -c $(CFLAGS) $< -o $(BUILD_PATH)/$@


