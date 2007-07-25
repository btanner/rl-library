package GenericSarsaLambda;
import messaging.agent.AgentMessageParser;
import messaging.agent.AgentMessages;
import rlglue.Action;
import rlglue.Agent;
import rlglue.Observation;
import visualization.QueryableAgent;
import functionapproximation.TileCoder;

public class GenericSarsaLambda implements Agent, QueryableAgent {


	boolean inited=false;
	private int actionCount;
	int MEMORY_SIZE;
	int NUM_TILINGS;
	int MAX_NONZERO_TRACES;
	double defaultDivider;

	int nonzero_traces[];
	int num_nonzero_traces;
	int nonzero_traces_inverse[];
	double minimum_trace;

	// Global RL variables:
	double tempQ[];	// action values

	double observationDividers[];

	double theta[];							// modifyable parameter vector, aka memory, weights
	double e[];								// eligibility traces
	int tempF[];								// sets of features, one set per action



	// Standard RL parameters:
	double epsilon;						// probability of random action
	double alpha;						// step size parameter
	double lambda;						// trace-decay parameters
	double gamma;

	int oldAction;						//action selected on previous time step
	int newAction;						//action selected on current time step

	double getAlpha(){return alpha;}
	double getEpsilon(){return epsilon;}
	int getNumTilings(){return NUM_TILINGS;}
	int getMemorySize(){return MEMORY_SIZE;}
	double getLambda(){return lambda;}


	TileCoder theTileCoder=new TileCoder();
	public void agent_cleanup() {
		// TODO Auto-generated method stub
		inited=false;

	}

	public void agent_end(double r) {
		DecayTraces(gamma*lambda);                               // let traces fall

		for (int a=0; a<actionCount; a++)                        // optionally clear other traces
			if (a != oldAction)
				for (int j=0; j<NUM_TILINGS; j++)
					ClearTrace(tempF[a*NUM_TILINGS+j]);

		for (int j=0; j<NUM_TILINGS; j++) 
			SetTrace(tempF[oldAction*NUM_TILINGS+j],1.0); // replace traces

		double delta = r - tempQ[oldAction];

		double temp = (alpha/NUM_TILINGS)*delta;

		for (int i=0; i<num_nonzero_traces; i++)                 // update theta (learn)
		{ 
			int f = nonzero_traces[i];
			theta[f] += temp * e[f];
		}

	}

	public void agent_freeze() {
		// TODO Auto-generated method stub

	}

	public void agent_init(String taskSpec) {
		//Parse the task spec somehow
		actionCount=3;//sampleAction->getIntValue(0);
		int doubleCount=2;
//		assert(actionCount>0);
		for (int i=0; i<MEMORY_SIZE; i++) {
			theta[i]= 0.0;                     // clear memory at start of each run
			e[i] = 0.0;                            // clear all traces
			nonzero_traces_inverse[i]=0;
		}

		for(int i=0;i<MAX_NONZERO_TRACES;i++)
			nonzero_traces[i]=0;

		observationDividers=new double[doubleCount];
		for(int i=0;i<doubleCount;i++) observationDividers[i]=defaultDivider;

		tempF = new int[actionCount*NUM_TILINGS];
		tempQ = new double[actionCount];
		inited=true;
	}

	public String agent_message(String theMessage) {
		AgentMessages theMessageObject=AgentMessageParser.parseMessage(theMessage);

		if(theMessageObject.canHandleAutomatically())
			return theMessageObject.handleAutomatically(this);

		System.out.println("We need some code written in Agent Message for Generic Sarsa Lambda!");
		Thread.dumpStack();
		return null;
	}




	public Action agent_start(Observation Observations) {
		DecayTraces(0.0);                                            // clear all traces

		// clear all traces
		loadF(tempF,Observations);                         // compute features
		//Q for all actions
		loadQ(tempQ,tempF); 

		if(Math.random() <= epsilon)
			oldAction = (int) (Math.random() * (actionCount));
		else
			oldAction = argmax(tempQ);                                      // pick argmax action


		Action action = new Action(1, 0);
		action.intArray[0] = oldAction;

		return action;
	}

	public Action agent_step(double r, Observation theObservations) {

		DecayTraces(gamma*lambda);                               // let traces fall

		for (int a=0; a<actionCount; a++)                        // optionally clear other traces
			if (a != oldAction)
				for (int j=0; j<NUM_TILINGS; j++)
					ClearTrace(tempF[a*NUM_TILINGS+j]);

		for (int j=0; j<NUM_TILINGS; j++)
			SetTrace(tempF[oldAction*NUM_TILINGS+j],1.0); // replace traces

		double partDelta = r - tempQ[oldAction];

		//Load Q values for all actions in current state
		loadF(tempF,theObservations);                         // compute features
		loadQ(tempQ,tempF);                                                 // compute new state values

		//Choose new action epsilon greedily
		if(Math.random() <= epsilon)
			newAction = (int) (Math.random() * (actionCount));
		else
			newAction = argmax(tempQ);                                      // pick argmax action

		double delta = partDelta + gamma * tempQ[newAction];

		double temp = (alpha/(double)(NUM_TILINGS))*delta;


		//When do these traces get turned on
		for (int i=0; i<num_nonzero_traces; i++)                 // update theta (learn)
		{ 
			int f = nonzero_traces[i];
			theta[f] += temp * e[f];
		}       // update theta (learn)

		//Reload the Q value using the new updated weights.
		loadQ(tempQ,tempF,newAction);

		oldAction = newAction;    

		Action action = new Action(1, 0);
		action.intArray[0] = newAction;

		return action;
	}




	public GenericSarsaLambda(){
		this.lambda=0.0f;
		this.epsilon=0.05f;
		this.alpha=.1f;
		this.MEMORY_SIZE=1<<16;
		this.NUM_TILINGS=8;
		this.defaultDivider=.1f;
		this.MAX_NONZERO_TRACES=100000;
		observationDividers=null;
		this.tempF=null;
		this.tempQ=null;

		sharedConstructorStuff();
	}


	public GenericSarsaLambda(double lambda,double epsilon, double alpha, int memsize, int num_tilings, double defaultDivider){

		this.lambda=lambda;
		this.epsilon=epsilon;
		this.alpha=alpha;
		this.MEMORY_SIZE=memsize;
		this.NUM_TILINGS=num_tilings;
		this.MAX_NONZERO_TRACES=100000;
		this.defaultDivider=defaultDivider;
		this.observationDividers=null;
		this.tempF=null;
		this.tempQ=null;

		sharedConstructorStuff();
	}


	private void sharedConstructorStuff(){
		nonzero_traces=new int[MAX_NONZERO_TRACES];
		num_nonzero_traces=0;
		nonzero_traces_inverse = new int[MEMORY_SIZE];
		minimum_trace = 0.01;
		theta = new double[MEMORY_SIZE];
		e = new double[MEMORY_SIZE];
		gamma=1.0f;

		System.out.print("***********\nnew tile coding agent:");
		System.out.print("lambda: "+lambda);
		System.out.print(" epsilon: "+epsilon);
		System.out.print(" alpha: "+alpha);
		System.out.print(" num_tilings: "+NUM_TILINGS);
		System.out.print(" defaultDivider: "+defaultDivider);
		System.out.println("\n**********");
	}






	// Compute all the action values from current F and theta
	private void loadQ(double Q[], int F[]) 
	{
		for (int a=0; a<actionCount; a++) 
		{
			Q[a] = 0;
			for (int j=0; j<NUM_TILINGS; j++){
				try{
					Q[a] += theta[F[a*NUM_TILINGS+j]];
				}catch(Exception e){
					System.out.println("Index into F is: "+a+"*"+NUM_TILINGS+"+"+j);
					System.out.println("Index into theta is: "+F[a*NUM_TILINGS+j]);
					e.printStackTrace();
					System.exit(1);
				}
			}
		}
	}

	// Compute an action value from current F and theta
	private void loadQ(double Q[], int F[],int a) 
	{
		Q[a] = 0;
		for (int j=0; j<NUM_TILINGS; j++) 
			Q[a] += theta[F[a*NUM_TILINGS+j]];
	}



	private void loadF(int F[],Observation theObservation)
	{
		int doubleCount=theObservation.doubleArray.length;
		int intCount=theObservation.intArray.length;
		double	double_vars[]=new double[doubleCount];
		int		int_vars[] = new int[intCount+1];

		for(int i=0;i<doubleCount;i++){
			double_vars[i] = theObservation.doubleArray[i] / observationDividers[i];
		}

		//int_vars[0] will be the action

		for(int i=0;i<intCount;i++){
			int_vars[i+1] = theObservation.intArray[i];
		}

		for (int a=0; a<actionCount; a++){
			int_vars[0]=a;
			theTileCoder.tiles(F,a*NUM_TILINGS,NUM_TILINGS,MEMORY_SIZE,double_vars,doubleCount,int_vars, intCount+1);
		}
	}

	// Returns index (action) of largest entry in Q array, breaking ties randomly
	private int argmax(double Q[])
	{
		int best_action = 0;
		double best_value = Q[0];
		int num_ties = 1;                    // actually the number of ties plus 1
		double value;

		for (int a=1; a<actionCount; a++) 
		{
			value = Q[a];
			if (value >= best_value) 
				if (value > best_value)
				{
					best_value = value;
					best_action = a;
				}
				else 
				{
					num_ties++;
					if (0 == (int)(Math.random()*num_ties))
					{
						best_value = value;
						best_action = a;
					}
				}
		}
		return best_action;
	}

	private void SetTrace(int f, double new_trace_value)
	// Set the trace for feature f to the given value, which must be positive
	{ 
		if (e[f] >= minimum_trace)
			e[f] = new_trace_value;         // trace already exists
		else 
		{ 
			while (num_nonzero_traces >= MAX_NONZERO_TRACES)
				IncreaseMinTrace(); // ensure room for new trace
			e[f] = new_trace_value;
			nonzero_traces[num_nonzero_traces] = f;
			nonzero_traces_inverse[f] = num_nonzero_traces;
			num_nonzero_traces++;
		}
	}



	private void ClearTrace(int f)       
	// Clear any trace for feature f
	{ 
		if (!(e[f]==0.0)) 
			ClearExistentTrace(f,nonzero_traces_inverse[f]); 
	}




	private void ClearExistentTrace(int f, int loc)
	// Clear the trace for feature f at location loc in the list of nonzero traces
	{ 
		e[f] = 0.0;
		num_nonzero_traces--;
		nonzero_traces[loc] = nonzero_traces[num_nonzero_traces];
		nonzero_traces_inverse[nonzero_traces[loc]] = loc;}



	private void DecayTraces(double decay_rate)
	// Decays all the (nonzero) traces by decay_rate, removing those below minimum_trace
	{ 
		for (int loc=num_nonzero_traces-1; loc>=0; loc--)      // necessary to loop downwards
		{ 
			int f = nonzero_traces[loc];
			e[f] *= decay_rate;
			if (e[f] < minimum_trace) ClearExistentTrace(f,loc);
		}

	}

	private void IncreaseMinTrace()
	// Try to make room for more traces by incrementing minimum_trace by 10%, 
	// culling any traces that fall below the new minimum
	{ 
		minimum_trace += 0.1 * minimum_trace;
		for (int loc=num_nonzero_traces-1; loc>=0; loc--)      // necessary to loop downwards
		{ 
			int f = nonzero_traces[loc];
			if (e[f] < minimum_trace) 
				ClearExistentTrace(f,loc);
		}
	}
	public double getValueForState(Observation theObservation) {
		if(!inited)return 0.0d;
		
		int queryF[] = new int[actionCount*NUM_TILINGS];
		double queryQ[] = new double[actionCount];

		loadF(queryF,theObservation);                         // compute features
		loadQ(queryQ,queryF); 
		int maxIndex=argmax(queryQ);
		return queryQ[maxIndex];	
	}



}
