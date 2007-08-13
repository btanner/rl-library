all:
	make -f Agent.makefile
	make -f Environment.makefile
	make -f Experiment.makefile
	make -f RLGlue.makefile

clean:
	rm -f bin/RL_glue
	rm -f bin/agents/GenericSarsaLambdaJava/GenericSarsaLambda/*.class
	rm -f bin/environments/*.jar
	rm -f bin/build/*
	rm -f trainingExperiments/trainJava/* 
