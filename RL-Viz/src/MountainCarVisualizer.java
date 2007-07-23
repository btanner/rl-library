import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
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


public class MountainCarVisualizer  extends EnvVisualizer implements ValueFunctionDataProvider, AgentOnValueFunctionDataProvider {

	Vector<Double> mins = new Vector<Double>();
	Vector<Double> maxs = new Vector<Double>();

MCStateResponse theCurrentState=null;

	ValueFunctionVizComponent theValueFunction=null;

	public MountainCarVisualizer(){
		super();
		theValueFunction=new ValueFunctionVizComponent(new Point2D.Double(1.0,1.0),this);
		//Get the Ranges (internalize this)
		EnvRangeResponse theERResponse=EnvRangeRequest.Execute();
		mins = theERResponse.getMins();
		maxs = theERResponse.getMaxs();
		
		VizComponent theRedBox=new RedBoxVizComponent();
		AgentOnValueFunctionVizComponent agentOnVF=new AgentOnValueFunctionVizComponent(this);
		super.addVizComponent(theValueFunction);
//		super.addVizComponent(theRedBox);
		super.addVizComponent(agentOnVF);
		super.startVisualizing();
	

		
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




	public Point2D getWindowLocationForQueryPoint(Point2D stateValues) {
		return theValueFunction.getWindowLocationForQueryPoint(stateValues);
	}




	public void updateAgentState() {
		theCurrentState=MCStateRequest.Execute();
	}


}
