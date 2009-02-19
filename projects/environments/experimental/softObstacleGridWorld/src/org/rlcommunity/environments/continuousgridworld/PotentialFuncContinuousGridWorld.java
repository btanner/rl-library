/*
 * (c) 2009 Marc G. Bellemare.
 */

package org.rlcommunity.environments.continuousgridworld;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import org.rlcommunity.rlglue.codec.taskspec.TaskSpec;
import org.rlcommunity.rlglue.codec.taskspec.TaskSpecVRLGLUE3;
import org.rlcommunity.rlglue.codec.taskspec.ranges.DoubleRange;
import org.rlcommunity.rlglue.codec.taskspec.ranges.IntRange;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Reward_observation_terminal;
import rlVizLib.general.ParameterHolder;
import rlVizLib.messaging.environmentShell.TaskSpecPayload;

/** An extension of the Continuous grid world that includes a specified goal
 *   and the possibility to provide distance-based potential function reward
 *   to the agent.
 *
 * @author Marc G. Bellemare (mg17 at cs ualberta ca)
 */
public class PotentialFuncContinuousGridWorld extends ContinuousGridWorld {
    protected boolean usePotentialFunction;
    protected boolean useBarriers;
    
    protected Point2D lastAgentPos;
    protected Point2D goalPos;

    protected double shapingRewardScale;
    
    public static ParameterHolder getDefaultParameters() {
        ParameterHolder p = ContinuousGridWorld.getDefaultParameters();

        // Default goal (centered at 87.5)
        p.addDoubleParam("cont-grid-world-goalX", 87.5);
        p.addDoubleParam("cont-grid-world-goalY", 87.5);
        p.addBooleanParam("give-potential-function-reward", false);
        p.addBooleanParam("use-cup-barriers", true);
        
        return p;
    }

    public PotentialFuncContinuousGridWorld() {
        this(getDefaultParameters());
    }

    public PotentialFuncContinuousGridWorld(ParameterHolder theParams) {
        super(theParams);
    }

    public void addBarriersAndGoal(ParameterHolder theParams) {
        double width = theParams.getDoubleParam("cont-grid-world-width");
        double height = theParams.getDoubleParam("cont-grid-world-height");
        double goalX = theParams.getDoubleParam("cont-grid-world-goalX");
        double goalY = theParams.getDoubleParam("cont-grid-world-goalY");

        goalPos = new Point2D.Double(goalX, goalY);

        usePotentialFunction = theParams.getBooleanParam("give-potential-function-reward");
        useBarriers = theParams.getBooleanParam("use-cup-barriers");

        double goalWidth, goalHeight;
        goalWidth = goalHeight = 25.0;

        addResetRegion(new Rectangle2D.Double(
                goalX-goalWidth/2,
                goalY-goalHeight/2,
                goalWidth, goalHeight));
        addRewardRegion(new Rectangle2D.Double(
                goalX-goalWidth/2,
                goalY-goalHeight/2,
                goalWidth, goalHeight), 1.0);

        if (useBarriers) {
            addBarrierRegion(new Rectangle2D.Double(50.0d, 50.0d, 10.0d, 100.0d), 1.0d);
            addBarrierRegion(new Rectangle2D.Double(50.0d, 50.0d, 100.0d, 10.0d), 1.0d);
            addBarrierRegion(new Rectangle2D.Double(150.0d, 50.0d, 10.0d, 100.0d), 1.0d);
        }

        // Set the shaping reward scale, which is the maximum distance in the world
        shapingRewardScale = 1;//Math.sqrt(width*width + height*height);
    }
    
    @Override
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
        theTaskSpecObject.setExtra("EnvName:PotentialFuncContinuousGridWorld");
        String taskSpecString = theTaskSpecObject.toTaskSpec();
        TaskSpec.checkTaskSpec(taskSpecString);
        return taskSpecString;

    }

    public static TaskSpecPayload getTaskSpecPayload(ParameterHolder P) {
        PotentialFuncContinuousGridWorld theGridWorld =
                new PotentialFuncContinuousGridWorld(P);
        String taskSpec = theGridWorld.makeTaskSpec();
        return new TaskSpecPayload(taskSpec, false, "");
    }

    @Override
    public Reward_observation_terminal env_step(Action action) {
        // This gets called after env_start, so agentPos will not be null
        lastAgentPos = agentPos;
        return super.env_step(action);
    }

    /** Provide the agent with some reward, possibly with a potential function
     *    bonus added in
     * 
     * @return The reward for the current state
     */
    @Override
    protected double getReward() {
        double baseReward = super.getReward();
        double shapingReward;

        if (usePotentialFunction) {
            double lastDist = lastAgentPos.distance(goalPos);
            double dist = agentPos.distance(goalPos);

            shapingReward = (lastDist - dist) / shapingRewardScale;
        }
        else shapingReward = 0;

        return baseReward + shapingReward;
    }
}
