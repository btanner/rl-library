# Agent Source Files
AGENT = RandomAgent
AGENT_PATH = ./src

# Path to RL-Glue to find RL_interface and related files.
RL_GLUE_PATH = ../../system/RL-Glue/RL-Glue

all: RL_agent

include $(RL_GLUE_PATH)/RL_glue.makefile

RL_agent:
	echo -e "#! /bin/bash\n PYTHONPATH=$(RL_GLUE_PATH)/Python:$(AGENT_PATH) python -c \"import rlglue.agent.AgentLoader\" $(AGENT)" | cat > run.bash
	chmod u+x run.bash

clean:
	rm -f RL_agent *.pyc $(AGENT_PATH)/*.pyc