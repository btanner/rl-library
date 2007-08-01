package rlViz;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SpringLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import rlVizLib.general.ParameterHolder;

public class RLControlPanel extends JPanel implements ActionListener, ChangeListener {

	/**
	 * 
	 */
	
	RLGlueLogic theGlueConnection=null;
	
	private static final long serialVersionUID = 1L;
	JComboBox envListComboBox = null;
	
	JComboBox agentListComboBox = null;
	
	Vector<ParameterHolder> envParamVector=null;
	
	ParameterHolderLogic envParamLogic=null;
	
	JButton bLoad = null;
	JButton bUnLoad = null;
	JButton bStart =null; 
	JButton bStop = null;
	JButton bStep = null;
	
	JSlider sleepTimeBetweenSteps=null;
	JSlider numColsOrRowsForValueFunction=null;
	
	private void  updateEnvList(){
		envListComboBox.removeAllItems();
		Vector<String> envListVector = theGlueConnection.getEnvNameList();
		
		for (String thisEnv : envListVector) {
			envListComboBox.addItem(thisEnv);
		}
		
		envParamVector=theGlueConnection.getEnvParamList();
		
		for (ParameterHolder thisEnvParams : envParamVector) {
			System.out.println("Received a parameter holder for an env jar and it is: "+thisEnvParams.stringSerialize());
		}

		
		if(envListVector.size()==0)envListComboBox.addItem("No Envs Available");
//		String[] agentListArray = theGlueConnection.getAgentList().toArray(new String[0]);

	}

	public RLControlPanel(RLGlueLogic theGlueConnection){
		super();
		this.theGlueConnection=theGlueConnection;
		
		
		envListComboBox = new JComboBox(new String[]{"No Envs Available"});

		ParameterHolderPanel envParamPanel=new ParameterHolderPanel(new SpringLayout());
		envParamLogic=new ParameterHolderLogic(envParamPanel,theGlueConnection.getEnvNameList(),theGlueConnection.getEnvParamList());
		add(envParamPanel);
		envListComboBox.addActionListener(envParamLogic);
		
		
		agentListComboBox = new JComboBox(new String[]{"No Agents Available"});

		bLoad = new JButton("Load Experiment");
		bLoad.addActionListener(this);

		bUnLoad = new JButton("UnLoad Experiment");
		bUnLoad.addActionListener(this);

		bStart = new JButton("Start");
		bStart.addActionListener(this);

		bStop = new JButton("Stop");
		bStop.addActionListener(this);

		bStep = new JButton("Step");
		bStep.addActionListener(this);
		
		setDefaultEnabling();
		add(envListComboBox);
		add(agentListComboBox);
		add(bLoad);
		add(bUnLoad);
		add(bStart);
		add(bStop);
		add(bStep);
		
		sleepTimeBetweenSteps = new JSlider(JSlider.HORIZONTAL,
		                                      1, 500, 50);
		sleepTimeBetweenSteps.addChangeListener(this);

		numColsOrRowsForValueFunction = new JSlider(JSlider.HORIZONTAL,
                1, 100, 10);
		numColsOrRowsForValueFunction.addChangeListener(this);
	
		//Turn on labels at major tick marks.
		sleepTimeBetweenSteps.setMajorTickSpacing(100);
		sleepTimeBetweenSteps.setMinorTickSpacing(10);
		sleepTimeBetweenSteps.setPaintTicks(true);
		sleepTimeBetweenSteps.setPaintLabels(true);
		add(sleepTimeBetweenSteps);
		add(numColsOrRowsForValueFunction);

		updateEnvList();
	}


	public void actionPerformed(ActionEvent theEvent) {
		if (theEvent.getSource()==bLoad)handleLoadClick();
		if (theEvent.getSource()==bUnLoad)handleUnLoadClick();
		if (theEvent.getSource()==bStart)handleStartClick();
		if (theEvent.getSource()==bStop)handleStopClick();
		if (theEvent.getSource()==bStep)handleStepClick();

	}

	private void handleUnLoadClick() {
		envListComboBox.setEnabled(true);
		bLoad.setEnabled(true);
		bUnLoad.setEnabled(false);
		bStart.setEnabled(false);
		bStop.setEnabled(false);
		bStep.setEnabled(false);
		
	}


	private void setDefaultEnabling() {
		envListComboBox.setEnabled(true);
//For now
		agentListComboBox.setEnabled(false);
		bLoad.setEnabled(true);
		bUnLoad.setEnabled(false);
		bStart.setEnabled(false);
		bStop.setEnabled(false);
		bStep.setEnabled(false);
	}

	private void handleLoadClick(){
		envListComboBox.setEnabled(false);

		bLoad.setEnabled(false);
		bUnLoad.setEnabled(true);
		bStart.setEnabled(true);
		bStop.setEnabled(false);
		bStep.setEnabled(true);
		
		String envName=envParamLogic.getCurrentEnvironmentName();
		ParameterHolder currentParams=envParamLogic.getCurrentParameterValues();
		theGlueConnection.loadEnvironment(envName,currentParams);
	}
	private void handleStartClick(){
		bLoad.setEnabled(false);
		bUnLoad.setEnabled(false);
		bStart.setEnabled(false);
		bStop.setEnabled(true);
		bStep.setEnabled(false);
		
		int stepDelay=(int)sleepTimeBetweenSteps.getValue();
		theGlueConnection.start(stepDelay);
	}

	private void handleStepClick() {
		envListComboBox.setEnabled(false);
		bLoad.setEnabled(false);
		bUnLoad.setEnabled(true);
		bStart.setEnabled(true);
		bStop.setEnabled(false);
		bStep.setEnabled(true);
		theGlueConnection.step();
	}

	private void handleStopClick() {
		envListComboBox.setEnabled(false);
		bLoad.setEnabled(false);
		bUnLoad.setEnabled(true);

		bStart.setEnabled(true);
		bStop.setEnabled(false);
		bStep.setEnabled(true);		
		theGlueConnection.stop();

	}

	public void stateChanged(ChangeEvent sliderChangeEvent) {
		JSlider source = (JSlider)sliderChangeEvent.getSource();
		int theValue = (int)source.getValue();
		
		if(source==numColsOrRowsForValueFunction)theGlueConnection.setNewValueFunctionResolution(theValue);
		if(source==sleepTimeBetweenSteps)theGlueConnection.setNewStepDelay(theValue);
	}
}
