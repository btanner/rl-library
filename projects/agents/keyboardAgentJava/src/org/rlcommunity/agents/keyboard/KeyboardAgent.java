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

import org.rlcommunity.agents.keyboard.mappings.MountainCarKeyboardMapper;
import org.rlcommunity.agents.keyboard.mappings.AcrobotKeyboardMapper;
import org.rlcommunity.agents.keyboard.mappings.TetrisKeyBoardMapper;
import org.rlcommunity.agents.keyboard.mappings.GridWorldMapper;
import java.util.Random;

import org.rlcommunity.agents.keyboard.mappings.CartPoleKeyboardMapper;
import rlVizLib.general.ParameterHolder;
import rlVizLib.general.hasVersionDetails;

import org.rlcommunity.rlglue.codec.AgentInterface;
import org.rlcommunity.rlglue.codec.taskspec.TaskSpec;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;

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
public class KeyboardAgent implements AgentInterface {

    private Action action;
    private Random random = new Random();
    TaskSpec TSO = null;
    private KeyboardMapper theKeyBoardMapper;

    public KeyboardAgent() {
        this(getDefaultParameters());
    }

    public KeyboardAgent(ParameterHolder p) {
        super();
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
        String extraString = TSO.getExtraString();
        System.out.println("Extra string was: "+extraString);
        if (extraString.contains("EnvName:Mountain-Car")) {
            theKeyBoardMapper = new MountainCarKeyboardMapper();
        }
        if (extraString.contains("EnvName:Acrobot")) {
            theKeyBoardMapper = new AcrobotKeyboardMapper();
        }
        if(extraString.contains("EnvName:ContinuousGridWorld")){
            theKeyBoardMapper=new GridWorldMapper();
        }
        if(extraString.contains("EnvName:Tetris")){
            theKeyBoardMapper=new TetrisKeyBoardMapper();
        }
        if(extraString.contains("EnvName:CartPole")){
            theKeyBoardMapper=new CartPoleKeyboardMapper();
        }
        if (theKeyBoardMapper != null) {
            theKeyBoardMapper.ensureTaskSpecMatchesExpectation(TSO);
        }else{
            System.err.println("Didn't know how to make a keyboard agent from string: "+extraString);
        }
    }

    public Action agent_start(Observation o) {
        return theKeyBoardMapper.getAction(o);
    }

    public Action agent_step(double reward, Observation o) {
        return theKeyBoardMapper.getAction(reward, o);
    }

    public void agent_end(double reward) {
    }

    public void agent_cleanup() {
    }

    public String agent_message(String theMessage) {
        return null;
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

