EXPERIMENT = src/consoleTrainer.py


# Path to RL-Glue to find RL_interface and related files.
RL_GLUE_PATH = ../../RL-Glue/RL-Glue

all: RL_experiment

include $(RL_GLUE_PATH)/RL_glue.makefile

ParameterHolder.py: $(RL_GLUE_PATH)/../../includes/ParameterHolder.py
	cp $^ $@

consoleTrainerHelper.py: src/consoleTrainerHelper.py
	cp $^ $@

RL_experiment: $(EXPERIMENT) RL_network.py RL_common.py ParameterHolder.py consoleTrainerHelper.py
	cp $(EXPERIMENT) $@
	cp $(RL_GLUE_PATH)/Python/RL_client_experiment.py $@.py
	chmod u+x $@
	
clean:
	rm -f RL_experiment RL_experiment.py RL_network.py RL_common.py ParameterHolder.py consoleTrainerHelper.py