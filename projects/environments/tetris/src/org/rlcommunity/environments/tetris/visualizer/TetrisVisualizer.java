/*
Copyright 2007 Brian Tanner
http://rl-library.googlecode.com/
brian@tannerpages.com
http://brian.tannerpages.com

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
package org.rlcommunity.environments.tetris.visualizer;

import rlVizLib.general.TinyGlue;
import rlVizLib.visualization.AbstractVisualizer;
import java.awt.Component;
import java.util.Vector;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.rlcommunity.environments.tetris.messages.TetrisStateRequest;
import org.rlcommunity.environments.tetris.messages.TetrisStateResponse;
import rlVizLib.visualization.interfaces.DynamicControlTarget;
import org.rlcommunity.rlglue.codec.types.Observation;
import rlVizLib.visualization.SelfUpdatingVizComponent;

public class TetrisVisualizer extends AbstractVisualizer {

    private TetrisStateResponse currentState = null;
    private TinyGlue theGlueState = null;
    private int lastUpdateTimeStep = -1;
    javax.swing.JCheckBox printGridCheckBox = null;

    public boolean printGrid() {
        if (printGridCheckBox != null) {
            return printGridCheckBox.isSelected();
        }
        return false;
    }
    DynamicControlTarget theControlTarget = null;

    public TetrisVisualizer(TinyGlue theGlueState, DynamicControlTarget theControlTarget) {
        super();

        this.theGlueState = theGlueState;
        this.theControlTarget = theControlTarget;
        SelfUpdatingVizComponent theBlockVisualizer = new TetrisBlocksComponent(this);
        SelfUpdatingVizComponent theTetrlaisScoreViz = new TetrisScoreComponent(this);

        addVizComponentAtPositionWithSize(theBlockVisualizer, 0, .1, 1.0, .9);
        addVizComponentAtPositionWithSize(theTetrlaisScoreViz, 0, 0, 1.0, 0.3);

        addDesiredExtras();
    }
//Override this if you don't want some extras (like check boxes)
    protected void addDesiredExtras() {
        addPreferenceComponents();
    }

    protected void addPreferenceComponents() {
        //Setup the slider
        printGridCheckBox = new JCheckBox();
        if (theControlTarget != null) {
            Vector<Component> newComponents = new Vector<Component>();
            JLabel tetrisPrefsLabel = new JLabel("Tetris Visualizer Preferences: ");
            JLabel printGridLabel = new JLabel("Draw Grid");
            tetrisPrefsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            printGridLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JPanel gridPrefPanel = new JPanel();
            gridPrefPanel.add(printGridLabel);
            gridPrefPanel.add(printGridCheckBox);


            newComponents.add(tetrisPrefsLabel);
            newComponents.add(gridPrefPanel);

            theControlTarget.addControls(newComponents);
        }

    }

    public void updateAgentState(boolean force) {
//        //Only do this if we're on a new time step
        int currentTimeStep = theGlueState.getTotalSteps();


        if (currentTimeStep != lastUpdateTimeStep || force) {
            currentState = TetrisStateRequest.Execute();
            lastUpdateTimeStep = currentTimeStep;
        }
    }

    public int getWidth() {
        return currentState.getWidth();
    }

    public int getHeight() {

        return currentState.getHeight();
    }

    public double getScore() {
        return theGlueState.getReturnThisEpisode();
    }

    public int[] getWorld() {
//        int[] theWorld=new int[getWidth()*getHeight()];
//        System.arraycopy(theGlueState.getLastObservation().intArray,0,theWorld,0,theWorld.length);
//        return theWorld;
        return currentState.getWorld();
    }

    public int getEpisodeNumber() {
        return theGlueState.getEpisodeNumber();
    }

    public int getTimeStep() {
        return theGlueState.getTimeStep();
    }

    public int getTotalSteps() {
        return theGlueState.getTotalSteps();
    }

    public int getCurrentPiece() {
        int[] lastIntArray=theGlueState.getLastObservation().intArray;
        int endOfBoardIndex=getWidth()*getHeight();
        
        int whichPiece=-1;
        for(int i=endOfBoardIndex;i<endOfBoardIndex+7;i++){
            if(lastIntArray[i]==1){
                if(whichPiece!=-1){
                    System.err.println("Weird, seems that there is more than 1 piece, must be a bug.");
                }
                whichPiece=i-endOfBoardIndex;
            }
        }
        if(whichPiece==-1){
            System.err.println("Could not figure out which piece it was");
        }
        return whichPiece;
        
    }

    public TinyGlue getGlueState() {
        return theGlueState;
    }

    public void drawObs(Observation tetrisObs) {
        System.out.println("STEP: " + theGlueState.getTotalSteps());
        int index = 0;
        for (int i = 0; i < currentState.getHeight(); i++) {
            for (int j = 0; j < currentState.getWidth(); j++) {
                index = i * currentState.getWidth() + j;
                System.out.print(tetrisObs.intArray[index]);
            }
            System.out.print("\n");
        }

    }

    public String getName() {
        return "Tetris 1.1 (DEV)";
    }
    int lastSaveIndex = -1;
    boolean forceBlocksRefresh = false;

    public boolean getForceBlocksRefresh() {
        return forceBlocksRefresh;
    }

    public void setForceBlocksRefresh(boolean newValue) {
        forceBlocksRefresh = newValue;
    }
}
