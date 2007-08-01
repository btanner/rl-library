package visualizers.mountainCar;


import java.util.Vector;

import messages.MCStateRequest;
import messages.MCStateResponse;
import rlVizLib.interfaces.AgentOnValueFunctionDataProvider;
import rlVizLib.interfaces.ValueFunctionDataProvider;
import rlVizLib.messaging.agent.AgentValueForObsRequest;
import rlVizLib.messaging.agent.AgentValueForObsResponse;
import rlVizLib.messaging.environment.EnvObsForStateRequest;
import rlVizLib.messaging.environment.EnvObsForStateResponse;
import rlVizLib.messaging.environment.EnvRangeRequest;
import rlVizLib.messaging.environment.EnvRangeResponse;
import rlVizLib.visualization.AgentOnValueFunctionVizComponent;
import rlVizLib.visualization.EnvVisualizer;
import rlVizLib.visualization.VizComponent;
import rlglue.types.Observation;
import vizComponents.ValueFunctionVizComponent;


public class MountainCarVisualizer  extends EnvVisualizer implements ValueFunctionDataProvider, AgentOnValueFunctionDataProvider {

	Vector<Double> mins = null;
	Vector<Double> maxs = null;

	int currentValueFunctionResolution=5;
	
	MCStateResponse theCurrentState=null;


	public MountainCarVisualizer(){
		super();
//		VizComponent theValueFunction=new ValueFunctionVizComponent(this);
//		VizComponent agentOnVF=new AgentOnValueFunctionVizComponent(this);
		VizComponent carOnMountain=new CarOnMountainVizComponent(this);
		
//		super.addVizComponentAtPositionWithSize(theValueFunction,0,.5,1.0,.5);
//		super.addVizComponentAtPositionWithSize(agentOnVF,0,.5,1.0,.5);
		super.addVizComponentAtPositionWithSize(carOnMountain, 0, 0, 1.0, 1.0);
}
	
	public void setValueFunctionResolution(int theValue) {
		currentValueFunctionResolution=theValue;
	}

	
	
	public void updateEnvironmentVariableRanges(){
		//Get the Ranges (internalize this)
		EnvRangeResponse theERResponse=EnvRangeRequest.Execute();
		
		if(theERResponse==null){
			System.err.println("Asked an Environment for Variable Ranges and didn't get back a parseable message.");
			Thread.dumpStack();
			System.exit(1);
		}

		mins = theERResponse.getMins();
		maxs = theERResponse.getMaxs();
	}
	
	public double getMaxValueForDim(int whichDimension) {
		if(maxs==null)updateEnvironmentVariableRanges();
		return maxs.get(whichDimension);
	}

	public double getMinValueForDim(int whichDimension) {
		if(mins==null)updateEnvironmentVariableRanges();
		return mins.get(whichDimension);
	}

	public Vector<Observation> getQueryObservations(Vector<Observation> theQueryStates) {
		EnvObsForStateResponse theObsForStateResponse=EnvObsForStateRequest.Execute(theQueryStates);
		
		if(theObsForStateResponse==null){
			System.err.println("Asked an Environment for Query Observations and didn't get back a parseable message.");
			Thread.dumpStack();
			System.exit(1);
		}
		return theObsForStateResponse.getTheObservations();
	}

	public Vector<Double> queryAgentValues(Vector<Observation> theQueryObs) {
		AgentValueForObsResponse theValueResponse = AgentValueForObsRequest.Execute(theQueryObs);
		
		if(theValueResponse==null){
			System.err.println("Asked an Agent for Values and didn't get back a parseable message.");
			Thread.dumpStack();
			System.exit(1);
		}

		return theValueResponse.getTheValues();
	}




	public double getCurrentStateInDimension(int whichDimension) {
		/*
		 * This is only allowed access to the state Variables which are defined
		 * in the Task Spec as being State Variables. The implicitly defined values,
		 * like the height, should not be accessed through here
		 */
		if(theCurrentState==null)
			return 0;
		
		
		if(whichDimension==0)
			return theCurrentState.getPosition();
		else
			return theCurrentState.getVelocity();
		
	}
	
	public double getHeight(){
		return theCurrentState.getHeight();		
	}
	public double getDeltaHeight(){
		return theCurrentState.getDeltaheight();
	}


	public void updateAgentState() {
		theCurrentState=MCStateRequest.Execute();
	}

	public double getValueFunctionResolution() {
		return (double)currentValueFunctionResolution;
	}


}
