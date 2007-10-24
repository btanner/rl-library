.PHONY: RL_experiment
# Experiment Source Files
EXPERIMENT = consoleTrainer
EXPERIMENT_PATH = ./src

# Path to RL-Glue to find RL_interface and related files.
RL_GLUE_PATH = ../../system/RL-Glue/RL-Glue
INCLUDE_PATH = ../../system/includes

all: RL_experiment

include $(RL_GLUE_PATH)/RL_glue.makefile

RL_experiment:
	echo -e "#! /bin/bash\n  PYTHONPATH=$(RL_GLUE_PATH)/Python:$(INCLUDE_PATH):$(EXPERIMENT_PATH) python $(EXPERIMENT_PATH)/$(EXPERIMENT).py " | cat > RL_experiment
	chmod u+x RL_experiment

clean:
	rm -f RL_experiment *.pyc $(EXPERIMENT_PATH)/*.pyc