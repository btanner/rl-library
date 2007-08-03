package rlViz;

import java.awt.Component;
import java.awt.LayoutManager;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import rlVizLib.general.ParameterHolder;

public class ParameterHolderPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Vector<Component> allComponents=new Vector<Component>();
	Map<String, Component> nameToValueMap= new TreeMap<String, Component>();

	public ParameterHolderPanel(LayoutManager Layout){
		super(Layout);
	}


	public void setEnabled(boolean shouldEnable){
		for (Component thisComponent : allComponents)thisComponent.setEnabled(shouldEnable);
	}
	
	ParameterHolder currentParamHolder=null;

	public void switchParameters(ParameterHolder p) {
		this.currentParamHolder=p;
		renderCurrentParameterHolder();
	}

	public ParameterHolder updateParamHolderFromPanel(){
		for(int i=0;i<currentParamHolder.getParamCount();i++){
			int thisParamType=currentParamHolder.getParamType(i);
			String thisParamName=currentParamHolder.getParamName(i);

			Component theRelatedComponent=nameToValueMap.get(thisParamName);

			switch (thisParamType) {
			case ParameterHolder.boolParam:
				JCheckBox boolField=(JCheckBox)theRelatedComponent;
				currentParamHolder.setBooleanParam(thisParamName, boolField.isSelected());
				break;

			case ParameterHolder.intParam:
				JTextField intField=(JTextField)theRelatedComponent;
				currentParamHolder.setIntParam(thisParamName, Integer.parseInt(intField.getText()));
				break;

			case ParameterHolder.doubleParam:
				JTextField doubleField=(JTextField)theRelatedComponent;
				currentParamHolder.setDoubleParam(thisParamName, Double.parseDouble(doubleField.getText()));
				break;
			case ParameterHolder.stringParam:
				JTextField stringField=(JTextField)theRelatedComponent;
				currentParamHolder.setStringParam(thisParamName,stringField.getText());
				break;
			}

		}
		return currentParamHolder;

	}
	public void renderCurrentParameterHolder(){
		//Should actually save the state of all these guys, people would probably like that
		for (Component thisComponent : allComponents)this.remove(thisComponent);
		allComponents.removeAllElements();

		for(int i=0;i<currentParamHolder.getParamCount();i++){
			int thisParamType=currentParamHolder.getParamType(i);
			String thisParamName=currentParamHolder.getParamName(i);

			Component newField=null;
			switch (thisParamType) {
			case ParameterHolder.boolParam:
				newField=addBoolParameter(thisParamName, currentParamHolder.getBooleanParam(thisParamName));
				break;
			case ParameterHolder.intParam:
				newField=addIntParameter(thisParamName, currentParamHolder.getIntParam(thisParamName));
				break;
			case ParameterHolder.doubleParam:
				newField=addDoubleParameter(thisParamName, currentParamHolder.getDoubleParam(thisParamName));
				break;
			case ParameterHolder.stringParam:
				newField=addStringParameter(thisParamName, currentParamHolder.getStringParam(thisParamName));
				break;
			}
			nameToValueMap.put(thisParamName, newField);

		}
//		//Lay out the panel.
		int numPairs=currentParamHolder.getParamCount();
		SpringUtilities.makeCompactGrid(this,
				numPairs, 2, //rows, cols
				6, 6,        //initX, initY
				6, 6);       //xPad, yPad



//		Not sure if this is the right thing to do
		this.updateUI();

	}

	private Component addIntParameter(String thisParamName, int theParam) {
		JLabel thisLabel=new JLabel(thisParamName+":", JLabel.TRAILING);
		JTextField thisField=new JTextField(10);
		thisLabel.setLabelFor(thisField);
		thisField.setText(""+theParam);

		allComponents.add(thisLabel);
		allComponents.add(thisField);

		add(thisLabel);
		add(thisField);

		return thisField;
	}

	private Component addDoubleParameter(String thisParamName, double theParam) {
		JLabel thisLabel=new JLabel(thisParamName+":", JLabel.TRAILING);
		JTextField thisField=new JTextField(10);
		thisLabel.setLabelFor(thisField);
		thisField.setText(""+theParam);

		allComponents.add(thisLabel);
		allComponents.add(thisField);

		add(thisLabel);
		add(thisField);

		return thisField;
	}

	private Component addStringParameter(String thisParamName, String theParam) {
		JLabel thisLabel=new JLabel(thisParamName+":", JLabel.TRAILING);
		JTextField thisField=new JTextField(10);
		thisLabel.setLabelFor(thisField);
		thisField.setText(""+theParam);

		allComponents.add(thisLabel);
		allComponents.add(thisField);

		add(thisLabel);
		add(thisField);

		return thisField;
	}


	private Component addBoolParameter(String thisParamName, boolean currentValue) {
		JLabel thisLabel=new JLabel(thisParamName+":", JLabel.TRAILING);
		JCheckBox thisField=new JCheckBox();
		thisField.setSelected(currentValue);
		thisLabel.setLabelFor(thisField);

		allComponents.add(thisLabel);
		allComponents.add(thisField);

		add(thisLabel);
		add(thisField);

		return thisField;
	}

}
