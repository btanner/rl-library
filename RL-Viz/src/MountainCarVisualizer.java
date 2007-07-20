import java.util.Vector;

import messaging.agent.AgentValueForObsRequest;
import messaging.agent.AgentValueForObsResponse;
import messaging.environment.EnvObsForStateRequest;
import messaging.environment.EnvObsForStateResponse;
import rlglue.Observation;


public class MountainCarVisualizer  implements ValueFunctionDataProvider {

	Vector<Double> mins = new Vector<Double>();
	Vector<Double> maxs = new Vector<Double>();
	
	public void setRanges(Vector<Double> mins,Vector<Double> maxs){
		this.mins=mins;
		this.maxs=maxs;
	}

	public double getMaxValueForDim(int whichDimension) {
		return maxs.get(whichDimension);
	}

	public double getMinValueForDim(int whichDimension) {
		return mins.get(whichDimension);
	}

	public Vector<Observation> getQueryObservations(Vector<Observation> theQueryStates) {
		System.out.println("getQueryObservations  was called in RLVizFrame!");

		EnvObsForStateResponse theObsForStateResponse=EnvObsForStateRequest.Execute(theQueryStates);
		return theObsForStateResponse.getTheObservations();
	}

	public Vector<Double> queryAgentValues(Vector<Observation> theQueryObs) {
		System.out.println("queryAgentValues  was called in RLVizFrame!");
		assert(theQueryObs!=null);
		AgentValueForObsResponse theValueResponse = AgentValueForObsRequest.Execute(theQueryObs);
		assert(theValueResponse.getTheValues()!=null);
		return theValueResponse.getTheValues();
	}

}
