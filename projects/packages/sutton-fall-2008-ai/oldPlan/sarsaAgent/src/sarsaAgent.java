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

import java.util.Vector;
import rlVizLib.general.ParameterHolder;
import rlVizLib.general.hasVersionDetails;
import rlVizLib.utilities.TaskSpecObject;

import org.rlcommunity.rlglue.codec.AgentInterface;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.util.AgentLoader;
import rlVizLib.messaging.NotAnRLVizMessageException;
import rlVizLib.messaging.agent.AgentMessageParser;
import rlVizLib.messaging.agent.AgentMessages;
import rlVizLib.visualization.QueryableAgent;

/**
 * Simple sarsa agent that can handle discrete random agent that can do multidimensional continuous or discrete
 * actions.
 * @author btanner
 */
public class sarsaAgent implements AgentInterface, QueryableAgent {

    private Random randomGenerator = new Random();
    private int numActions = 0;
    private int numStates = 0;
    private double[][] QTable = null;
    private double learningRate = .1;
    private double exploreRate = .05;
    private int lastState = 0;
    private int lastAction = 0;

    public sarsaAgent() {
        this(getDefaultParameters());
    }

    public sarsaAgent(ParameterHolder p) {
        super();
        this.learningRate = p.getDoubleParam("sarsaAgent-learningRate");
        this.exploreRate = p.getDoubleParam("sarsaAgent-exploreRate");
    }

    public void agent_init(String taskSpec) {
        TaskSpecObject TSO = new TaskSpecObject(taskSpec);
        numActions = (int) (TSO.action_maxs[0]) + 1;
        numStates = (int) (TSO.obs_maxs[0]) + 1;

        System.out.println("Agent thinks there are: " + numActions + " actions and: " + numStates + " states");
        QTable = new double[numStates][numActions];
    }

    public Action agent_start(Observation o) {
        int whichActionToReturn = 0;

        int theState = o.intArray[0];
        if (randomGenerator.nextFloat() <= exploreRate) {
            whichActionToReturn = randomGenerator.nextInt(numActions);
        } else {
            whichActionToReturn = getArgMax(QTable[theState]);
        }

        lastAction = whichActionToReturn;
        lastState = theState;

        Action returnActionObject = new Action(1, 0, 0);
        returnActionObject.intArray[0] = whichActionToReturn;
        return returnActionObject;
    }

    public Action agent_step(double reward, Observation o) {
        int whichActionToReturn = 0;

        int theState = o.intArray[0];

        double lastValue = QTable[lastState][lastAction];
        double thisValue = reward + getMaxValue(theState);

        QTable[lastState][lastAction] += learningRate * (thisValue - lastValue);

        if (randomGenerator.nextFloat() <= exploreRate) {
            whichActionToReturn = randomGenerator.nextInt(numActions);
        } else {
            whichActionToReturn = getArgMax(QTable[theState]);
        }

        lastState = theState;
        lastAction = whichActionToReturn;

        Action returnActionObject = new Action(1, 0, 0);
        returnActionObject.intArray[0] = whichActionToReturn;
        return returnActionObject;
    }

    public void agent_end(double reward) {
        double lastValue = QTable[lastState][lastAction];
        double thisValue = reward;

        QTable[lastState][lastAction] += learningRate * (thisValue - lastValue);
    }
    
    public void agent_cleanup() {
        QTable = null;
    }


    /**
     * Get the value for this state-action pair
     * @param State
     * @param action
     * @return
     */
    private double getValue(int State, int action) {
        return QTable[State][action];
    }

    /**
     * Get the highest value for this tate
     * @param State
     * @return
     */
    private double getMaxValue(int State) {
        int maxAction = getArgMax(QTable[State]);
        return QTable[State][maxAction];
    }

    /**
     * Return the best action for the values of this state
     * @param values
     * @return
     */
    private int getArgMax(double[] values) {
        Vector<Integer> ties = new Vector<Integer>();
        double theMax = values[0];
        ties.add(0);

        for (int i = 1; i < values.length; i++) {
            if (values[i] == theMax) {
                ties.add(i);
            }
            if (values[i] > theMax) {
                theMax = values[i];
                ties.clear();
                ties.add(i);
            }
        }
        //Break ties randomly
        int tieBreak = randomGenerator.nextInt(ties.size());
        return ties.get(tieBreak);
    }


    /**
     * Get the epsilon-greedy value of a state 
     * @param values
     * @return
     */
    private double getPolicyValue(double[] values) {
        double policyValue = 0.0d;
        double allActionsValue = 0.0d;
        int maxAction = getArgMax(values);
        policyValue = (1.0d - exploreRate) *values[maxAction];

        for (int i = 0; i < numActions; i++) {
            allActionsValue += values[maxAction];
        }
        return policyValue + (allActionsValue / (exploreRate * (double) numActions));
    }

    
    
    /******************************************/
    /******************************************/
    /******************************************/
    /******************************************/
    /* Down below is nothing very interesting */
    /******************************************/
    /******************************************/
    /******************************************/
    
    
    public String agent_message(String theMessage) {
        /**  A little RL-Viz Magic **/
        AgentMessages theMessageObject;
        try {
            theMessageObject = AgentMessageParser.parseMessage(theMessage);
        } catch (NotAnRLVizMessageException e) {
            System.err.println("Someone sent " + getClass() + " a message that wasn't RL-Viz compatible");
            return "I only respond to RL-Viz messages!";
        }

        if (theMessageObject.canHandleAutomatically(this)) {
            return theMessageObject.handleAutomatically(this);
        }
        System.out.println(getClass() + " :: Unhandled Message :: " + theMessageObject);
        return null;
    }

    public static ParameterHolder getDefaultParameters() {
        ParameterHolder p = new ParameterHolder();
        p.addDoubleParam("sarsaAgent-learningRate", .1);
        p.addDoubleParam("sarsaAgent-exploreRate", .05);
        rlVizLib.utilities.UtilityShop.setVersionDetails(p, new DetailsProvider());
        return p;
    }

    /**
     * This is a trick we can use to make the agent easily loadable.
     * @param args
     */
    public static void main(String[] args) {
        AgentLoader theLoader = new AgentLoader(new sarsaAgent());
        theLoader.run();
    }

    /**
     * So we can draw fancy value functions in the visualizer.
     * @param stateICareAbout
     * @return
     */
    public double getValueForState(Observation stateICareAbout) {
        //Need to be a bit careful because this can be called
        //before agent_init
        if (stateICareAbout == null || QTable == null) {
            return 0.0d;
        }
        int stateLabel = stateICareAbout.intArray[0];
        return getPolicyValue(QTable[stateLabel]);
    }
}

/**
 * This is a little helper class that fills in the details about this environment
 * for the fancy print outs in the visualizer application.
 * @author btanner
 */
class DetailsProvider implements hasVersionDetails {

    public String getName() {
        return "sarsaAgent For Sutton Class";
    }

    public String getShortName() {
        return "sarsaAgent";
    }

    public String getAuthors() {
        return "Brian Tanner";
    }

    public String getInfoUrl() {
        return " ";
    }

    public String getDescription() {
        return "Simple Sarsa agent created for Rich Suttons AI class.";
    }
}

