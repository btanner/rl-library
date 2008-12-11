package org.rlcommunity.environments.continuousgridworld;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.net.URL;
import java.util.Vector;

import org.rlcommunity.environments.continuousgridworld.messages.CGWMapResponse;

import org.rlcommunity.environments.continuousgridworld.visualizer.ContinuousGridWorldVisualizer;
import org.rlcommunity.rlglue.codec.taskspec.TaskSpec;
import org.rlcommunity.rlglue.codec.taskspec.TaskSpecVRLGLUE3;
import org.rlcommunity.rlglue.codec.taskspec.ranges.DoubleRange;
import org.rlcommunity.rlglue.codec.taskspec.ranges.IntRange;
import rlVizLib.Environments.EnvironmentBase;
import rlVizLib.general.ParameterHolder;
import rlVizLib.general.RLVizVersion;
import rlVizLib.messaging.NotAnRLVizMessageException;
import rlVizLib.messaging.environment.EnvironmentMessageParser;
import rlVizLib.messaging.environment.EnvironmentMessages;
import rlVizLib.messaging.interfaces.HasAVisualizerInterface;
import rlVizLib.messaging.interfaces.getEnvMaxMinsInterface;
import rlVizLib.messaging.interfaces.getEnvObsForStateInterface;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;

import org.rlcommunity.rlglue.codec.types.Reward_observation_terminal;


/*
 *  ContinuousGridWorld
 *
 *  Created by Brian Tanner on 02/03/07.
 *  Copyright 2007 Brian Tanner. All rights reserved.
 *
 */
import rlVizLib.general.hasVersionDetails;
import rlVizLib.messaging.environmentShell.TaskSpecPayload;
import rlVizLib.messaging.interfaces.HasImageInterface;


public class ContinuousGridWorld extends EnvironmentBase implements
        getEnvMaxMinsInterface,
        getEnvObsForStateInterface,
        HasAVisualizerInterface,
HasImageInterface{

    protected Point2D agentPos;
    protected Point2D agentSize;
    protected Rectangle2D currentAgentRect;
    protected Rectangle2D worldRect;
    protected Vector<Rectangle2D> resetRegions = new Vector<Rectangle2D>();
    protected Vector<Rectangle2D> barrierRegions = new Vector<Rectangle2D>();
    protected Vector<Double> thePenalties = new Vector<Double>();
    protected double walkSpeed = 25.0d;
    protected Vector<Rectangle2D> rewardRegions = new Vector<Rectangle2D>();
    protected Vector<Double> theRewards = new Vector<Double>();

    public static ParameterHolder getDefaultParameters() {
        ParameterHolder p = new ParameterHolder();
        rlVizLib.utilities.UtilityShop.setVersionDetails(p, new DetailsProvider());

        p.addDoubleParam("cont-grid-world-minX", 0.0d);
        p.addDoubleParam("cont-grid-world-minY", 0.0d);
        p.addDoubleParam("cont-grid-world-width", 200.0d);
        p.addDoubleParam("cont-grid-world-height", 200.0d);
        p.addDoubleParam("cont-grid-world-walk-speed", 10.0d);
        return p;
    }

    public ContinuousGridWorld() {
        this(getDefaultParameters());
    }

    public ContinuousGridWorld(ParameterHolder theParams) {

        double minX = theParams.getDoubleParam("cont-grid-world-minX");
        double minY = theParams.getDoubleParam("cont-grid-world-minY");
        double width = theParams.getDoubleParam("cont-grid-world-width");
        double height = theParams.getDoubleParam("cont-grid-world-height");
        walkSpeed = theParams.getDoubleParam("cont-grid-world-walk-speed");

        worldRect = new Rectangle2D.Double(minX, minY, width, height);
        agentSize = new Point2D.Double(1.0d, 1.0d);

        addResetRegion(new Rectangle2D.Double(75.0d, 75.0d, 25.0d, 25.0d));
        addRewardRegion(new Rectangle2D.Double(75.0d, 75.0d, 25.0d, 25.0d), 1.0d);

        addRewardRegion(new Rectangle2D.Double(0.0d, 0.0d, 200.0d, 200.0d), -1.0d);

        addBarrierRegion(new Rectangle2D.Double(50.0d, 50.0d, 10.0d, 100.0d), 1.0d);
        addBarrierRegion(new Rectangle2D.Double(50.0d, 50.0d, 100.0d, 10.0d), 1.0d);
        addBarrierRegion(new Rectangle2D.Double(150.0d, 50.0d, 10.0d, 100.0d), 1.0d);
    }

    protected Rectangle2D makeAgentSizedRectFromPosition(Point2D thePos) {
        return new Rectangle2D.Double(thePos.getX(), thePos.getY(), agentSize.getX(), agentSize.getY());
    }

    protected void updateCurrentAgentRect() {
        currentAgentRect = makeAgentSizedRectFromPosition(agentPos);
    }

    @Override
    protected Observation makeObservation() {
        Observation currentObs = new Observation(0, 2);
        currentObs.doubleArray[0] = agentPos.getX();
        currentObs.doubleArray[1] = agentPos.getY();
        return currentObs;
    }

    public void env_cleanup() {
    }

    public String env_init() {
        return makeTaskSpec();
    }

    private String makeTaskSpec() {
        TaskSpecVRLGLUE3 theTaskSpecObject = new TaskSpecVRLGLUE3();
        theTaskSpecObject.setEpisodic();
        theTaskSpecObject.setDiscountFactor(1.0d);
        theTaskSpecObject.addContinuousObservation(new DoubleRange(getWorldRect().getMinX(), getWorldRect().getMaxX()));
        theTaskSpecObject.addContinuousObservation(new DoubleRange(getWorldRect().getMinY(), getWorldRect().getMaxY()));
        theTaskSpecObject.addDiscreteAction(new IntRange(0, 3));
        theTaskSpecObject.setRewardRange(new DoubleRange(-1, 1));
        theTaskSpecObject.setExtra("EnvName:ContinuousGridWorld");
        String taskSpecString = theTaskSpecObject.toTaskSpec();
        TaskSpec.checkTaskSpec(taskSpecString);
        return taskSpecString;

    }

    public static TaskSpecPayload getTaskSpecPayload(ParameterHolder P) {
        ContinuousGridWorld theGridWorld = new ContinuousGridWorld(P);
        String taskSpec = theGridWorld.makeTaskSpec();
        return new TaskSpecPayload(taskSpec, false, "");
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
                CGWMapResponse theResponseObject = new CGWMapResponse(getWorldRect(), resetRegions, rewardRegions, theRewards, barrierRegions, thePenalties);
                return theResponseObject.makeStringResponse();
            }
        }
        System.err.println("We need some code written in Env Message for ContinuousGridWorld.. unknown request received: " + theMessage);
        Thread.dumpStack();
        return null;
    }

    public Observation env_start() {
        randomizeAgentPosition();

//		setAgentPosition(new Point2D.Double(startX,startY));

        while (calculateMaxBarrierAtPosition(currentAgentRect) >= 1.0f || !getWorldRect().contains(currentAgentRect)) {
            randomizeAgentPosition();
        }

        return makeObservation();

    }

    public Reward_observation_terminal env_step(Action action) {
        int theAction = action.intArray[0];

        double dx = 0;
        double dy = 0;

        //Should find a good way to abstract actions and add them in like the old wya, that was good

        if (theAction == 0) {
            dx = walkSpeed;
        }
        if (theAction == 1) {
            dx = -walkSpeed;
        }
        if (theAction == 2) {
            dy = walkSpeed;
        }
        if (theAction == 3) {
            dy = -walkSpeed;        //Add a small bit of random noise
        }
        double noiseX = .125d * (Math.random() - 0.5d);
        double noiseY = .125d * (Math.random() - 0.5d);

        dx += noiseX;
        dy += noiseY;
        Point2D nextPos = new Point2D.Double(agentPos.getX() + dx, agentPos.getY() + dy);


        nextPos = updateNextPosBecauseOfWorldBoundary(nextPos);
        nextPos = updateNextPosBecauseOfBarriers(nextPos);

        agentPos = nextPos;
        updateCurrentAgentRect();
        boolean inResetRegion = false;

        for (int i = 0; i < resetRegions.size(); i++) {
            if (resetRegions.get(i).contains(currentAgentRect)) {
                inResetRegion = true;
            }
        }

        return makeRewardObservation(getReward(), inResetRegion);
    }

    protected double getReward() {
        double reward = 0.0d;

        for (int i = 0; i < rewardRegions.size(); i++) {
            if (rewardRegions.get(i).contains(currentAgentRect)) {
                reward += theRewards.get(i);
            }
        }
        return reward;
    }

    protected Rectangle2D getAgent() {
        return currentAgentRect;

    }

    protected void setAgentPosition(Point2D dp) {
        this.agentPos = dp;
        updateCurrentAgentRect();
    }

    protected void addResetRegion(Rectangle2D resetRegion) {
        resetRegions.add(resetRegion);
    }

    //Penalty is between 0 and 1, its a movement penalty
    protected void addBarrierRegion(Rectangle2D barrierRegion, double penalty) {
        barrierRegions.add(barrierRegion);

        assert (penalty >= 0);
        assert (penalty <= 1);
        thePenalties.add(penalty);
    }

    protected void addRewardRegion(Rectangle2D rewardRegion, double reward) {
        rewardRegions.add(rewardRegion);
        theRewards.add(reward);
    }

    protected Rectangle2D getWorldRect() {
        return worldRect;
    }

    protected void randomizeAgentPosition() {
        double startX = Math.random() * getWorldRect().getWidth();
        double startY = Math.random() * getWorldRect().getHeight();

        startX = .1;
        startY = .1;

        setAgentPosition(new Point2D.Double(startX, startY));
    }

    protected double calculateMaxBarrierAtPosition(Rectangle2D r) {
        double maxPenalty = 0.0f;

        for (int i = 0; i < barrierRegions.size(); i++) {
            if (barrierRegions.get(i).intersects(r)) {
                double penalty = thePenalties.get(i);
                if (penalty > maxPenalty) {
                    maxPenalty = penalty;
                }
            }
        }
        return maxPenalty;
    }

    protected boolean intersectsResetRegion(Rectangle2D r) {
        for (int i = 0; i < resetRegions.size(); i++) {
            if (resetRegions.get(i).intersects(r)) {
                return true;
            }
        }
        return false;
    }

    protected Point2D updateNextPosBecauseOfBarriers(Point2D nextPos) {
        //See if the agent's current position is in a wall, if so we want to impede his movement.
        double penalty = calculateMaxBarrierAtPosition(currentAgentRect);

        double currentX = agentPos.getX();
        double currentY = agentPos.getY();

        double nextX = nextPos.getX();
        double nextY = nextPos.getY();

        double newNextX = currentX + ((nextX - currentX) * (1.0f - penalty));
        double newNextY = currentY + ((nextY - currentY) * (1.0f - penalty));

        nextPos.setLocation(newNextX, newNextY);
        //Now, find out if he's in an immobile obstacle... and if so move him out
        float fudgeCounter = 0;

        Rectangle2D nextPosRect = makeAgentSizedRectFromPosition(nextPos);
        while (calculateMaxBarrierAtPosition(nextPosRect) == 1.0f) {
            nextPos = findMidPoint(nextPos, agentPos);
            fudgeCounter++;
            if (fudgeCounter == 4) {
                nextPos = (Point2D) agentPos.clone();
                break;
            }
        }
        return nextPos;
    }

    protected Point2D findMidPoint(Point2D a, Point2D b) {
        double newX = (a.getX() + b.getX()) / 2.0d;
        double newY = (a.getY() + b.getY()) / 2.0d;
        return new Point2D.Double(newX, newY);
    }

    protected Point2D updateNextPosBecauseOfWorldBoundary(Point2D nextPos) {
        //Gotta do this somewhere
        int fudgeCounter = 0;
        Rectangle2D nextPosRect = makeAgentSizedRectFromPosition(nextPos);
        while (!getWorldRect().contains(nextPosRect)) {
            nextPos = findMidPoint(nextPos, agentPos);

            fudgeCounter++;
            if (fudgeCounter == 4) {
                nextPos = agentPos;
                break;
            }
        }
        return nextPos;
    }

    public double getMaxValueForQuerableVariable(int dimension) {
        if (dimension == 0) {
            return getWorldRect().getMaxX();
        }
        return getWorldRect().getMaxY();
    }

    public double getMinValueForQuerableVariable(int dimension) {
        if (dimension == 0) {
            return getWorldRect().getMinX();
        }
        return getWorldRect().getMinY();
    }

    public int getNumVars() {
        return 2;
    }

    public Observation getObservationForState(Observation theState) {
        return theState;
    }

    public RLVizVersion getTheVersionISupport() {
        return new RLVizVersion(1, 1);
    }

    public String getVisualizerClassName() {
        return ContinuousGridWorldVisualizer.class.getName();
    }

    public URL getImageURL() {
        return this.getClass().getResource("/images/cgwsplash.png");
    }
}
class DetailsProvider implements hasVersionDetails {

    public String getName() {
        return "Soft Obstacle Continuous Grid World 0.1";
    }

    public String getShortName() {
        return "Cont-Grid-World";
    }

    public String getAuthors() {
        return "Brian Tanner";
    }

    public String getInfoUrl() {
        return "http://library.rl-community.org/environments/continuousgridworld";
    }

    public String getDescription() {
        return "RL-Library Java Version of a continuous grid world with soft obstacles.";
    }
}