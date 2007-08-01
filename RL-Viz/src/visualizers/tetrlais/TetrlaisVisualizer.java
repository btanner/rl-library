package visualizers.tetrlais;

import messages.TetrlaisStateRequest;
import messages.TetrlaisStateResponse;
import rlVizLib.visualization.EnvVisualizer;
import rlVizLib.visualization.VizComponent;

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
