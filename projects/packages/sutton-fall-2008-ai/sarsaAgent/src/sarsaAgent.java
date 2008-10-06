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
    private int numActions=0;
    private int numStates=0;
    
    private double[][] QTable=null;
    private double learningRate=.1;
    private double exploreRate=.05;

   public sarsaAgent() {
        this(getDefaultParameters());
    }

    public sarsaAgent(ParameterHolder p) {
        super();
        this.learningRate=p.getDoubleParam("sarsaAgent-learningRate");
        this.exploreRate=p.getDoubleParam("sarsaAgent-exploreRate");
   }


    public static ParameterHolder getDefaultParameters() {
        ParameterHolder p = new ParameterHolder();
        p.addDoubleParam("sarsaAgent-learningRate", .1);
        p.addDoubleParam("sarsaAgent-exploreRate", .05);
        rlVizLib.utilities.UtilityShop.setVersionDetails(p, new DetailsProvider());
        return p;
    }
    
    public void agent_init(String taskSpec) {
        TaskSpecObject TSO = new TaskSpecObject(taskSpec);
        numActions=(int)(TSO.action_maxs[0])+1;
        numStates=(int)(TSO.obs_maxs[0])+1;

        System.out.println("Agent thinks there are: "+numActions+" actions and: "+numStates+" states");
        QTable=new double[numStates][numActions];
    }

    public Action agent_start(Observation o) {
        int whichActionToReturn=0;

        int theState=o.intArray[0];
        if(randomGenerator.nextFloat()<=exploreRate){
            whichActionToReturn=randomGenerator.nextInt(numActions);
        }else{
            whichActionToReturn=getArgMax(QTable[theState]);
        }
        
        Action returnActionObject=new Action(1,0,0);
        returnActionObject.intArray[0]=whichActionToReturn;
        return returnActionObject;
    }

    public Action agent_step(double reward, Observation o) {
        int whichActionToReturn=0;

        int theState=o.intArray[0];
        if(randomGenerator.nextFloat()<=exploreRate){
            whichActionToReturn=randomGenerator.nextInt(numActions);
        }else{
            whichActionToReturn=getArgMax(QTable[theState]);
        }
        
        Action returnActionObject=new Action(1,0,0);
        returnActionObject.intArray[0]=whichActionToReturn;
        return returnActionObject;
    }

    public double getValue(int State, int action){
        return QTable[State][action];
    }

    public double getMaxValue(int State){
        int maxAction=getArgMax(QTable[State]);
        return QTable[State][maxAction];
    }

    public void agent_end(double reward) {
    }

    private int getArgMax(double[] values) {
        Vector<Integer> ties=new Vector<Integer>();
        double theMax=values[0];
        
        for(int i=1;i<values.length;i++){
            if(values[i]==theMax){
                ties.add(i);
            }
            if(values[i]>theMax){
                theMax=values[i];
                ties.clear();
                ties.add(i);
            }
        }
        //Break ties randomly
        int tieBreak=randomGenerator.nextInt(ties.size());
        return ties.get(tieBreak);
    }


    public void agent_cleanup() {
        QTable=null;
    }


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

    /**
     * This is a trick we can use to make the agent easily loadable.
     * @param args
     */
    public static void main(String[] args){
     	AgentLoader theLoader=new AgentLoader(new sarsaAgent());
        theLoader.run();
	}

    /**
     * So we can draw fancy value functions in the visualizer.
     * @param stateICareAbout
     * @return
     */
    public double getValueForState(Observation stateICareAbout) {
        int stateLabel=stateICareAbout.intArray[0];
        return getMaxValue(stateLabel);
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

