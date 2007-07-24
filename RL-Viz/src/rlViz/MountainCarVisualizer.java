package rlViz;

import interfaces.AgentOnValueFunctionDataProvider;
import interfaces.ValueFunctionDataProvider;

import java.util.Vector;

import messages.MCStateRequest;
import messages.MCStateResponse;
import messaging.agent.AgentValueForObsRequest;
import messaging.agent.AgentValueForObsResponse;
import messaging.environment.EnvObsForStateRequest;
import messaging.environment.EnvObsForStateResponse;
import messaging.environment.EnvRangeRequest;
import messaging.environment.EnvRangeResponse;
import rlglue.Observation;
import visualization.AgentOnValueFunctionVizComponent;
import visualization.EnvVisualizer;
import visualization.VizComponent;


public class MountainCarVisualizer  extends EnvVisualizer implements ValueFunctionDataProvider, AgentOnValueFunctionDataProvider {

	Vector<Double> mins = null;
	Vector<Double> maxs = null;

	MCStateResponse theCurrentState=null;


	public MountainCarVisualizer(){
		super();
		VizComponent theValueFunction=new ValueFunctionVizComponent(this);
		VizComponent agentOnVF=new AgentOnValueFunctionVizComponent(this);
		VizComponent carOnMountain=new CarOnMountainVizComponent();
		
		super.addVizComponentAtPositionWithSize(theValueFunction,0,.5,1.0,.5);
		super.addVizComponentAtPositionWithSize(agentOnVF,0,.5,1.0,.5);
		super.addVizComponentAtPositionWithSize(carOnMountain, 0, 0, 1.0, 0.5);
//		RedBoxVizComponent testComponent=new RedBoxVizComponent();
//		super.addVizComponent(testComponent);
		super.startVisualizing();
	

		
	}
	
	
	
	public void updateEnvironmentVariableRanges(){
		//Get the Ranges (internalize this)
		EnvRangeResponse theERResponse=EnvRangeRequest.Execute();
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
		return theObsForStateResponse.getTheObservations();
	}

	public Vector<Double> queryAgentValues(Vector<Observation> theQueryObs) {
		AgentValueForObsResponse theValueResponse = AgentValueForObsRequest.Execute(theQueryObs);
		return theValueResponse.getTheValues();
	}




	public double getCurrentStateInDimension(int whichDimension) {
		if(theCurrentState==null)
			return 0;
		
		
		if(whichDimension==0)
			return theCurrentState.getPosition();
		else
			return theCurrentState.getVelocity();
		
	}


	public void updateAgentState() {
		theCurrentState=MCStateRequest.Execute();
	}


}
