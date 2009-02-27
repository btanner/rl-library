/*
 * (c) 2009 Marc G. Bellemare.
 */

package org.rlcommunity.environments.continuousgridworld;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Random;
import java.util.Vector;
import org.rlcommunity.environments.continuousgridworld.visualizer.DiscontinuousContinuousGridWorldVisualizer;
import org.rlcommunity.rlglue.codec.taskspec.TaskSpec;
import org.rlcommunity.rlglue.codec.taskspec.TaskSpecVRLGLUE3;
import org.rlcommunity.rlglue.codec.taskspec.ranges.DoubleRange;
import org.rlcommunity.rlglue.codec.taskspec.ranges.IntRange;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.types.Reward_observation_terminal;
import rlVizLib.general.ParameterHolder;
import rlVizLib.messaging.NotAnRLVizMessageException;
import rlVizLib.messaging.environment.EnvObsForStateRequest;
import rlVizLib.messaging.environment.EnvObsForStateResponse;
import rlVizLib.messaging.environment.EnvironmentMessageParser;
import rlVizLib.messaging.environment.EnvironmentMessages;
import rlVizLib.messaging.environmentShell.TaskSpecPayload;

/** An extension of the Continuous grid world that includes a specified goal
 *   and the possibility to provide distance-based potential function reward
 *   to the agent.
 *
 *  This version does not include the potential function as part of the
 *  environment, but allows the use of a 'nasty' observation mapping.
 *
 * @author Marc G. Bellemare (mg17 at cs ualberta ca)
 */
public class DiscontinuousContinuousGridWorld extends ContinuousGridWorld {
    public static final int MAP_EMPTY = 0;
    public static final int MAP_CUP   = 1;

    public static final int MAPPING_DIRECT = 0;
    public static final int MAPPING_DISCONTINUOUS_TILES = 1;
    
    public static final int numActions = 4;
    public static final int numObsDimensions = 2;
    
    protected int mapNumber;
    protected int mappingType;
    
    protected final Random randomMaker;

    protected Point2D lastAgentPos;
    protected Point2D.Double goalPos;
    protected Point2D.Double startPos;
    
    protected double randomActionProbability;
    protected double movementNoise;

    protected double discountFactor = 1.0;

    protected ObservationMapping obsMapping;
    
    public static ParameterHolder getDefaultParameters() {
        ParameterHolder p = ContinuousGridWorld.getDefaultParameters();

        // Default goal (centered at 87.5)
        p.addDoubleParam("cont-grid-world-goalX", 87.5);
        p.addDoubleParam("cont-grid-world-goalY", 87.5);
        p.addDoubleParam("cont-grid-world-startX", 0.1);
        p.addDoubleParam("cont-grid-world-startY", 0.1);

        p.addIntegerParam("observation-mapping", MAPPING_DISCONTINUOUS_TILES);

        p.addIntegerParam("map-number", MAP_EMPTY);
        p.addDoubleParam("random-action-prob", 0.0);
        p.addDoubleParam("movement-noise", 0.0);

        // Mapping-specific parameters, unclear whether they should be here
        p.addIntegerParam("obs-mapping-cells-per-dimension", 2);

        return p;
    }

    public DiscontinuousContinuousGridWorld() {
        this(getDefaultParameters());
    }

    public DiscontinuousContinuousGridWorld(ParameterHolder theParams) {
        super(theParams);

        randomMaker = new Random();
        createMapping(mappingType, theParams);
    }

    @Override
    public void addBarriersAndGoal(ParameterHolder theParams) {
        double width = theParams.getDoubleParam("cont-grid-world-width");
        double height = theParams.getDoubleParam("cont-grid-world-height");
        double goalX = theParams.getDoubleParam("cont-grid-world-goalX");
        double goalY = theParams.getDoubleParam("cont-grid-world-goalY");
        double startX = theParams.getDoubleParam("cont-grid-world-startX");
        double startY = theParams.getDoubleParam("cont-grid-world-startY");

        goalPos = new Point2D.Double(goalX, goalY);
        startPos = new Point2D.Double(startX, startY);
        
        mappingType = theParams.getIntegerParam("observation-mapping");
        mapNumber = theParams.getIntegerParam("map-number");

        randomActionProbability = theParams.getDoubleParam("random-action-prob");
        movementNoise = theParams.getDoubleParam("movement-noise");
        
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

        createMap(mapNumber);
    }

    private void createMap(int number) {
        switch (number) {
            case MAP_EMPTY: // Empty map
                break;
            case MAP_CUP: // Cup map
                addBarrierRegion(new Rectangle2D.Double(50.0d, 50.0d, 10.0d, 100.0d), 1.0d);
                addBarrierRegion(new Rectangle2D.Double(50.0d, 50.0d, 100.0d, 10.0d), 1.0d);
                addBarrierRegion(new Rectangle2D.Double(150.0d, 50.0d, 10.0d, 100.0d), 1.0d);
                break;
            default:
                throw new IllegalArgumentException ("Map number "+number);
        }
    }

    private void createMapping(int number, ParameterHolder theParams) {
        double width = theParams.getDoubleParam("cont-grid-world-width");
        double height = theParams.getDoubleParam("cont-grid-world-height");

        switch(number) {
            case MAPPING_DIRECT:
                // @todo should create a direct mapping
                obsMapping = null;
                break;
            case MAPPING_DISCONTINUOUS_TILES:
                int numCells = theParams.getIntegerParam("obs-mapping-cells-per-dimension");
                obsMapping = new ScaledMapping(new TiledObservationMapping(numObsDimensions,
                        numCells, randomMaker),
                        new double[] {0, 0},
                        new double[] {width, height});
                break;
        }
    }
    
    @Override
    public String env_init() {
        return makeTaskSpec();
    }

    public String env_message(String theMessage) {
        EnvironmentMessages theMessageObject;
        try {
            theMessageObject = EnvironmentMessageParser.parseMessage(theMessage);
        } catch (NotAnRLVizMessageException e) {
            System.err.println("Someone sent mountain Car a message that wasn't RL-Viz compatible");
            return "I only respond to RL-Viz messages!";
        }

        // Map requested observations
        if (theMessageObject instanceof EnvObsForStateRequest) {
            EnvObsForStateRequest requestMessage = (EnvObsForStateRequest)theMessageObject;
            Vector<Observation> requestedStates = requestMessage.getTheRequestStates();

            Vector<Observation> theObservations = new Vector<Observation>();
            for(Observation o : requestedStates) {
                Observation thisObs;
                
                if (obsMapping != null)
                    thisObs = obsMapping.map(o);
                else
                    thisObs = o;
                
                theObservations.add(thisObs);
            }

            EnvObsForStateResponse theResponse = new EnvObsForStateResponse(theObservations);
            return theResponse.makeStringResponse();
        }
        else
            return super.env_message(theMessage);
    }

    @Override
    public Observation env_start() {
		setAgentPosition(startPos);

        return makeObservation();

    }

    private String makeTaskSpec() {
        TaskSpecVRLGLUE3 theTaskSpecObject = new TaskSpecVRLGLUE3();
        theTaskSpecObject.setEpisodic();
        theTaskSpecObject.setDiscountFactor(discountFactor);
        theTaskSpecObject.addContinuousObservation(new DoubleRange(getWorldRect().getMinX(), getWorldRect().getMaxX()));
        theTaskSpecObject.addContinuousObservation(new DoubleRange(getWorldRect().getMinY(), getWorldRect().getMaxY()));
        theTaskSpecObject.addDiscreteAction(new IntRange(0, 3));
        theTaskSpecObject.setRewardRange(new DoubleRange(-1, 1));
        theTaskSpecObject.setExtra("EnvName:DiscontinuousContinuousGridWorld");
        theTaskSpecObject.setExtra("GOALINFO:"+goalPos.x+":"+goalPos.y);
        
        String taskSpecString = theTaskSpecObject.toTaskSpec();
        TaskSpec.checkTaskSpec(taskSpecString);
        return taskSpecString;

    }

    public static TaskSpecPayload getTaskSpecPayload(ParameterHolder P) {
        DiscontinuousContinuousGridWorld theGridWorld =
                new DiscontinuousContinuousGridWorld(P);
        String taskSpec = theGridWorld.makeTaskSpec();
        return new TaskSpecPayload(taskSpec, false, "");
    }

    public String getVisualizerClassName() {
        return DiscontinuousContinuousGridWorldVisualizer.class.getName();
    }

    @Override
    public Reward_observation_terminal env_step(Action action) {
        lastAgentPos = agentPos;
        // Fudge the action a bit
        int theAction;
        if (randomActionProbability > 0 && 
                randomMaker.nextDouble() < randomActionProbability) {
            theAction = (int)(randomMaker.nextDouble() * numActions);
        }
        else
            theAction = action.intArray[0];

        double dx = 0;
        double dy = 0;

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
        double noiseX = randomMaker.nextGaussian() * movementNoise;
        double noiseY = randomMaker.nextGaussian() * movementNoise;

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

    /** Provide the agent with some reward, possibly with a potential function
     *    bonus added in
     * 
     * @return The reward for the current state
     */
    @Override
    protected double getReward() {
        return super.getReward();
    }

    @Override
    protected Observation makeObservation() {
        Observation obs = super.makeObservation();

        // if we have an observation mapping, apply it
        if (obsMapping != null)
            obs = obsMapping.map(obs);
        
        return obs;
    }
}
