import java.util.Vector;

import messaging.agent.AgentMessageParser;
import messaging.agent.AgentMessageType;
import messaging.agent.AgentMessages;
import messaging.agent.AgentValueForObsRequest;
import messaging.agent.AgentValueForObsResponse;
import messaging.environment.EnvMessageType;
import messaging.environment.EnvObsForStateRequest;
import messaging.environment.EnvObsForStateResponse;
import messaging.environment.EnvRangeResponse;
import messaging.environment.EnvironmentMessageParser;
import messaging.environment.EnvironmentMessages;
import functionapproximation.FunctionApproximator;
import functionapproximation.TileCodingFunctionApproximator;
import rlglue.Action;
import rlglue.Agent;
import rlglue.Observation;


public class DynaSarsa implements Agent {


	private int actionCount;
	private double epsilon;
	private double alpha;
	private double defaultWidth;
	private int planningSteps;
		
	private int numTilings;
	FunctionApproximator theFA;



	public DynaSarsa(){
		this(5,8,.05,.1);
	}
	
	public DynaSarsa(int planningSteps, int numTilings, double alpha, double defaultWidth){
		this.epsilon=.05;
		this.alpha=alpha;
		
		this.planningSteps=planningSteps;
		this.numTilings=numTilings;
		this.defaultWidth=defaultWidth;
	}

	double  queryValueFunction(Observation theObservation){
		double bestActionValue=Double.MIN_VALUE;
		
		for(int a=0;a<actionCount;a++){
			double thisActionValue=theFA.query(theObservation,a);		
			if(thisActionValue>=bestActionValue)bestActionValue=thisActionValue;
		}
		return bestActionValue;
	}


	public void agent_end(double reward) {
	    theFA.end(reward);
	}



	public void agent_init(String theTaskSpec) {
//		TaskSpecObject TSO = TaskSpecParser.parse(theTaskSpec);
//		assert(TSO.action_dim==1);
//		assert(TSO.action_types[0]=='i');
//		assert((int) TSO.action_mins[0]==0);
//		
//		actionCount=(int) TSO.action_maxs[0];
//		int obsCount=TSO.obs_dim;

		actionCount=3;
		int obsCount=2;

		TileCodingFunctionApproximator tmpFA=new TileCodingFunctionApproximator(obsCount,actionCount,numTilings,alpha);
		for(int i=0;i<obsCount;i++){
			tmpFA.setWidth(i,defaultWidth);
		}
		theFA=tmpFA;
		theFA.init();
	}

	public String agent_message(String theMessage) {
		AgentMessages theMessageObject=AgentMessageParser.parseMessage(theMessage);
		String theResponseString=null;
		
		System.out.println("DynaSars got it");
		
		if(theMessageObject.getTheMessageType().id()==AgentMessageType.kAgentQueryValuesForObs.id()){
			System.out.println("and treating it like value  request");

			
			AgentValueForObsRequest theCastedRequest=(AgentValueForObsRequest)theMessageObject;

			Vector<Observation> theQueryObservations=theCastedRequest.getTheRequestObservations();
			Vector<Double> theValues = new Vector<Double>();
			
			for(int i=0;i<theQueryObservations.size();i++){
				theValues.add(getMaxValue(theQueryObservations.get(i)));
			}
			
			AgentValueForObsResponse theResponse = new AgentValueForObsResponse(theValues);
			
			theResponseString=theResponse.makeStringResponse();
			
			//System.out.println("DynaSars: Sending response over network: "+theResponseString);
			return theResponseString;
		}
		


	
		

		System.out.println("We need some code written in Env Message for MountainCar!");
		Thread.dumpStack();
		// TODO Auto-generated method stub
		return null;

	}

	public Action agent_start(Observation theObservation) {
		int theAction=chooseEpsilonGreedy(theObservation);
		theFA.start(theObservation, theAction);

		return makeAction(theAction);
	}

	public Action agent_step(double reward, Observation theObservation) {
	int theAction=chooseEpsilonGreedy(theObservation);

	theFA.step(theObservation, reward,theAction);

	 for(int i=0;i<planningSteps;i++)
		theFA.plan();

	return makeAction(theAction);
}


//make this more standard, return an int, forget the value
int chooseEpsilonGreedy(Observation theObservation) {
	int action=0;
	
	if(Math.random()<=epsilon){
		action=(int)(Math.random()*(float)actionCount);
	}else{
		action=chooseGreedy(theObservation);
	}
	return action;
}

double getMaxValue(Observation theObservation){
	int action=0;
	double bestValue=theFA.query(theObservation,action);

	for(int a=1;a<actionCount;a++){
		double thisActionValue=theFA.query(theObservation,a);		

		if(thisActionValue>=bestValue){
				bestValue=thisActionValue;
		}
	}
	return bestValue;
}
	
int chooseGreedy(Observation theObservation){
	int num_ties=1;
	int action=0;
	double bestValue=theFA.query(theObservation,action);

	for(int a=1;a<actionCount;a++){
		double thisActionValue=theFA.query(theObservation,a);		

		if(thisActionValue>=bestValue){
			if(thisActionValue>bestValue){
				action=a;
				bestValue=thisActionValue;
			}else {
				num_ties++;
				//FIXME: Did weird things, should look into this and fix it
				if (0 == (int)(Math.random()*num_ties))
				{
					bestValue = thisActionValue;
					action = a;
				}
			}
		}
	}
	return action;
}

Action makeAction(int baseAction){
	Action actionObject = new Action(1, 0);
	actionObject.intArray[0] = baseAction;
	return actionObject;
}
	

	public void agent_cleanup() {
		// TODO Auto-generated method stub
		
	}
	public void agent_freeze() {
		// TODO Auto-generated method stub
		
	}

}
