/*
Copyright 2008 Brian Tanner
http://rl-library.googlecode.com/
brian@tannerpages.com
http://brian.tannerpages.com

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

import java.util.Random;

import rlVizLib.general.ParameterHolder;
import rlVizLib.general.hasVersionDetails;
import rlVizLib.utilities.TaskSpecObject;

import org.rlcommunity.rlglue.codec.AgentInterface;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.util.AgentLoader;

/**
 * Simple random agent that can do multidimensional continuous or discrete
 * actions.
 * @author btanner
 */
public class sarsaAgent implements AgentInterface {

    private Action action;
    private int numInts = 1;
    private int numDoubles = 0;
    private Random random = new Random();
    TaskSpecObject TSO = null;

   public sarsaAgent() {
        this(getDefaultParameters());
    }

    public sarsaAgent(ParameterHolder p) {
        super();
    }


    /**
		This agent doesn't really have any parameters
		*/
	public static ParameterHolder getDefaultParameters() {
        ParameterHolder p = new ParameterHolder();
        rlVizLib.utilities.UtilityShop.setVersionDetails(p, new DetailsProvider());
        return p;
    }
    
    public void agent_init(String taskSpec) {
        TSO = new TaskSpecObject(taskSpec);
        action = new Action(TSO.num_discrete_action_dims, TSO.num_continuous_action_dims);
    }

    public Action agent_start(Observation o) {
        setRandomActions(action);
        return action;
    }

    public Action agent_step(double arg0, Observation o) {
        setRandomActions(action);
        return action;
    }

    public void agent_end(double reward) {
    }

    private void setRandomActions(Action action) {
        for (int i = 0; i < TSO.num_discrete_action_dims; i++) {
            action.intArray[i] = random.nextInt(((int) TSO.action_maxs[i] + 1) - (int) TSO.action_mins[i]) + ((int) TSO.action_mins[i]);
        }
        for (int i = 0; i < TSO.num_continuous_action_dims; i++) {
            action.doubleArray[i] = random.nextDouble() * (TSO.action_maxs[i] - TSO.action_mins[i]) + TSO.action_mins[i];
        }
    }

    public void agent_cleanup() {
    }

    public void agent_freeze() {
    }

    public String agent_message(String theMessage) {
        return null;
    }

    /**
     * This is a trick we can use to make the agent easily loadable.
     * @param args
     */
    public static void main(String[] args){
     	AgentLoader theLoader=new AgentLoader(new sarsaAgent());
        theLoader.run();
	}

}

/**
 * This is a little helper class that fills in the details about this environment
 * for the fancy print outs in the visualizer application.
 * @author btanner
 */
class DetailsProvider implements hasVersionDetails {

    public String getName() {
        return "Random Agent 1.0";
    }

    public String getShortName() {
        return "Random Agent";
    }

    public String getAuthors() {
        return "Leah Hackman, Matt Radkie, Brian Tanner";
    }

    public String getInfoUrl() {
        return "http://code.google.com/p/rl-library/wiki/RandomAgent";
    }

    public String getDescription() {
        return "RL-Library Java Version of the random agent.  Can handle multi dimensional continuous and discrete actions.";
    }
}

