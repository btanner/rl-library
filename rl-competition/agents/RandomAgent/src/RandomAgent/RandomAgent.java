package RandomAgent;

import java.util.Random;

import rlVizLib.utilities.TaskSpecObject;

import rlglue.agent.Agent;
import rlglue.types.Action;
import rlglue.types.Observation;

public class RandomAgent implements Agent {
	private Action action;
	private int numInts =1;
	private int numDoubles =0;
	private Random random = new Random();

        TaskSpecObject TSO=null;
	
        
        public RandomAgent(){
        }

	public void agent_cleanup() {
		// TODO Auto-generated method stub
		
	}

	public void agent_end(double arg0) {
		// TODO Auto-generated method stub
		
	}

	public void agent_freeze() {
		// TODO Auto-generated method stub
		
	}

	public void agent_init(String taskSpec) {
                TSO = new TaskSpecObject(taskSpec);
		// TODO Auto-generated method stub
		action = new Action(TSO.num_discrete_action_dims,TSO.num_continuous_action_dims);	
	}

	public String agent_message(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Action agent_start(Observation o) {
		randomify(action);
		//ask(o,action);
		return action;
	}

	public Action agent_step(double arg0, Observation o) {
		randomify(action);
		return action;
	}

	public double getValueForState(Observation theObservation) {
		// TODO Auto-generated method stub
		return 0;
	}

	private void randomify(Action action){
        for(int i=0;i<TSO.num_discrete_action_dims;i++)
            action.intArray[i]=random.nextInt((int)TSO.action_maxs[i]+1);
        for(int i=0;i<TSO.num_continuous_action_dims;i++)
            action.doubleArray[i]=random.nextDouble()*TSO.action_maxs[i];
	}
	
}
