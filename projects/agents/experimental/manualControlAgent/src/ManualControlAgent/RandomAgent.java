/* 
 * Copyright (C) 2007, Brian Tanner
 * 
http://rl-library.googlecode.com/
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
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
	}

	public void agent_end(double arg0) {

	}

	public void agent_freeze() {

	}

	public void agent_init(String taskSpec) {
            TSO = new TaskSpecObject(taskSpec);

            action = new Action(TSO.num_discrete_action_dims,TSO.num_continuous_action_dims);	
	}

	public String agent_message(String arg0) {
            return null;
	}

	public Action agent_start(Observation o) {
            randomify(action);
            return action;
	}

	public Action agent_step(double arg0, Observation o) {
            randomify(action);
            return action;
	}

	private void randomify(Action action){
            for(int i=0;i<TSO.num_discrete_action_dims;i++){
                action.intArray[i]=random.nextInt(((int)TSO.action_maxs[i]+1)-(int)TSO.action_mins[i]) + ((int)TSO.action_mins[i]);
            }
            for(int i=0;i<TSO.num_continuous_action_dims;i++){
                action.doubleArray[i]=random.nextDouble()*(TSO.action_maxs[i] - TSO.action_mins[i]) + TSO.action_mins[i];
            }
       	}
	


}
