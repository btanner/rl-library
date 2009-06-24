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
package org.rlcommunity.environments.continuousgridworld.visualizer;

import java.util.Vector;
import org.rlcommunity.environments.continuousgridworld.State;
import org.rlcommunity.environments.continuousgridworld.messages.StateRequest;
import org.rlcommunity.environments.continuousgridworld.messages.StateResponse;
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
import org.rlcommunity.rlglue.codec.types.Observation;

import rlVizLib.messaging.NotAnRLVizMessageException;
import rlVizLib.visualization.SelfUpdatingVizComponent;
import rlVizLib.visualization.interfaces.DynamicControlTarget;

public class ContinuousGridWorldVisualizer extends AbstractVisualizer implements GridWorldVisualizerInterface {

    private Vector<Double> mins = null;
    private Vector<Double> maxs = null;
    private StateResponse theStateMessage = null;
    private AgentValueForObsResponse theValueResponse = null;
    private Vector<Double> theQueryPositions = null;
    private TinyGlue theGlueState = null;
    private int lastAgentValueUpdateTimeStep = -1;
    private DynamicControlTarget theControlTarget = null;
    private ValueFunctionVizComponent theValueFunction = null;
    private AgentOnValueFunctionVizComponent theAgentOnValueFunction = null;

    public ContinuousGridWorldVisualizer(TinyGlue glueState, DynamicControlTarget theControlTarget) {
        super();
        this.theControlTarget = theControlTarget;

        this.theGlueState = glueState;

        theValueFunction = new ValueFunctionVizComponent(this, theControlTarget, glueState);
        theAgentOnValueFunction = new AgentOnValueFunctionVizComponent(this, glueState);
        SelfUpdatingVizComponent theMapComponent = new GridWorldMapComponent(this);
        SelfUpdatingVizComponent scoreComponent = new GenericScoreComponent(this);


        super.addVizComponentAtPositionWithSize(theValueFunction, 0, 0, 1.0, 1.0);
        super.addVizComponentAtPositionWithSize(theMapComponent, 0, 0, 1.0, 1.0);
        super.addVizComponentAtPositionWithSize(theAgentOnValueFunction, 0, 0, 1.0, 1.0);
//
        super.addVizComponentAtPositionWithSize(scoreComponent, 0, 0, 1.0, 1.0);
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
            try {
                theValueResponse = AgentValueForObsRequest.Execute(theQueryObs);
            } catch (NotAnRLVizMessageException e) {
                theValueFunction.setEnabled(false);
            }
            lastAgentValueUpdateTimeStep = currentTimeStep;
        }

        if (theValueResponse == null) {
            if (!printedQueryError) {
                printedQueryError = true;
                System.err.println("In the ContinuousGridWorld Visualizer: Asked an Agent for Values and didn't get back a parseable message.  I'm not printing this again.");
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
        checkCoreData();
        assert theStateMessage!=null : "For some reason theStateMessage is null.";
        assert theStateMessage.getState()!=null : "For some reason theStateMessage.getState() is null.";
        assert theStateMessage.getState().getAgentPosition()!=null : "For some reason theStateMessage.getState().getAgent() is null.";

        if (whichDimension == 0) {
            return theStateMessage.getState().getAgentPosition().getX();
        } else {
            return theStateMessage.getState().getAgentPosition().getY();
        }
    }

    public void updateAgentState() {
        theStateMessage = StateRequest.Execute();
    }

    private void checkCoreData() {
        if (theStateMessage == null) {
            theStateMessage = StateRequest.Execute();
        }
    }

    public State getState() {
        checkCoreData();
        return theStateMessage.getState();
    }


    public TinyGlue getTheGlueState() {
        return theGlueState;
    }
}