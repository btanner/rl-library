/* 
 * Copyright (C) 2007, Brian Tanner
 * 
http://rl-library.googlecode.com/
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
package DiscreteGridWorld;

import ContinuousGridWorld.ContinuousGridWorld;
import ContinuousGridWorld.messages.MapResponse;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import rlVizLib.general.ParameterHolder;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.types.Reward_observation;
import rlVizLib.messaging.NotAnRLVizMessageException;
import rlVizLib.messaging.environment.EnvironmentMessageParser;
import rlVizLib.messaging.environment.EnvironmentMessages;
import rlVizLib.messaging.interfaces.HasAVisualizerInterface;

/**
 * This is very much like the Continuous Grid World, but we have a discretized, 
 * labelled integer state representation instead of continuous state variables, and movement
 * is restricted to be a fixed distance in x,y giving us a more traditional 
 * discrete grid world.  
 * <p>
 * Good time to mention, this code isn't actually meant to work with world that
 * don't start at (0,0).  Positive starting positions will just falsely mean
 * more states.  Negative starting positions will break.
 * 
 * @author Brian Tanner
 */
public class DiscreteGridWorld extends ContinuousGridWorld implements HasAVisualizerInterface {

    protected int xDiscFactor = 10;
    protected int yDiscFactor = 10;
    private int numRows = 0;
    private int numCols = 0;

    /**
     * Not going to pass on params from above, we'll go with defaults
    
     * @return
     */
    public DiscreteGridWorld(ParameterHolder theParams) {
        super();
        int maxDiscY = (int) (getWorldRect().getHeight() / yDiscFactor);
        int maxDiscX = (int) (getWorldRect().getWidth() / xDiscFactor);
        numCols = maxDiscY;
        numRows = maxDiscX;

        setupObstaclesAndStuff();
    }

    private void setupObstaclesAndStuff() {
        /*Goal*/
        addResetRegion(new Rectangle2D.Double(70.0d, 70.0d, 20.0d, 20.0d));
        addRewardRegion(new Rectangle2D.Double(70.0d, 70.0d, 20.0d, 20.0d), 1.0d);


        /*-1 per step everywhere*/
        addRewardRegion(new Rectangle2D.Double(0.0d, 0.0d, 200.0d, 200.0d), -1.0d);

        addBarrierRegion(new Rectangle2D.Double(30.0d, 30.0d, 150.0d, 10.0d), 1.0d);
        addBarrierRegion(new Rectangle2D.Double(30.0d, 30.0d, 10.0d, 150.0d), 1.0d);


        addBarrierRegion(new Rectangle2D.Double(30.0d, 170.0d, 130.0d, 10.0d), 1.0d);
        addBarrierRegion(new Rectangle2D.Double(170.0d, 30.0d, 10.0d, 130.0d), 1.0d);


        addBarrierRegion(new Rectangle2D.Double(50.0d, 50.0d, 10.0d, 100.0d), 1.0d);
        addBarrierRegion(new Rectangle2D.Double(50.0d, 50.0d, 100.0d, 10.0d), 1.0d);
        addBarrierRegion(new Rectangle2D.Double(150.0d, 50.0d, 10.0d, 120.0d), 1.0d);

    }

    public static ParameterHolder getDefaultParameters() {
        ParameterHolder p = new ParameterHolder();
        return p;

    }

    protected int getMaxState() {
        int maxState = getState(getWorldRect().getWidth(), getWorldRect().getHeight());
        return maxState;
    }

    private int getRow(double x) {
        return (int) (x / xDiscFactor);
    }

    private int getCol(double y) {
        return (int) (y / yDiscFactor);
    }

    /**
     * Return a labeled state from a continuous (x,y) pair.
     * @param x
     * @param y
     * @return
     */
    protected int getState(double x, double y) {
        int theState = getCol(y) * numCols + getRow(x);
        return theState;
    }

    /**
     * As a hack for now, am actually putting together BOTH the integer and double
     * state, just because the visualizer is dumb and I don't feel like writing 
     * custom messages right now.
     * @param x
     * @param y
     * @return
     */
    protected Observation makeObservation(double x, double y) {
        Observation currentObs = new Observation(1, 0, 0);
        currentObs.intArray[0] = getState(x, y);

        return currentObs;
    }

    @Override
    protected Observation makeObservation() {
        return makeObservation(agentPos.getX(), agentPos.getY());
    }

    @Override
    public String env_init() {
        int taskSpecVersion = 2;
        String theTaskSpec = taskSpecVersion + ":e:1_[i]_[";
        theTaskSpec += 0 + "," + getMaxState() + "]:1_[i]_[0,3]:[-1,1]";
        return theTaskSpec;
    }

    protected void discretizeAgentPos() {
        double x = agentPos.getX();
        double y = agentPos.getY();
        x = xDiscFactor * (x / xDiscFactor);
        y = yDiscFactor * (y / yDiscFactor);
        setAgentPosition(new Point2D.Double(x, y));
    }

    /**
     * This should mostly work out ok, even if things are discretized.  I guess we can 
     * do the work to move him to a discrete spot
     */
    @Override
    public Observation env_start() {
        setAgentPosition(new Point2D.Double(5, 5));
        discretizeAgentPos();


        while (calculateMaxBarrierAtPosition(currentAgentRect) >= 1.0f || !getWorldRect().contains(currentAgentRect)) {
            randomizeAgentPosition();
            discretizeAgentPos();
        }
        return makeObservation();

    }

    @Override
    public Reward_observation env_step(Action action) {
        int theAction = action.intArray[0];

        double dx = 0;
        double dy = 0;

        if (theAction == 0) {
            dx = xDiscFactor;
        }
        if (theAction == 1) {
            dx = -xDiscFactor;
        }
        if (theAction == 2) {
            dy = yDiscFactor;
        }
        if (theAction == 3) {
            dy = -yDiscFactor;
        }

        Point2D nextPos = new Point2D.Double(agentPos.getX() + dx, agentPos.getY() + dy);

        nextPos = updateNextPosBecauseOfWorldBoundary(nextPos);
        nextPos = updateNextPosBecauseOfBarriers(nextPos);

        agentPos = nextPos;
        discretizeAgentPos();
        updateCurrentAgentRect();
        boolean inResetRegion = false;

        for (int i = 0; i < resetRegions.size(); i++) {
            if (resetRegions.get(i).contains(currentAgentRect)) {
                inResetRegion = true;
            }
        }

        return makeRewardObservation(getReward(), inResetRegion);
    }

    @Override
    public Observation getObservationForState(Observation theState) {
        double x = theState.doubleArray[0];
        double y = theState.doubleArray[1];
        return makeObservation(x, y);
    }

    public String env_message(String theMessage) {
        EnvironmentMessages theMessageObject;
        try {
            theMessageObject = EnvironmentMessageParser.parseMessage(theMessage);
        } catch (NotAnRLVizMessageException e) {
            System.err.println("Someone sent mountain Car a message that wasn't RL-Viz compatible");
            return "I only respond to RL-Viz messages!";
        }

        if (theMessageObject.canHandleAutomatically(this)) {
            return theMessageObject.handleAutomatically(this);//		If it wasn't handled automatically, maybe its a custom Mountain Car Message
        }
        if (theMessageObject.getTheMessageType() == rlVizLib.messaging.environment.EnvMessageType.kEnvCustom.id()) {

            String theCustomType = theMessageObject.getPayLoad();

            if (theCustomType.equals("GETCGWMAP")) {
                //It is a request for the state
                MapResponse theResponseObject = new MapResponse(getWorldRect(), resetRegions, rewardRegions, theRewards, barrierRegions, thePenalties, numRows, numCols);
                return theResponseObject.makeStringResponse();
            }
        }
        System.err.println("We need some code written in Env Message for ContinuousGridWorld.. unknown request received: " + theMessage);
        Thread.dumpStack();
        return null;
    }

    public String getVisualizerClassName() {
        return "visualizers.ContinuousGridWorld.DiscreteGridWorldVisualizer";
    }
}
