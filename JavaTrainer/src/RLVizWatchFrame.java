

import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.WindowConstants;

import rlViz.EnvVisualizerFactory;
import rlViz.VisualizerPanel;
import rlViz.visualizerLoadListener;
import rlVizLib.visualization.AbstractVisualizer;



public class RLVizWatchFrame extends JFrame{
	private static final long serialVersionUID = 1L;

	//Components
	VisualizerPanel ePanel=null;

	public RLVizWatchFrame(){
		this("","");
	}
	public RLVizWatchFrame(String envName, String agentName){
		super();
		//This very well might return null
		this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
//		theGlueConnection=RLGlueLogic.getGlobalGlueLogic();

		ePanel= new VisualizerPanel(new Dimension(600,600));
		
		AbstractVisualizer defaultEnvVisualizer = EnvVisualizerFactory.createVisualizerFromString(envName);

		if(ePanel instanceof visualizerLoadListener){
			((visualizerLoadListener)ePanel).notifyVisualizerLoaded(defaultEnvVisualizer);
		}
//		EnvironmentPanel aPanel= new EnvironmentPanel(new Dimension(200,200),theAVisualizer);
//		
//		JPanel controlPanel=new RLControlPanel(theGlueConnection);
		
		getContentPane().add(ePanel);
//		JSplitPane agentEnvSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,ePanel,new JLabel("Agent viz goes here"));
//		agentEnvSplitPane.setOneTouchExpandable(true);
//		agentEnvSplitPane.setDividerLocation(150);
//
//		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, agentEnvSplitPane, controlPanel);
//		splitPane.setOneTouchExpandable(true);
//		splitPane.setDividerLocation(150);


//		getContentPane().add(splitPane);
		setSize(800,600);

	}


	public void startVisualizing() {
		ePanel.updateUI();
		setVisible(true);
		ePanel.startVisualizing();
	}
	
	public void stopVisualizing(){
		ePanel.stopVisualizing();
		this.setVisible(false);
	}


}
