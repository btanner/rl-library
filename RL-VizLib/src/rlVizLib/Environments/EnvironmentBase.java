package rlVizLib.Environments;

import rlglue.environment.Environment;
import rlglue.types.Observation;
import rlglue.types.Reward_observation;


public abstract class EnvironmentBase implements Environment {
	abstract protected Observation makeObservation();
	
	protected Reward_observation makeRewardObservation(double reward, boolean isTerminal){
		Reward_observation RO=new Reward_observation();
		RO.o=makeObservation();
		RO.r=reward;
		
		RO.terminal=1;
		if(!isTerminal)
			RO.terminal=0;

		return RO;
	}
}
