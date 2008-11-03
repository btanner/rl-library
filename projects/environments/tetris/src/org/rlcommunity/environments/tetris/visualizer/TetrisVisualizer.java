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

import java.awt.event.ActionEvent;
import rlVizLib.general.TinyGlue;
import rlVizLib.visualization.AbstractVisualizer;
import rlVizLib.visualization.VizComponent;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.rlcommunity.environments.tetris.messages.TetrisStateRequest;
import org.rlcommunity.environments.tetris.messages.TetrisStateResponse;
import org.rlcommunity.rlglue.codec.RLGlue;
import rlVizLib.visualization.interfaces.DynamicControlTarget;
import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.types.State_key;

public class TetrisVisualizer  extends AbstractVisualizer implements ActionListener{

	private TetrisStateResponse currentState = null;

	private TinyGlue theGlueState=null;

	private int lastUpdateTimeStep=-1;

        javax.swing.JCheckBox printGridCheckBox=null;
        javax.swing.JButton saveButton=null;
        javax.swing.JButton loadButton=null;

        public boolean printGrid(){
            if(printGridCheckBox!=null)
                return  printGridCheckBox.isSelected();
            return false;
        }

DynamicControlTarget theControlTarget=null;

	public TetrisVisualizer(TinyGlue theGlueState, DynamicControlTarget theControlTarget){
		super();
                 
                this.theGlueState=theGlueState;
		this.theControlTarget=theControlTarget;
                VizComponent theBlockVisualizer= new TetrisBlocksComponent(this);
		VizComponent theTetrlaisScoreViz = new TetrisScoreComponent(this);

		addVizComponentAtPositionWithSize(theBlockVisualizer,0,.1,1.0,.9);
		addVizComponentAtPositionWithSize(theTetrlaisScoreViz, 0,0,1.0,0.3);

                addDesiredExtras();
	}
//Override this if you don't want some extras (like check boxes)
protected void addDesiredExtras(){
addPreferenceComponents();
}

protected void addPreferenceComponents(){
        //Setup the slider
        printGridCheckBox = new JCheckBox();
        saveButton=new JButton();
        saveButton.setText("Save");
        loadButton=new JButton();
        loadButton.setText("Load");
        loadButton.setEnabled(false);
        saveButton.addActionListener(this);
        loadButton.addActionListener(this);
        
        loadButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        saveButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        if (theControlTarget != null) {
            Vector<Component> newComponents = new Vector<Component>();
            JLabel tetrisPrefsLabel = new JLabel("Tetris Visualizer Preferences: ");
            JLabel printGridLabel = new JLabel("Draw Grid");
            tetrisPrefsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            printGridLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JPanel gridPrefPanel=new JPanel();
            gridPrefPanel.add(printGridLabel);
            gridPrefPanel.add(printGridCheckBox);


            newComponents.add(tetrisPrefsLabel);
            newComponents.add(saveButton);
            newComponents.add(loadButton);
            newComponents.add(gridPrefPanel);

            theControlTarget.addControls(newComponents);
        }

}

	public void updateAgentState(boolean force) {
		//Only do this if we're on a new time step
		int currentTimeStep=theGlueState.getTotalSteps();

                
                if(currentTimeStep!=lastUpdateTimeStep||force){
			currentState=TetrisStateRequest.Execute();
			lastUpdateTimeStep=currentTimeStep;
		}
	}

	public int getWidth(){
		return currentState.getWidth();
	}

	public int getHeight(){

		return currentState.getHeight();
	}

	public int getScore(){
		return currentState.getScore();
	}

	public int [] getWorld(){
		return currentState.getWorld();
	}
	
	public int getEpisodeNumber(){
		return theGlueState.getEpisodeNumber();
	}
	public int getTimeStep(){
		return theGlueState.getTimeStep();
	}

	public int getTotalSteps() {
		return theGlueState.getTotalSteps();
	}
	
	public int getCurrentPiece(){
		return currentState.getCurrentPiece();
	}

	public TinyGlue getGlueState() {
		return theGlueState;
	}
        public void drawObs(Observation tetrisObs){
            System.out.println("STEP: " + theGlueState.getTotalSteps());
            int index = 0;
            for(int i=0;i<currentState.getHeight(); i++){
                for(int j=0;j<currentState.getWidth(); j++){
                    index = i*currentState.getWidth() + j;
                    System.out.print(tetrisObs.intArray[index]);
                }
                System.out.print("\n");
            }
            
        }
        
   public String getName(){
        return "Tetris 1.0 (DEV)";
    }

    int lastSaveIndex=-1;
    boolean forceBlocksRefresh=false;
    
    public boolean getForceBlocksRefresh(){
        return forceBlocksRefresh;
    }
    public void setForceBlocksRefresh(boolean newValue){
        forceBlocksRefresh=newValue;
    }
   
    public void actionPerformed(ActionEvent event) {
        if(event.getSource()==loadButton){
            State_key k=new State_key(1,0);
            k.intArray[0]=lastSaveIndex;

            RLGlue.RL_load_state(k);
            setForceBlocksRefresh(true);
        }
        if(event.getSource()==saveButton){
            loadButton.setEnabled(true);
            State_key k =RLGlue.RL_save_state();
            lastSaveIndex=k.intArray[0];
        }
    }


	
}
