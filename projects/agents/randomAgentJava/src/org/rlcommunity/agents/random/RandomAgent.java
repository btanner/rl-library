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
package org.rlcommunity.agents.random;

import java.util.Random;

import rlVizLib.general.ParameterHolder;
import rlVizLib.general.hasVersionDetails;

import org.rlcommunity.rlglue.codec.AgentInterface;
import org.rlcommunity.rlglue.codec.taskspec.TaskSpec;
import org.rlcommunity.rlglue.codec.taskspec.ranges.AbstractRange;
import org.rlcommunity.rlglue.codec.taskspec.ranges.DoubleRange;
import org.rlcommunity.rlglue.codec.taskspec.ranges.IntRange;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.util.AgentLoader;
import rlVizLib.messaging.agentShell.TaskSpecResponsePayload;

/**
 * Simple random agent that can do multidimensional continuous or discrete
 * actions.
 * @author btanner
 */
public class RandomAgent implements AgentInterface {

    private Action action;
    private Random random = new Random();
    TaskSpec TSO = null;

    public RandomAgent() {
        this(getDefaultParameters());
    }

    public RandomAgent(ParameterHolder p) {
        super();
    }
    
    /**
     * Random agent can take any task spec.
     * @param P
     * @param TaskSpec
     * @return
     */
    public static TaskSpecResponsePayload isCompatible(ParameterHolder P, String TaskSpec){
        return new TaskSpecResponsePayload(false,"");
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

    public static void main(String[] args){
        AgentLoader L=new AgentLoader(new RandomAgent());
        L.run();
    }
    public void agent_init(String taskSpec) {
        TSO = new TaskSpec(taskSpec);

        //Do some checking on the ranges here so we don't feel bad if we crash later for not checking them.
        for (int i = 0; i < TSO.getNumDiscreteActionDims(); i++) {
            AbstractRange thisActionRange = TSO.getDiscreteActionRange(i);

            if (thisActionRange.hasSpecialMinStatus() || thisActionRange.hasSpecialMaxStatus()) {
                System.err.println("The random agent does not know how to deal with actions that are unbounded or unspecified ranges.");
            }
        }
        for (int i = 0; i < TSO.getNumContinuousActionDims(); i++) {
            AbstractRange thisActionRange = TSO.getContinuousActionRange(i);

            if (thisActionRange.hasSpecialMinStatus() || thisActionRange.hasSpecialMaxStatus()) {
                System.err.println("The random agent does not know how to deal with actions that are unbounded or unspecified ranges.");
            }
        }

        action = new Action(TSO.getNumDiscreteActionDims(), TSO.getNumContinuousActionDims());
    }

    public Action agent_start(Observation o) {
        setRandomActions(action);
        return action;
    }

    private int step=0;
    public Action agent_step(double arg0, Observation o) {
        step++;
        if(step%500==0)System.out.println("Agent on step: "+step);
        setRandomActions(action);
        return action;
    }

    public void agent_end(double reward) {
    }

    private void setRandomActions(Action action) {
        for (int i = 0; i < TSO.getNumDiscreteActionDims(); i++) {
            IntRange thisActionRange = TSO.getDiscreteActionRange(i);
            action.intArray[i] = random.nextInt(thisActionRange.getRangeSize()) + thisActionRange.getMin();
        }
        for (int i = 0; i < TSO.getNumContinuousActionDims(); i++) {
            DoubleRange thisActionRange = TSO.getContinuousActionRange(i);
            action.doubleArray[i] = random.nextDouble() * (thisActionRange.getRangeSize()) + thisActionRange.getMin();
        }
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

