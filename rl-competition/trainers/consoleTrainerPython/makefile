EXPERIMENT = src/consoleTrainer.py


# Path to RL-Glue to find RL_interface and related files.
RL_GLUE_PATH = ../../system/RL-Glue/RL-Glue
INCLUDES_PATH = ../../system/includes
all: RL_experiment

include $(RL_GLUE_PATH)/RL_glue.makefile

ParameterHolder.py: $(INCLUDES_PATH)/ParameterHolder.py
	cp $^ $@

consoleTrainerHelper.py: src/consoleTrainerHelper.py
	cp $^ $@

RL_experiment: $(EXPERIMENT) RL_network.py RL_common.py ParameterHolder.py consoleTrainerHelper.py
	cp $(EXPERIMENT) $@
	cp $(RL_GLUE_PATH)/Python/RL_client_experiment.py $@.py
	chmod u+x $@
	
clean:
	rm -f *.pyc RL_experiment RL_experiment.py RL_network.py RL_common.py ParameterHolder.py consoleTrainerHelper.py
