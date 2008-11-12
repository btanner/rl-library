package org.rlcommunity.environments.continuousratworld.visualizer;

import org.rlcommunity.awhite.continuousratworld.messages.CRWMapRequest;
import org.rlcommunity.awhite.continuousratworld.messages.CRWMapResponse;
import java.awt.geom.Rectangle2D;
import java.util.Vector;

import org.rlcommunity.awhite.visualizers.tools.humanAgentInterface.HumanAgentInterface;
import rlVizLib.general.TinyGlue;
import rlVizLib.messaging.agent.AgentValueForObsRequest;
import rlVizLib.messaging.agent.AgentValueForObsResponse;
import rlVizLib.messaging.environment.EnvObsForStateRequest;
import rlVizLib.messaging.environment.EnvObsForStateResponse;
import rlVizLib.messaging.environment.EnvRangeRequest;
import rlVizLib.messaging.environment.EnvRangeResponse;
import rlVizLib.visualization.AbstractVisualizer;
import rlVizLib.visualization.AgentOnValueFunctionVizComponent;
import rlVizLib.visualization.GenericScoreComponent;
import rlVizLib.visualization.ValueFunctionVizComponent;
import rlVizLib.visualization.interfaces.AgentOnValueFunctionDataProvider;
import rlVizLib.visualization.interfaces.ValueFunctionDataProvider;
import org.rlcommunity.rlglue.codec.types.Observation;

import rlVizLib.visualization.PollingVizComponent;
import rlVizLib.visualization.SelfUpdatingVizComponent;
import rlVizLib.visualization.interfaces.DynamicControlTarget;
import rlVizLib.visualization.interfaces.GlueStateProvider;

public class ContinuousRatWorldVisualizer extends AbstractVisualizer implements ValueFunctionDataProvider, AgentOnValueFunctionDataProvider, GlueStateProvider {

    Vector<Double> mins = null;
    Vector<Double> maxs = null;

    //Keep these responses around so we don't have to ask twice
    CRWMapResponse theMapResponse = null;
    AgentValueForObsResponse theValueResponse = null;
    Vector<Double> theQueryPositions = null;
    TinyGlue theGlueState = null;
    private int lastAgentValueUpdateTimeStep = -1;
    DynamicControlTarget theControlTarget=null;
    HumanAgentInterface HAI = new HumanAgentInterface();

    public ContinuousRatWorldVisualizer(TinyGlue glueState, DynamicControlTarget theControlTarget) {
        super();
        this.theControlTarget=theControlTarget;

        this.theGlueState = glueState;

        SelfUpdatingVizComponent theValueFunction = new ValueFunctionVizComponent(this,theControlTarget,glueState);
        SelfUpdatingVizComponent agentOnVF = new AgentOnValueFunctionVizComponent(this,glueState);
        SelfUpdatingVizComponent theMapComponent = new RatWorldMapComponent(this);

        SelfUpdatingVizComponent scoreComponent = new GenericScoreComponent(this);


        super.addVizComponentAtPositionWithSize(theValueFunction, 0, 0, 1.0, 1.0);
        super.addVizComponentAtPositionWithSize(theMapComponent, 0, 0, 1.0, 1.0);
        super.addVizComponentAtPositionWithSize(agentOnVF, 0, 0, 1.0, 1.0);

        super.addVizComponentAtPositionWithSize(scoreComponent, 0, 0, 1.0, 1.0);
        HAI.addComponents(theControlTarget);
        
    }


    public void updateEnvironmentVariableRanges() {
        //Get the Ranges (internalize this)
        EnvRangeResponse theERResponse = EnvRangeRequest.Execute();

        if (theERResponse == null) {
            System.err.println("Asked an Environment for Variable Ranges and didn't get back a parseable message.");
            Thread.dumpStack();
            System.exit(1);
        }

        mins = theERResponse.getMins();
        maxs = theERResponse.getMaxs();
    }

    public double getMaxValueForDim(int whichDimension) {
        if (maxs == null) {
            updateEnvironmentVariableRanges();
        }
        return maxs.get(whichDimension);
    }

    public double getMinValueForDim(int whichDimension) {
        if (mins == null) {
            updateEnvironmentVariableRanges();
        }
        return mins.get(whichDimension);
    }

    public Vector<Observation> getQueryObservations(Vector<Observation> theQueryStates) {
        EnvObsForStateResponse theObsForStateResponse = EnvObsForStateRequest.Execute(theQueryStates);

        if (theObsForStateResponse == null) {
            System.err.println("Asked an Environment for Query Observations and didn't get back a parseable message.");
            Thread.dumpStack();
            System.exit(1);
        }
        return theObsForStateResponse.getTheObservations();
    }
    boolean printedQueryError = false;

    public Vector<Double> queryAgentValues(Vector<Observation> theQueryObs) {
      int currentTimeStep = theGlueState.getTotalSteps();

        boolean needsUpdate = false;
        if (currentTimeStep != lastAgentValueUpdateTimeStep) {
            needsUpdate = true;
        }
        if (theValueResponse == null) {
            needsUpdate = true;
        } else if (theValueResponse.getTheValues().size() != theQueryObs.size()) {
            needsUpdate = true;
        }
        if (needsUpdate) {
            theValueResponse = AgentValueForObsRequest.Execute(theQueryObs);
            lastAgentValueUpdateTimeStep = currentTimeStep;
        }

        if (theValueResponse == null) {
            if (!printedQueryError) {
                printedQueryError = true;
                System.err.println("In the ContinuousRatWorld Visualizer: Asked an Agent for Values and didn't get back a parseable message.  I'm not printing this again.");
            }
            //Return NULL and make sure that gets handled
            return null;
        }

        return theValueResponse.getTheValues();
    }

    public double getCurrentStateInDimension(int whichDimension) {
        /*
         * This is only allowed access to the state Variables which are defined
         * in the Task Spec as being State Variables. The implicitly defined values,
         * like the height, should not be accessed through here
         */
        if (theGlueState.getLastObservation() == null) {
            return 0;
        }
        return theGlueState.getLastObservation().doubleArray[whichDimension];
    }

    public void updateAgentState() {
        //Don't need to do anything because its rat world, we can get the state from the variables
    }

    public Vector<Rectangle2D> getResetRegions() {
        checkMapResponse();
        return theMapResponse.getResetRegions();
    }

    public Vector<Rectangle2D> getRewardRegions() {
        checkMapResponse();
        return theMapResponse.getRewardRegions();
    }

    public Vector<Rectangle2D> getBarrierRegions() {
        checkMapResponse();
        return theMapResponse.getBarrierRegions();
    }

    public Vector<Double> getPenalties() {
        checkMapResponse();
        return theMapResponse.getThePenalties();
    }

    public Vector<Double> getTheRewards() {
        checkMapResponse();
        return theMapResponse.getTheRewards();
    }

    public Rectangle2D getWorldRect() {
        checkMapResponse();
        return theMapResponse.getTheWorldRect();
    }

    private void checkMapResponse() {
        if (theMapResponse == null) {
            theMapResponse = CRWMapRequest.Execute();
        }
    }

    public TinyGlue getTheGlueState() {
        return theGlueState;
    }
}