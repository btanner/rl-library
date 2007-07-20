import java.awt.Dimension;
import java.awt.Point;
import java.util.Vector;

import javax.swing.JFrame;

import messaging.agent.AgentValueForObsRequest;
import messaging.agent.AgentValueForObsResponse;
import messaging.environment.EnvObsForStateRequest;
import messaging.environment.EnvObsForStateResponse;

import rlglue.Observation;


public class RLVizFrame extends JFrame implements ValueFunctionDataProvider {

	public double getMaxValueForDim(int whichDimension) {
		// TODO Auto-generated method stub
		return 0;
	}

	public double getMinValueForDim(int whichDimension) {
		// TODO Auto-generated method stub
		return 0;
	}

	public Vector<Observation> getQueryObservations(
			Vector<Observation> theQueryStates) {
		// TODO Auto-generated method stub
		return null;
	}

	public Vector<Double> queryAgentValues(Vector<Observation> theQueryObs) {
		// TODO Auto-generated method stub
		return null;
	}

	public void setRanges(Vector<Double> mins, Vector<Double> maxs) {
		// TODO Auto-generated method stub
		
	}


}
