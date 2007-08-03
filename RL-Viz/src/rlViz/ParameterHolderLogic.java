package rlViz;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JComboBox;

import rlVizLib.general.ParameterHolder;

public class ParameterHolderLogic implements ActionListener {
ParameterHolderPanel thePanel=null;

Vector<String> theNames=null;
Vector<ParameterHolder> theParams=null;


int currentLoadedIndex=-1;

public ParameterHolderLogic(ParameterHolderPanel thePanel, Vector<String> theNames, Vector<ParameterHolder> theParams){
	this.thePanel=thePanel;
	this.theNames=theNames;
	this.theParams=theParams;
}

public void updateParamsFromPanel(){
	if(currentLoadedIndex!=-1){
		ParameterHolder latestP=thePanel.updateParamHolderFromPanel();
		theParams.set(currentLoadedIndex,latestP);
	}
}
public ParameterHolder getCurrentParameterValues(){
	updateParamsFromPanel();
	return theParams.get(currentLoadedIndex);
}

public void setEnabled(boolean b){
	thePanel.setEnabled(b);
}

public String getCurrentEnvironmentName(){
	return theNames.get(currentLoadedIndex);
}

	public void actionPerformed(ActionEvent e) {
		JComboBox cb = (JComboBox)e.getSource();
        int whichIndex = cb.getSelectedIndex();

        updateParamsFromPanel();
        
        if(whichIndex>-1){
        	thePanel.switchParameters(theParams.get(whichIndex));
        	currentLoadedIndex=whichIndex;
        }
	}

}
