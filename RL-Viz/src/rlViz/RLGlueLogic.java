package rlViz;

import general.RLVizVersion;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import messaging.environment.EnvVersionSupportedRequest;
import messaging.environment.EnvVersionSupportedResponse;
import messaging.environmentShell.EnvShellListRequest;
import messaging.environmentShell.EnvShellListResponse;
import messaging.environmentShell.EnvShellLoadRequest;
import rlglue.RLGlue;
import rlglue.Reward_observation_action_terminal;
import visualization.EnvVisualizer;

public class RLGlueLogic {

//Singleton pattern, should make sure its thread safe
	static RLGlueLogic theGlobalGlueLogic=null;

	static RLGlueLogic getGlobalGlueLogic(){
		if(theGlobalGlueLogic==null)theGlobalGlueLogic=new RLGlueLogic();
		
		return theGlobalGlueLogic;
	}
	
	boolean debugLocal=false;

	TinyGlue myGlueState=null;

	EnvVisualizer theEnvVisualizer=null;
	
	Timer currentTimer=null;

	private RLVizVersion theEnvVersion=null;
	
	private RLGlueLogic(){
		myGlueState=new TinyGlue();
	}
	
	public RLVizVersion getEnvVersion(){
		return theEnvVersion;
	}

	public void step(){
		//This is not ideal.. getting bad fast
		if(theEnvVisualizer!=null)
			if(!theEnvVisualizer.isCurrentlyRunning()){
				theEnvVisualizer.startVisualizing();
			}
		myGlueState.step();
	}

	public Vector<String> getEnvList(){
		if(debugLocal){
			Vector<String> theList=new Vector<String>();
			theList.add("Env 1");
			theList.add("Env 2");
			return theList;
		}
		//Get the Environment Names
		EnvShellListResponse theEnvListResponseObject=EnvShellListRequest.Execute();
		return theEnvListResponseObject.getTheEnvList();
	}
	public Vector<String> getAgentList(){
		//don't have an agent list loading mechanism yet
		if(debugLocal||true){
			Vector<String> theList=new Vector<String>();
			theList.add("Agent 1");
			theList.add("Agent 2");
			theList.add("Agent 3");
			return theList;
		}
//		//Get the Environment Names
//		EnvShellListResponse theEnvListResponseObject=EnvShellListRequest.Execute();
//		return theEnvListResponseObject.getTheEnvList();
		return null;
	}

	public void loadEnvironment(String envName) {
		EnvShellLoadRequest.Execute(envName);
		EnvVersionSupportedResponse versionResponse=EnvVersionSupportedRequest.Execute();
		
		//this shouldn't happen anyway
		if(versionResponse!=null)
			theEnvVersion=versionResponse.getTheVersion();
		else
			theEnvVersion=RLVizVersion.NOVERSION;
	}

	public void setVisualizer(EnvVisualizer theEVisualizer) {
		this.theEnvVisualizer=theEVisualizer;
	}

	public void start(int period) {
	    currentTimer = new Timer();

	    
	    currentTimer.scheduleAtFixedRate(new TimerTask() {
	            public void run() {
	            	step();
	            }
	        }, 0, period);		
	}

	public void stop() {
		if(currentTimer!=null){
			currentTimer.cancel();
			currentTimer=null;
		}
		
	}

	public void setNewStepDelay(int stepDelay) {
		stop();
		start(stepDelay);
	}

	public void setNewValueFunctionResolution(int theValue) {
		if(theEnvVisualizer!=null)
			theEnvVisualizer.setValueFunctionResolution(theValue);
	}

}


class TinyGlue{
	boolean hasInited=false;
	boolean episodeOver=true;
	void step(){

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
	}
}
