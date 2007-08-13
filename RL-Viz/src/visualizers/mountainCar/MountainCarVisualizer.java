package visualizers.mountainCar;


import java.util.Vector;

import messages.MCHeightRequest;
import messages.MCHeightResponse;
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
import rlVizLib.visualization.AbstractVisualizer;
import rlVizLib.visualization.VizComponent;
import rlglue.types.Observation;


public class MountainCarVisualizer  extends AbstractVisualizer implements ValueFunctionDataProvider, AgentOnValueFunctionDataProvider {

	Vector<Double> mins = null;
	Vector<Double> maxs = null;

	int currentValueFunctionResolution=5;
	
	MCStateResponse theCurrentState=null;
	
	Vector<Double> theQueryPositions=null;
	Vector<Double> theHeights=null;
	double minHeight= Double.MIN_VALUE;
	double maxHeight = Double.MAX_VALUE;


	public MountainCarVisualizer(){
		super();
//		VizComponent theValueFunction=new ValueFunctionVizComponent(this);
//		VizComponent agentOnVF=new AgentOnValueFunctionVizComponent(this);
		VizComponent mountain=new MountainVizComponent(this);
		VizComponent carOnMountain=new CarOnMountainVizComponent(this);
		VizComponent scoreComponent=new MountainCarScoreComponent(this);
		
//		super.addVizComponentAtPositionWithSize(theValueFunction,0,.5,1.0,.5);
//		super.addVizComponentAtPositionWithSize(agentOnVF,0,.5,1.0,.5);
		super.addVizComponentAtPositionWithSize(mountain, 0, 0, 1.0, 1.0);
		super.addVizComponentAtPositionWithSize(carOnMountain, 0, 0, 1.0, 1.0);
		super.addVizComponentAtPositionWithSize(scoreComponent, 0, 0, 1.0, 1.0);
		
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
	
	public double getMaxHeight(){
		if(theQueryPositions==null){
			initializeHeights();
		}
		return minHeight;
	}
	
	public double getMinHeight(){
		if(theQueryPositions==null){
			initializeHeights();
		}
		return maxHeight;
	}
	
	public Vector<Double> getSampleHeights(){
		if(theHeights == null)
			initializeHeights();
		return theHeights;
		
	}
	
	public Vector<Double> getSamplePositions(){
		if(theQueryPositions==null)
			initializeHeights();
		return theQueryPositions;
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

	public Vector<Double> getHeightsForPositions(Vector<Double> theQueryPositions) {
		MCHeightResponse heightResponse=MCHeightRequest.Execute(theQueryPositions);
		return heightResponse.getHeights();
	}
	
	
	

	public int getEpisodeNumber() {
		return theCurrentState.getEpisodeNumber();
	}

	public int getTimeStep() {
		return theCurrentState.getCurrentStep();
	}

	public int getTotalTimeSteps() {
		return theCurrentState.getTotalSteps();
	}
	
	public void initializeHeights(){
		//Because we can change the shape of the curve we have no guarantees what 
		// the max and min heights of the mountains may turn out to be...
		// this takes a quick sample based approach to find out what is a good approximation
		//for the min and the max.
		double minPosition=getMinValueForDim(0);
		double maxPosition=getMaxValueForDim(0);

		int pointsToDraw=500;
		double theRangeSize=maxPosition-minPosition;
		double pointIncrement=theRangeSize/(double)pointsToDraw;

		theQueryPositions=new Vector<Double>();
		for(double i=minPosition;i<maxPosition;i+=pointIncrement){
			theQueryPositions.add(i);
		}

		theHeights=this.getHeightsForPositions(theQueryPositions);
		
		maxHeight=Double.MIN_VALUE;
		minHeight=Double.MAX_VALUE;
		for (Double thisHeight : theHeights) {
			if(thisHeight>maxHeight)maxHeight=thisHeight;
			if(thisHeight<minHeight)minHeight=thisHeight;
		}
		
	}


}
