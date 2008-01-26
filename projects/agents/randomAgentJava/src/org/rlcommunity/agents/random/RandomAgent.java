/* Random Agent that works in all domains
 * Copyright (C) 2007, Brian Tanner brian@tannerpages.com (http://brian.tannerpages.com/)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA. */
package org.rlcommunity.agents.random;

import java.util.Random;

import rlVizLib.general.ParameterHolder;
import rlVizLib.general.hasVersionDetails;
import rlVizLib.utilities.TaskSpecObject;

import rlglue.agent.Agent;
import rlglue.types.Action;
import rlglue.types.Observation;

/**
 * Simple random agent that can do multidimensional continuous or discrete
 * actions.
 * @author btanner
 */
public class RandomAgent implements Agent {

    private Action action;
    private int numInts = 1;
    private int numDoubles = 0;
    private Random random = new Random();
    TaskSpecObject TSO = null;

   public RandomAgent() {
        this(getDefaultParameters());
    }

    public RandomAgent(ParameterHolder p) {
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

