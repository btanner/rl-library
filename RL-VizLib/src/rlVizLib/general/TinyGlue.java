package rlVizLib.general;

import rlglue.RLGlue;
import rlglue.types.Reward_observation_action_terminal;

public class TinyGlue{
	boolean hasInited=false;
	boolean episodeOver=true;
	
	public void setInited(boolean initValue){
		hasInited=initValue;
	}
	
	//returns true of the episode is over
	public boolean step(){

		if(!hasInited){
			RLGlue.RL_init();
			hasInited=true;
		}

		if(episodeOver){
			RLGlue.RL_start();
			episodeOver=false;
		}else{
			Reward_observation_action_terminal whatHappened=RLGlue.RL_step();
			if(whatHappened.terminal==1){
				episodeOver=true;
			}
		}
		return episodeOver;
	}
}