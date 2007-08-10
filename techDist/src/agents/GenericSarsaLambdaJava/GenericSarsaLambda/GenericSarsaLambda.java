package GenericSarsaLambda;
import rlVizLib.functionapproximation.TileCoder;
import rlVizLib.messaging.NotAnRLVizMessageException;
import rlVizLib.messaging.agent.AgentMessageParser;
import rlVizLib.messaging.agent.AgentMessages;
import rlVizLib.utilities.TaskSpecObject;
import rlVizLib.visualization.QueryableAgent;
import rlglue.agent.Agent;
import rlglue.types.Action;
import rlglue.types.Observation;

public class GenericSarsaLambda implements Agent, QueryableAgent {

	/* Generic Sarsa Lambda is a Java agent which will work with both
	 * RL-Glue and Rl-Viz. It must implement Agent to be an RL-Glue Agent
	 * and it must implement QueryableAgent to work with RL-Viz.
	 */

	
	boolean inited=false;
	private int actionCount;
	
	/*trace code variables*/
	int MEMORY_SIZE;
	int NUM_TILINGS;
	int MAX_NONZERO_TRACES;
	double numGrids; // number of tiles for each observation variable
	
	double defaultDivider;

	/*trace code requirements*/
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

	//I want to keep track of the mins and maxs' I've seen to do some smart tile coding. That sounds
	//pretty clever. I think I shall do it.
	double doubleMins[] = null;
	double doubleMaxs[]=null;
	boolean rangeDetermined = false;



	// Standard RL parameters:
	double epsilon;						// probability of random action
	double alpha;						// step size parameter
	double lambda;						// trace-decay parameters
	double gamma;

	int oldAction;						//action selected on previous time step
	int newAction;						//action selected on current time step

	/*Extra Sarsa Lambda Functions, not required by RL-Glue or RL-Viz*/
	double getAlpha(){return alpha;}
	double getEpsilon(){return epsilon;}
	int getNumTilings(){return NUM_TILINGS;}
	int getMemorySize(){return MEMORY_SIZE;}
	double getLambda(){return lambda;}
	
	
	TileCoder theTileCoder=new TileCoder();

	

	public GenericSarsaLambda(){
		/*set up the default values for all the internal variables*/
		this.lambda=0.5f;
		this.epsilon=0.05f;
		this.alpha=.25f;
		this.MEMORY_SIZE=1<<16;
		this.NUM_TILINGS=16;
		this.defaultDivider=.05f;
		this.MAX_NONZERO_TRACES=100000;
		observationDividers=null;
		this.tempF=null;
		this.tempQ=null;
		/*Set up the variables which are consistant in all the constructors*/
		sharedConstructorStuff();
	}


	public GenericSarsaLambda(double lambda,double epsilon, double alpha, int memsize, int num_tilings, double defaultDivider){
		/*Set up the values with the values given*/
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
		/* Set up the variables which are consistant in all the constructors*/
		sharedConstructorStuff();
	}
	

	private void sharedConstructorStuff(){
		/* sets up things that are the same for all the constructors. 
		 * not changeable by the user at run time. Prints out info
		 */
		nonzero_traces=new int[MAX_NONZERO_TRACES];
		num_nonzero_traces=0;
		nonzero_traces_inverse = new int[MEMORY_SIZE];
		minimum_trace = 0.01;
		theta = new double[MEMORY_SIZE];
		e = new double[MEMORY_SIZE];
		gamma=1.0f;
		rangeDetermined = false;
		numGrids = 8.0f;

		System.out.print("***********\nnew tile coding agent:");
		System.out.print("lambda: "+lambda);
		System.out.print(" epsilon: "+epsilon);
		System.out.print(" alpha: "+alpha);
		System.out.print(" num_tilings: "+NUM_TILINGS);
		System.out.print(" defaultDivider: "+defaultDivider);
		System.out.println("\n**********");
	}


	
	
	/********* RL GLUE REQUIRED FUNCTIONS START HERE *******/
	/* if you are writing your own agent you need to at least instantiate and follow 
	 * all method protocols for all the RL-Glue Functions*/
	
	
	public void agent_init(String taskSpec) {
		
		System.out.println("Sarsa: Agent Init");
		System.out.println("The task Spec is: "+taskSpec);
		//parse the task spec object
		TaskSpecObject theTaskObject = new TaskSpecObject(taskSpec);

		//In this case we've assumed one action which can take on
		//the integer values 0 to action_max. So the action count is
		// 1 + the action max. If the action values are -3 to -1 
		//there will be a problem in the current implementation
		actionCount=1+(int)theTaskObject.action_maxs[0];
		
		System.out.println("The number of actions is: "+actionCount);
		assert(actionCount>0);

		//if the min is less than 0 there is a problem
		assert(theTaskObject.action_mins[0]==0);
		
		System.out.println("Num discrete action dims is: "+theTaskObject.num_discrete_action_dims);
		//here we're asserting there IS only one discrete action variable. 
		assert(theTaskObject.num_discrete_action_dims==1); //check the number of discrete actions is only 1
		assert(theTaskObject.num_continuous_action_dims==0); //check that there is no continuous actions
		assert(theTaskObject.action_types[0]=='i'); // check that the action type has b een labelled properly..
		assert(theTaskObject.action_maxs[0]>=0); //Check that the max is greater or equal to 0. If it is equal to 0 there is only one action always.... learning is going to be super easy

		int doubleCount=theTaskObject.num_continuous_obs_dims; //get the number of continuous Observation variables
	
		/*Initialize the traces being used for the value function*/
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
		this.doubleMins = new double[theTaskObject.num_continuous_obs_dims];
		this.doubleMaxs = new double[theTaskObject.num_continuous_obs_dims];
			
		inited=true;
	}

	
	public Action agent_start(Observation Observations) {
		/*Based on the initial observation, choose an action to take as your first step :)*/
		DecayTraces(0.0);  // clear all traces
		
		//We are tracking the mins and maxs we've seen. These functions will adjust what our 
		//recorded values are given the new observation
		adjustRecordedRange(Observations);

		// clear all traces
		loadF(tempF,Observations); // compute features
		//Q for all actions
		loadQ(tempQ,tempF); 

		/*Recall oldAction and newAction are being stored as one int because we have decided the 
		 * GenericSarsaLambda will only handle action spaces which consist of one integer. They then must be stuffed into 
		 * An Action Object to be passed out of agent_start
		 */
		if(Math.random() <= epsilon) 
			/*take a random action*/
			oldAction = (int) (Math.random() * (actionCount));
		else
			/*take the greedy action*/
			oldAction = argmax(tempQ);                                      // pick argmax action


		Action action = new Action(1, 0);/* The Action constructor takes two arguements: 1) the size of the int array 2) the size of the double array*/
		action.intArray[0] = oldAction; /*Set the action value*/

		return action;
	}


	public Action agent_step(double r, Observation theObservations) {	
		/*The agent needs to update it's learning function in here and decide on what action to take. */
		
		//We are tracking the mins and maxs we've seen. These functions will adjust what our 
		//recorded values are given the new observation
		adjustRecordedRange(theObservations);
		
		DecayTraces(gamma*lambda);  // let traces fall

		for (int a=0; a<actionCount; a++)  // optionally clear other traces
			if (a != oldAction)
				for (int j=0; j<NUM_TILINGS; j++)
					ClearTrace(tempF[a*NUM_TILINGS+j]);

		for (int j=0; j<NUM_TILINGS; j++)
			SetTrace(tempF[oldAction*NUM_TILINGS+j],1.0); // replace traces

		double partDelta = r - tempQ[oldAction];

		//Load Q values for all actions in current state
		loadF(tempF,theObservations);                         // compute features
		loadQ(tempQ,tempF);                                                 // compute new state values

		/*Recall oldAction and newAction are being stored as one int because we have decided the 
		 * GenericSarsaLambda will only handle action spaces which consist of one integer. They then must be stuffed into 
		 * An Action Object to be passed out of agent_start
		 */
		
		
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
		action.intArray[0] = newAction; //stuffing the new Action value into an Action Object 

		return action;
	}

	public void agent_end(double r) {
		/* Complete the final update to the value function after the agent enters
		 * a terminal state
		 */
		DecayTraces(gamma*lambda); // let traces fall

		for (int a=0; a<actionCount; a++) // optionally clear other traces
			if (a != oldAction)
				for (int j=0; j<NUM_TILINGS; j++)
					ClearTrace(tempF[a*NUM_TILINGS+j]);

		for (int j=0; j<NUM_TILINGS; j++) 
			SetTrace(tempF[oldAction*NUM_TILINGS+j],1.0); // replace traces

		double delta = r - tempQ[oldAction];

		double temp = (alpha/NUM_TILINGS)*delta;

		for (int i=0; i<num_nonzero_traces; i++)  // update theta (learn)
		{ 
			int f = nonzero_traces[i];
			theta[f] += temp * e[f];
		}

	}

	public void agent_cleanup() {
		/* By setting inited to false, we specify that the agent
		 * will need to be reinitialized before use
		 */
		inited=false;

	}
	
	public void agent_freeze() {
		// This agent does not bother to fully implement agent_freeze
		System.out.println("You've called agent_freeze in Generic Sarsa Lambda, however we have not implemented this function fully");
	}


	public String agent_message(String theMessage) {
		/*As per the RL-Glue specs, you can implement anything you want in agent_message. If your agent
		 * needs to be able to change it's lambda value halfway through the experiment, you could write code here so that 
		 * when agent_message is called with a Message like Lambda:0.2 it changes Lambda to 0.2. HOWEVER For RL-Viz, 
		 * a certain number of messages must be handled. The messages handled below are done specifically to use
		 * RL-Viz
		 */
		AgentMessages theMessageObject;
		try {
			/*tries to parse the message to see if it is a RL-VIZ message. If you are handling your own set of messages
			 * you could put your own message handling in the catch block here. 
			 */
			theMessageObject = AgentMessageParser.parseMessage(theMessage);
		} catch (NotAnRLVizMessageException e) {
			System.err.println("Someone sent GenericSarsaLambda a message that wasn't RL-Viz compatible");
			return "I only respond to RL-Viz messages!";
		}

		if(theMessageObject.canHandleAutomatically(null)){
			/*Some messages have a "handle automatically" method that works. Each environment will have
			 * sets of messages it implements. If they implement them with handle automatically you will need to do no other work
			 */
			return theMessageObject.handleAutomatically(this);
		}
		
		/*If the message cannot be handled automatically but WAS RL-Viz Compatible, you should 
		 * write code here to handle those messages. The Environment Writer should let you know if there
		 * are messages you're going to have to worry about... 
		 */
		System.out.println("We need some code written in Agent Message for Generic Sarsa Lambda!");
		Thread.dumpStack();
		/*this is the best return value I've ever seen... ;)*/
		return "BLAH!";
	}

	/********* RL GLUE REQUIRED FUNCTIONS END HERE *******/
	
	/********** RL VIZ REQUIRED  FUNCTIONS START HERE*******/
	public double getValueForState(Observation theObservation) {
		/*this must be implemented to use RL-Viz. This allows the 
		 * visualizer to get values for states. This is useful when trying
		 * to visualize the value function for example
		 */
		//retrieve the current value for the state/observation provided. 
		if(!inited)return 0.0d;
		
		int queryF[] = new int[actionCount*NUM_TILINGS];
		double queryQ[] = new double[actionCount];

		loadF(queryF,theObservation);                         // compute features
		loadQ(queryQ,queryF); 
		int maxIndex=argmax(queryQ);
		return queryQ[maxIndex];	
	}
	/********** RL VIZ REQUIRED  FUNCTIONS END HERE*******/

	/********* Generic Sarsa Lambda Specific Functions Follow *********/

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

	// Compute the action value from current F and theta
	private void loadQ(double Q[], int F[],int a) 
	{
		Q[a] = 0;
		for (int j=0; j<NUM_TILINGS; j++) 
			Q[a] += theta[F[a*NUM_TILINGS+j]];
	}



	private void loadF(int F[],Observation theObservation)
	{
		//Compute the current F values given the observation
		int doubleCount=theObservation.doubleArray.length; // number of continuous observation values
		int intCount=theObservation.intArray.length; // number of discrete observation values
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

	// Returns index (which corresponds to the appropriate action value) of largest entry in Q array, breaking ties randomly
	private int argmax(double Q[])
	{
		int best_action = 0;
		double best_value = Q[0];
		int num_ties = 1;                    // actually the number of ties plus 1
		double value;

		//for all possible actions we flip through the Q values to find the max
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
					//we Break ties RANDOMLY
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
	// Manually Set the trace for feature f to the given value, which must be positive
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
	
	private void adjustRecordedRange(Observation Observations){
		if(!this.rangeDetermined){
			//if we haven't seen any observations yet, we initialize our min and max arrays to hold the 
			//current values of the observation variables and then break
			this.rangeDetermined = true;
			for(int i=0; i< Observations.doubleArray.length; i++){
				this.doubleMins[i] = Observations.doubleArray[i];
				this.doubleMaxs[i] = Observations.doubleArray[i];
			}
			return;
		}
		
		//updating our double Max and Mins
		for(int i =0; i< Observations.doubleArray.length; i++){
			if(Observations.doubleArray[i] < this.doubleMins[i])
				this.doubleMins[i] = Observations.doubleArray[i];
			if(Observations.doubleArray[i] > this.doubleMaxs[i])
				this.doubleMaxs[i] = Observations.doubleArray[i];
		}
		
		for(int i=0; i< this.observationDividers.length; i++)
			this.observationDividers[i] = (this.doubleMaxs[i] - this.doubleMins[i])/numGrids;
		
	}


}
