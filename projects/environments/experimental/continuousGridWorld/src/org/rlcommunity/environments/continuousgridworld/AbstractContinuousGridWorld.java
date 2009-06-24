/*
 *  Copyright 2009 Brian Tanner.
 *
 *  brian@tannerpages.com
 *  http://research.tannerpages.com
 *
 *  This source file is from one of:
 *  {rl-coda,rl-glue,rl-library,bt-agentlib,rl-viz}.googlecode.com
 *  Check out http://rl-community.org for more information!
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
package org.rlcommunity.environments.continuousgridworld;

import org.rlcommunity.environments.continuousgridworld.messages.StateResponse;
import java.net.URL;
import org.rlcommunity.environments.continuousgridworld.visualizer.ContinuousGridWorldVisualizer;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.types.Reward_observation_terminal;
import rlVizLib.Environments.EnvironmentBase;
import rlVizLib.general.ParameterHolder;
import rlVizLib.general.RLVizVersion;
import rlVizLib.messaging.NotAnRLVizMessageException;
import rlVizLib.messaging.environment.EnvMessageType;
import rlVizLib.messaging.environment.EnvironmentMessageParser;
import rlVizLib.messaging.environment.EnvironmentMessages;
import rlVizLib.messaging.interfaces.HasAVisualizerInterface;
import rlVizLib.messaging.interfaces.HasImageInterface;
import rlVizLib.messaging.interfaces.getEnvMaxMinsInterface;
import rlVizLib.messaging.interfaces.getEnvObsForStateInterface;

/**
 *
 * @author btanner
 */
public abstract class AbstractContinuousGridWorld extends EnvironmentBase implements HasAVisualizerInterface, HasImageInterface, getEnvMaxMinsInterface, getEnvObsForStateInterface {

    protected final State theState;

    public static ParameterHolder getDefaultParameters() {
        ParameterHolder p = new ParameterHolder();
        p.addBooleanParam("UseVelocities", false);
        p.addIntegerParam("RandomSeed(0 means random)",0);
        p.addBooleanParam("RandomStartStates", false);
        p.addDoubleParam("TransitionNoise[0,1]", 0.0d);
        p.setAlias("noise","TransitionNoise[0,1]");
        p.setAlias("seed","RandomSeed(0 means random)");
        return p;
    }

    public AbstractContinuousGridWorld(ParameterHolder theParams) {
        boolean useVelocities = false;
        boolean randomStartStates = false;
        double transitionNoise = 0.0d;
        long randomSeed=0L;
        if (theParams != null) {
            if (!theParams.isNull()) {
                useVelocities = theParams.getBooleanParam("UseVelocities");
                randomStartStates = theParams.getBooleanParam("RandomStartStates");
                transitionNoise = theParams.getDoubleParam("noise");
                randomSeed=theParams.getIntegerParam("seed");
            }
        }
        if (useVelocities) {
            theState = new StateWithVelocities(randomStartStates,transitionNoise,randomSeed);
        } else {
            theState = new State(randomStartStates,transitionNoise,randomSeed);
        }

        addBarriersAndGoal(theParams);
    }

    protected abstract void addBarriersAndGoal(ParameterHolder theParams);

    public void env_cleanup() {
    }

    protected abstract String makeTaskSpec();

    public String env_init() {
        return makeTaskSpec();
    }

    public String env_message(String theMessage) {
        EnvironmentMessages theMessageObject;
        try {
            theMessageObject = EnvironmentMessageParser.parseMessage(theMessage);
        } catch (NotAnRLVizMessageException e) {
            System.err.println("Someone sent a Grid World message that wasn\'t RL-Viz compatible");
            return "I only respond to RL-Viz messages!";
        }
        if (theMessageObject.canHandleAutomatically(this)) {
            return theMessageObject.handleAutomatically(this);
        }
        if (theMessageObject.getTheMessageType() == EnvMessageType.kEnvCustom.id()) {
            String theCustomType = theMessageObject.getPayLoad();

            if (theCustomType.equals("GETSTATE")) {
                StateResponse theResponseObject = new StateResponse(theState);
                return theResponseObject.makeStringResponse();
            }
        }
        System.err.println("We need some code written in Env Message for " + AbstractContinuousGridWorld.class + "... unknown request received: " + theMessage);
        Thread.dumpStack();
        return null;
    }

    public Observation env_start() {
        theState.reset();
        return makeObservation();
    }

    public Reward_observation_terminal env_step(Action action) {
        int theAction = action.intArray[0];

        theState.update(theAction);

        return makeRewardObservation(theState.getReward(), theState.inResetRegion());
    }

    public URL getImageURL() {
        return this.getClass().getResource("/images/cgwsplash.png");
    }

    public double getMaxValueForQuerableVariable(int dimension) {
        if (dimension == 0 || dimension == 1) {
            return 100.0d;
        } else {
            return 5.0d;
        }
    }

    public double getMinValueForQuerableVariable(int dimension) {
        if (dimension == 0 || dimension == 1) {
            return 0.0d;
        } else {
            return -5.0d;
        }
    }

    public int getNumVars() {
        return theState.getNumVars();
    }

    public Observation getObservationForState(Observation theQueryState) {
        return theState.getObservationForState(theQueryState);
    }

    public RLVizVersion getTheVersionISupport() {
        return new RLVizVersion(1, 1);
    }

    public String getVisualizerClassName() {
        return ContinuousGridWorldVisualizer.class.getName();
    }

    @Override
    protected Observation makeObservation() {
        return theState.makeObservation();
    }
}
