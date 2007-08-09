package Helicopter;

import rlVizLib.Environments.EnvironmentBase;
import rlVizLib.general.ParameterHolder;
import rlVizLib.general.RLVizVersion;
import rlVizLib.messaging.environment.EnvironmentMessageParser;
import rlVizLib.messaging.environment.EnvironmentMessages;
import rlVizLib.messaging.interfaces.RLVizEnvInterface;
import rlglue.types.Action;
import rlglue.types.Observation;
import rlglue.types.Random_seed_key;
import rlglue.types.Reward_observation;
import rlglue.types.State_key;


public class Helicopter extends EnvironmentBase implements RLVizEnvInterface {

	Observation o;
	HelicopterState heli=new HelicopterState();
	Reward_observation ro;
	Random_seed_key random_seed;

	// upper bounds on values state variables can take on (required by rl_glue to be put into a string at environment initialization)
	 double MAX_VEL = 100.0; // m/s
	 double MAX_POS = 100.0;
	 double MAX_RATE = 50.0;
	 double MAX_QUAT = 1.0;
	 double MAX_ACTION = 1.0;

	
	 
		//This method creates the object that can be used to easily set different problem parameters
		public static ParameterHolder getDefaultParameters(){
			ParameterHolder p = new ParameterHolder();
			p.addBooleanParam("Test", true);
			return p;
		}

		public Helicopter(){
			
		}
		
public Helicopter(ParameterHolder p){
	
}
	@Override
	protected Observation makeObservation() {
		return heli.makeObservation();
	}

	public RLVizVersion getTheVersionISupport() {
		return new RLVizVersion(1,0);
	}

	public void env_cleanup() {
		// TODO Auto-generated method stub
		
	}

	public Random_seed_key env_get_random_seed() {
		// TODO Auto-generated method stub
		return null;
	}

	public State_key env_get_state() {
		// TODO Auto-generated method stub
		return null;
	}

	public String env_init() {
		/*initializing the map struct and an observation object*/
		String Task_spec="";

		/*set random seed*/
//		random_seed.numInts = 1;
//		random_seed.numDoubles = 0;
//		random_seed.intArray = (int*)malloc(sizeof(int)*random_seed.numInts);
//		random_seed.intArray[0] = 0;
		// set the random seed
//		srand(random_seed.intArray[0]);

		/* Create and Return task specification */
		Task_spec = String.format("2.0:e:13_[f,f,f,f,f,f,f,f,f,f,f,f,f]_[-%f,%f]_[-%f,%f]_[-%f,%f]_[-%f,%f]_[-%f,%f]_[-%f,%f]_[-%f,%f]_[-%f,%f]_[-%f,%f]_[-%f,%f]_[-%f,%f]_[-%f,%f]_[-%f,%f]:4_[f,f,f,f]_[-%f,%f]_[-%f,%f]_[-%f,%f]_[-%f,%f]",
				MAX_VEL,MAX_VEL,MAX_VEL,MAX_VEL,MAX_VEL,MAX_VEL, MAX_POS, MAX_POS, MAX_POS, MAX_POS, MAX_POS, MAX_POS, MAX_RATE, MAX_RATE, MAX_RATE, MAX_RATE, MAX_RATE, MAX_RATE,
				MAX_QUAT, MAX_QUAT, MAX_QUAT, MAX_QUAT, MAX_QUAT, MAX_QUAT, MAX_QUAT, MAX_QUAT, MAX_ACTION, MAX_ACTION, MAX_ACTION, MAX_ACTION, MAX_ACTION, MAX_ACTION, MAX_ACTION, MAX_ACTION);

		return Task_spec;
	}

	public String env_message(String theMessage) {
		EnvironmentMessages theMessageObject;
		try {
			theMessageObject = EnvironmentMessageParser.parseMessage(theMessage);
		} catch (Exception e) {
			System.err.println("Someone sent Helicopter a message that wasn't RL-Viz compatible");
			return "I only respond to RL-Viz messages!";
		}



		if(theMessageObject.canHandleAutomatically(this)){
			return theMessageObject.handleAutomatically(this);
		}

		System.out.println("We need some code written in Env Message for  Helicopter!");
		Thread.dumpStack();

		return null;
	}

	public void env_set_random_seed(Random_seed_key key) {
		// TODO Auto-generated method stub
		
	}

	public void env_set_state(State_key key) {
		// TODO Auto-generated method stub
		
	}

	public Observation env_start() {
		// start at origin, zero velocity, zero angular rate, perfectly level and facing north
		heli.env_terminal = false;
		heli.reset();
		heli.state[HelicopterState.qw_idx] = 1.0;	
		heli.num_sim_steps = 0;

		return makeObservation();
	}
	
	// goal state is all zeros, quadratically penalize for deviation:
	double getReward()
	{
		double reward = 0;
		for (int i=0; i < 12; ++i)
			reward -= heli.state[i]*heli.state[i];
		
		return reward;

	}


	public Reward_observation env_step(Action action) {
		heli.stateUpdate(action);
		heli.num_sim_steps++;	
		heli.env_terminal = heli.env_terminal || (heli.num_sim_steps == heli.NUM_SIM_STEPS_PER_EPISODE);

		int isTerminal=0;
		if(heli.env_terminal)
			isTerminal=1;

		
		Reward_observation ro=new Reward_observation(getReward(),makeObservation(), isTerminal);
		return ro;
	}

	
	
	
}




