/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rlcommunity.environments.continuousgridworld;

import org.rlcommunity.environments.continuousgridworld.messages.StateResponse;
import java.awt.geom.Point2D;
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
        return p;
    }

    public AbstractContinuousGridWorld(ParameterHolder theParams) {
        boolean useVelocities = false;
        if (theParams != null) {
            if (!theParams.isNull()) {
                useVelocities = theParams.getBooleanParam("UseVelocities");
            }
        }
        if (useVelocities) {
            theState = new StateWithVelocities();
        } else {
            theState = new State();
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
