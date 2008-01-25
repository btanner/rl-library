/* Tetris Domain
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
package org.rlcommunity.tetris.visualizer;

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
import org.rlcommunity.tetris.messages.TetrisStateRequest;
import org.rlcommunity.tetris.messages.TetrisStateResponse;
import rlVizLib.glueProxy.RLGlueProxy;
import rlVizLib.visualization.interfaces.DynamicControlTarget;
import rlglue.types.Observation;
import rlglue.types.State_key;

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

            RLGlueProxy.RL_set_state(k);
            setForceBlocksRefresh(true);
        }
        if(event.getSource()==saveButton){
            loadButton.setEnabled(true);
            State_key k =RLGlueProxy.RL_get_state();
            lastSaveIndex=k.intArray[0];
        }
    }


	
}
