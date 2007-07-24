package dynaSars;
import messaging.agent.AgentMessageParser;
import messaging.agent.AgentMessages;
import rlglue.Action;
import rlglue.Agent;
import rlglue.Observation;
import visualization.QueryableAgent;
import functionapproximation.FunctionApproximator;
import functionapproximation.TileCodingFunctionApproximator;


public class DynaSarsa implements Agent, QueryableAgent {


	private int actionCount;
	private double epsilon;
	private double alpha;
	private double defaultWidth;
	private int planningSteps;

	private int numTilings;
	FunctionApproximator theFA;



	public DynaSarsa(){
		this(5,16,.05,.05);
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

		if(theMessageObject.canHandleAutomatically())
			return theMessageObject.handleAutomatically(this);

		System.out.println("We need some code written in Agent Message for DynaSars!");
		Thread.dumpStack();
		return null;
	}

	public Action agent_start(Observation theObservation) {
		int theAction=chooseEpsilonGreedy(theObservation);
		theFA.start(theObservation, theAction);

		return makeAction(theAction);
	}

	private int numSteps=0;
	public Action agent_step(double reward, Observation theObservation) {
		numSteps++;
		if(numSteps%25000==0)
			System.out.println("Steps so far: "+numSteps);

		int theAction=chooseEpsilonGreedy(theObservation);

		theFA.step(theObservation, reward,theAction);

		for(int i=0;i<planningSteps;i++)
			theFA.plan();

		return makeAction(theAction);
	}


//	make this more standard, return an int, forget the value
	int chooseEpsilonGreedy(Observation theObservation) {
		int action=0;

		if(Math.random()<=epsilon){
			action=(int)(Math.random()*(float)actionCount);
		}else{
			action=chooseGreedy(theObservation);
		}
		return action;
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


	public double getValueForState(Observation theObservation) {
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

}
