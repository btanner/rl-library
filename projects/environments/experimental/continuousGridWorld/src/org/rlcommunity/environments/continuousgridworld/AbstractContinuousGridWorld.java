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
        return p;
    }

    public AbstractContinuousGridWorld(ParameterHolder theParams) {
        theState = new State();
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
        return 4;
    }

    public Observation getObservationForState(Observation theQueryState) {
        //Value function only loops over dims 1 and 2 so we have to add the others.
        Observation theObs = new Observation(0, 4, 0);
        //States are in [0,100], observations are in [0,1]
        theObs.doubleArray[0] = theQueryState.doubleArray[0] / 100.0d;
        theObs.doubleArray[1] = theQueryState.doubleArray[1] / 100.0d;
        //States are in [-5,5], observations are in [0,1]
        //Use the current velocity to draw the VF
        Point2D theVelocity = theState.getAgentVelocity();
        theObs.doubleArray[2] = (theVelocity.getX() + 5.0d) / 10.0d;
        theObs.doubleArray[3] = (theVelocity.getY() + 5.0d) / 10.0d;

        //Use this code to draw velocity=0 value function.
//        theObs.doubleArray[2]=.5d;
//        theObs.doubleArray[3]=.5d;
        return theObs;
    }

    public RLVizVersion getTheVersionISupport() {
        return new RLVizVersion(1, 1);
    }

    public String getVisualizerClassName() {
        return ContinuousGridWorldVisualizer.class.getName();
    }

    @Override
    protected Observation makeObservation() {
        Observation currentObs = new Observation(0, 4);
        Point2D theAgent = theState.getAgentPosition();
        Point2D theVelocity = theState.getAgentVelocity();

        //Normalize each to 0,1
        currentObs.doubleArray[0] = theAgent.getX() / 100.0d;
        currentObs.doubleArray[1] = theAgent.getY() / 100.0d;

        currentObs.doubleArray[2] = (theVelocity.getX() + 5.0d) / 10.0d;
        currentObs.doubleArray[3] = (theVelocity.getY() + 5.0d) / 10.0d;
        return currentObs;
    }
}
