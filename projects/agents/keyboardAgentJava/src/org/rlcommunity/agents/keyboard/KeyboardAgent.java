/*
Copyright 2007 Brian Tanner
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
package org.rlcommunity.agents.keyboard;

import java.net.URL;
import java.util.Random;

import org.rlcommunity.agents.keyboard.messages.TaskSpecResponse;
import org.rlcommunity.agents.keyboard.messages.TellAgentWhatToDoResponse;
import org.rlcommunity.agents.keyboard.visualizer.KeyboardActionVizComponent;
import org.rlcommunity.agents.keyboard.visualizer.KeyboardAgentVisualizer;
import rlVizLib.general.ParameterHolder;
import rlVizLib.general.hasVersionDetails;

import org.rlcommunity.rlglue.codec.AgentInterface;
import org.rlcommunity.rlglue.codec.taskspec.TaskSpec;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;
import rlVizLib.messaging.NotAnRLVizMessageException;
import rlVizLib.messaging.agent.AgentMessageParser;
import rlVizLib.messaging.agent.AgentMessages;
import rlVizLib.messaging.agentShell.TaskSpecResponsePayload;
import rlVizLib.messaging.interfaces.HasAVisualizerInterface;
import rlVizLib.messaging.interfaces.HasImageInterface;


/**
 * Keyboard agent that lets you manually control the agent.
 * Unfortunately, any RL-Viz visualizer will always necessarily be one 
 * step behind the real state because RL_step is atomic and does a whole 
 * step at a time.  So, when watching the simulation in RL-Viz and controlling
 * with the keyboard remember that there is a 1-step delay on your action (or conversely
 * that you are looking at a 1-step delayed observation.  Basically, on step 0
 * you get to see the initial state, and the agent has already picked an action for it.
 * @author btanner
 */
public class KeyboardAgent implements AgentInterface,HasAVisualizerInterface,HasImageInterface {

    private Action action;
    TaskSpec TSO = null;

    public KeyboardAgent() {
        this(getDefaultParameters());
    }

    public KeyboardAgent(ParameterHolder p) {
        super();
    }
    
    public static TaskSpecResponsePayload isCompatible(ParameterHolder P, String TaskSpec){
        boolean supported=KeyboardActionVizComponent.supportsEnvironment(TaskSpec);
        if(supported){
            return new TaskSpecResponsePayload(false,"");
        }else{
            return new TaskSpecResponsePayload(true,"Keyboard agent does not have a mapping for this environment.");
        }
    }


    /**
     * Tetris doesn't really have any parameters
     * @return
     */
    public static ParameterHolder getDefaultParameters() {
        ParameterHolder p = new ParameterHolder();
        rlVizLib.utilities.UtilityShop.setVersionDetails(p, new DetailsProvider());
        return p;
    }

    public void agent_init(String taskSpec) {
        TSO = new TaskSpec(taskSpec);
        action=new Action(TSO.getNumDiscreteActionDims(),TSO.getNumContinuousActionDims(),0);
//        System.out.println("TSO tells us there are: "+TSO.getNumDiscreteActionDims()+" discrete actions");
//        System.out.println(taskSpec);
//        System.out.println(TSO.getStringRepresentation());

    }

    public Action agent_start(Observation o) {
        return action;
    }

    public Action agent_step(double reward, Observation o) {
        return action;
    }

    public void agent_end(double reward) {
    }

    public void agent_cleanup() {
    }

    public String agent_message(String theMessage) {
        AgentMessages theMessageObject;
        try {
            theMessageObject = AgentMessageParser.parseMessage(theMessage);
        } catch (NotAnRLVizMessageException e) {
            System.err.println("Someone sent keyboard agent a message that wasn't RL-Viz compatible");
            return "I only respond to RL-Viz messages!";
        }

        if (theMessageObject.canHandleAutomatically(this)) {
            return theMessageObject.handleAutomatically(this);
        }
        
        if (theMessageObject.getTheMessageType() == rlVizLib.messaging.agent.AgentMessageType.kAgentCustom.id()) {

            String theCustomType = theMessageObject.getPayLoad();

            if (theCustomType.startsWith("SETACTION")) {
                String[] splitPayload=theCustomType.split(":");
                for(int i=1;i<splitPayload.length;i++){
                    action.intArray[i-1]=Integer.parseInt(splitPayload[i]);
                }
                TellAgentWhatToDoResponse theResponseObject = new TellAgentWhatToDoResponse();
                return theResponseObject.makeStringResponse();
            }
            if (theCustomType.startsWith("GETTASKSPEC")) {
                if(TSO==null){
                    System.out.println("Was asked for task spec before RL_init was called");
                    
                }
                TaskSpecResponse theResponseObject = TaskSpecResponse.makeResponseFromTaskSpec(TSO.getStringRepresentation());
                return theResponseObject.makeStringResponse();
            }

        }
//        System.err.println("We need some code written in agent_message for keyboard agent. unknown request received: " + theMessage);
//        Thread.dumpStack();
        return null;
    }
    public String getVisualizerClassName() {
        return KeyboardAgentVisualizer.class.getName();
    }

    public URL getImageURL() {
        return this.getClass().getResource("/images/keyboardagent.png");
    }
}
/**
 * This is a little helper class that fills in the details about this environment
 * for the fancy print outs in the visualizer application.
 * @author btanner
 */
class DetailsProvider implements hasVersionDetails {

    public String getName() {
        return "Keyboard Agent 1.0";
    }

    public String getShortName() {
        return "Keyboard Agent";
    }

    public String getAuthors() {
        return "Brian Tanner";
    }

    public String getInfoUrl() {
        return "http://library.rl-community.org";
    }

    public String getDescription() {
        return "RL-Library Java Version of a keyboard agent.";
    }
}

