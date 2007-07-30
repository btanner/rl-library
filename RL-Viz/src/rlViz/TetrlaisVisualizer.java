package rlViz;

import java.util.Vector;

import messages.MCStateRequest;
import messages.TetrlaisStateRequest;
import messages.TetrlaisStateResponse;
import rlVizLib.messaging.environment.EnvRangeRequest;
import rlVizLib.messaging.environment.EnvRangeResponse;

import rlglue.Observation;
import rlVizLib.interfaces.AgentOnValueFunctionDataProvider;
import rlVizLib.interfaces.ValueFunctionDataProvider;
import rlVizLib.visualization.AgentOnValueFunctionVizComponent;
import rlVizLib.visualization.EnvVisualizer;
import rlVizLib.visualization.VizComponent;
import vizComponents.CarOnMountainVizComponent;
import vizComponents.TetrlaisBlocksComponent;
import vizComponents.ValueFunctionVizComponent;

public class TetrlaisVisualizer  extends EnvVisualizer {
	
	private TetrlaisStateResponse currentState = null;

	public TetrlaisVisualizer(){
		super();
		VizComponent theTetrlaisVisualizer=(VizComponent) new TetrlaisBlocksComponent(this);
		super.addVizComponentAtPositionWithSize(theTetrlaisVisualizer,0,.5,1.0,.5);
		VizComponent theTetrlaisScoreViz = (VizComponent) new TetrlaisScoreComponent(this);
		super.addVizComponentAtPositionWithSize(theTetrlaisScoreViz, 0,0,1.0,0.5);
	}
	
	public void updateAgentState() {
		currentState=TetrlaisStateRequest.Execute();
	}

	public int getWidth(){
		return this.currentState.getWidth();
	}
	
	public int getHeight(){

		return this.currentState.getHeight();
	}
	
	public int getScore(){
		return this.currentState.getScore();
	}
	
	public int [] getWorld(){
		return this.currentState.getWorld();
	}

}
