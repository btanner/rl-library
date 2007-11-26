/* RL-Viz Visualizer for Mountain Car Domain
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
package visualizers.mountainCar;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JLabel;
import mcMessages.MCGoalRequest;
import mcMessages.MCGoalResponse;
import mcMessages.MCHeightRequest;
import mcMessages.MCHeightResponse;
import mcMessages.MCStateRequest;
import rlVizLib.general.TinyGlue;
import rlVizLib.glueProxy.RLGlueProxy;
import rlVizLib.visualization.interfaces.AgentOnValueFunctionDataProvider;
import rlVizLib.visualization.interfaces.ValueFunctionDataProvider;
import rlVizLib.visualization.interfaces.GlueStateProvider;
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
import rlVizLib.visualization.VizComponent;
import rlVizLib.visualization.interfaces.DynamicControlTarget;
import rlglue.types.Observation;
import rlglue.types.State_key;

public class MountainCarVisualizer extends AbstractVisualizer implements ValueFunctionDataProvider, AgentOnValueFunctionDataProvider, GlueStateProvider, ActionListener {

    Vector<Double> mins = null;
    Vector<Double> maxs = null;
    mcMessages.MCStateResponse theCurrentState = null;
    Vector<Double> theQueryPositions = null;
    Vector<Double> theHeights = null;
    double minHeight = Double.MIN_VALUE;
    double maxHeight = Double.MAX_VALUE;
    double goalPosition = 0.5;
    private int lastStateUpdateTimeStep = -1;
    private int lastAgentValueUpdateTimeStep = -1;
    private boolean printedQueryError = false;
    //Will have to find a way to easily generalize this and move it to vizlib

    TinyGlue glueState = null;
    //This is a little interface that will let us dump controls to a panel somewhere.

    DynamicControlTarget theControlTarget = null;
    javax.swing.JButton saveButton = null;
    javax.swing.JButton loadButton = null;

    public MountainCarVisualizer(TinyGlue glueState, DynamicControlTarget theControlTarget) {
        super();

        this.glueState = glueState;
        this.theControlTarget = theControlTarget;
        setupVizComponents();
        addDesiredExtras();


    }

    protected void setupVizComponents() {
        VizComponent theValueFunction = new ValueFunctionVizComponent(this, theControlTarget);
        VizComponent agentOnVF = new AgentOnValueFunctionVizComponent(this);
        VizComponent mountain = new MountainVizComponent(this);
        VizComponent carOnMountain = new CarOnMountainVizComponent(this);
        VizComponent scoreComponent = new GenericScoreComponent(this);

                super.addVizComponentAtPositionWithSize(theValueFunction, 0, .5, 1.0, .5);
        super.addVizComponentAtPositionWithSize(agentOnVF, 0, .5, 1.0, .5);

        super.addVizComponentAtPositionWithSize(mountain, 0, 0, 1.0, .5);
        super.addVizComponentAtPositionWithSize(carOnMountain, 0, 0, 1.0, .5);
        super.addVizComponentAtPositionWithSize(scoreComponent, 0, 0, 1.0, 1.0);

    }

    protected void addDesiredExtras() {
        addPreferenceComponents();
    }

    public void addPreferenceComponents() {
        saveButton = new JButton();
        saveButton.setText("Save");
        saveButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        loadButton = new JButton();
        loadButton.setText("Load");
        loadButton.setEnabled(false);
        loadButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        saveButton.addActionListener(this);
        loadButton.addActionListener(this);
        if (theControlTarget != null) {
            Vector<Component> newComponents = new Vector<Component>();
            JLabel MCControlsLabel = new JLabel("MountainCar Visualizer Controls: ");
            MCControlsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            newComponents.add(MCControlsLabel);
            newComponents.add(saveButton);
            newComponents.add(loadButton);
            theControlTarget.addControls(newComponents);
        }
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
    AgentValueForObsResponse theValueResponse = null;

    public Vector<Double> queryAgentValues(Vector<Observation> theQueryObs) {
        int currentTimeStep = glueState.getTotalSteps();

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
                System.err.println("In the Mountain Car Visualizer: Asked an Agent for Values and didn't get back a parseable message.  I'm not printing this again.");
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
        if (theCurrentState == null) {
            return 0;
        }
        if (whichDimension == 0) {
            return theCurrentState.getPosition();
        } else {
            return theCurrentState.getVelocity();
        }
    }

    public double getHeight() {
        return theCurrentState.getHeight();
    }

    public double getMaxHeight() {
        if (theQueryPositions == null) {
            initializeHeights();
        }
        return minHeight;
    }

    public double getMinHeight() {
        if (theQueryPositions == null) {
            initializeHeights();
        }
        return maxHeight;
    }

    public Vector<Double> getSampleHeights() {
        if (theHeights == null) {
            initializeHeights();
        }
        return theHeights;
    }

    public Vector<Double> getSamplePositions() {
        if (theQueryPositions == null) {
            initializeHeights();
        }
        return theQueryPositions;
    }

    public double getDeltaHeight() {
        return theCurrentState.getDeltaheight();
    }

    public void updateAgentState(boolean force) {
        //Only do this if we're on a new time step
        int currentTimeStep = glueState.getTotalSteps();

        if (currentTimeStep != lastStateUpdateTimeStep || force) {
            theCurrentState = MCStateRequest.Execute();
            lastStateUpdateTimeStep = currentTimeStep;
        }
    }

    public Vector<Double> getHeightsForPositions(Vector<Double> theQueryPositions) {
        MCHeightResponse heightResponse = MCHeightRequest.Execute(theQueryPositions);
        return heightResponse.getHeights();
    }

    public double getGoalPosition() {
        MCGoalResponse goalResponse = MCGoalRequest.Execute();
        return goalResponse.getGoalPosition();
    }

    public void initializeHeights() {
        //Because we can change the shape of the curve we have no guarantees what
        // the max and min heights of the mountains may turn out to be...
        // this takes a quick sample based approach to find out what is a good approximation
        //for the min and the max.
        double minPosition = getMinValueForDim(0);
        double maxPosition = getMaxValueForDim(0);

        int pointsToDraw = 500;
        double theRangeSize = maxPosition - minPosition;
        double pointIncrement = theRangeSize / (double) pointsToDraw;

        theQueryPositions = new Vector<Double>();
        for (double i = minPosition; i < maxPosition; i += pointIncrement) {
            theQueryPositions.add(i);
        }
        theHeights = this.getHeightsForPositions(theQueryPositions);

        maxHeight = Double.MIN_VALUE;
        minHeight = Double.MAX_VALUE;
        for (Double thisHeight : theHeights) {
            if (thisHeight > maxHeight) {
                maxHeight = thisHeight;
            }
            if (thisHeight < minHeight) {
                minHeight = thisHeight;
            }
        }
    }

    public TinyGlue getTheGlueState() {
        return glueState;
    }

    public String getName() {
        return "Mountain Car 1.01 (DEV)";
    }
    int lastSaveIndex = -1;
    boolean forceDrawRefresh = false;

    public boolean getForceDrawRefresh() {
        return forceDrawRefresh;
    }

    public void setForceDrawRefresh(boolean newValue) {
        forceDrawRefresh = newValue;
    }

    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == loadButton) {
            State_key k = new State_key(1, 0);
            k.intArray[0] = lastSaveIndex;

            RLGlueProxy.RL_set_state(k);
            setForceDrawRefresh(true);
        }
        if (event.getSource() == saveButton) {
            loadButton.setEnabled(true);
            State_key k = RLGlueProxy.RL_get_state();
            lastSaveIndex = k.intArray[0];
        }
    }

    //This is the one required from RLVizLib, ours has a forcing parameter.  Should update the VizLib

    public void updateAgentState() {
        updateAgentState(false);
    }
}