package visualizers.tetrlais;

import messages.TetrlaisStateRequest;
import messages.TetrlaisStateResponse;
import rlVizLib.visualization.AbstractVisualizer;
import rlVizLib.visualization.VizComponent;

public class TetrlaisVisualizer  extends AbstractVisualizer {

	private TetrlaisStateResponse currentState = null;

	public TetrlaisVisualizer(){
		super();
		VizComponent theBlockVisualizer= new TetrlaisBlocksComponent(this);
		VizComponent theTetrlaisScoreViz = new TetrlaisScoreComponent(this);

		addVizComponentAtPositionWithSize(theBlockVisualizer,0,0,1.0,1.0);
		addVizComponentAtPositionWithSize(theTetrlaisScoreViz, 0,0,1.0,0.5);
	}

	public boolean updateAgentState() {
		TetrlaisStateResponse newState=TetrlaisStateRequest.Execute();
		if(!newState.equals(currentState)){
			currentState=newState;
			return true;
		}
		return false;
	}

	public int getWidth(){
		return currentState.getWidth();
	}

	public int getHeight(){

		return currentState.getHeight();
	}

	public int getScore(){
		return currentState.getScore();
	}

	public int [] getWorld(){
		return currentState.getWorld();
	}
	
	public int getEpisodeNumber(){
		return currentState.getEpisodeNumber();
	}
	public int getTimeStep(){
		return currentState.getTimeStep();
	}

	public int getTotalSteps() {
		return currentState.getTotalSteps();
	}
	
	public int getCurrentPiece(){
		return currentState.getCurrentPiece();
	}
	
}
