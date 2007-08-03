package rlViz;

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;



public class RLVizFrame extends JFrame{
	private static final long serialVersionUID = 1L;

	//Components
	VisualizerPanel ePanel=null;
	VisualizerPanel aPanel=null;

	RLGlueLogic theGlueConnection=null;
	public RLVizFrame(){
		super();
		
		theGlueConnection=RLGlueLogic.getGlobalGlueLogic();
		setLayout(new BoxLayout(getContentPane(),BoxLayout.X_AXIS));

		
		ePanel= new VisualizerPanel(new Dimension(600,600));
		ePanel.setPreferredSize(new Dimension(600,600));
		
		if (ePanel instanceof visualizerLoadListener) {
			RLGlueLogic.getGlobalGlueLogic().addEnvVisualizerLoadListener((visualizerLoadListener) ePanel);
		}
//		EnvironmentPanel aPanel= new EnvironmentPanel(new Dimension(200,200),theAVisualizer);
//		
		JPanel controlPanel=new RLControlPanel(theGlueConnection);
//		controlPanel.setSize(300, this.getHeight());
		
//		JSplitPane agentEnvSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,ePanel,new JLabel("Agent viz goes here"));
//		agentEnvSplitPane.setOneTouchExpandable(true);
//		agentEnvSplitPane.setDividerLocation(150);

//		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, ePanel, controlPanel);
//		splitPane.setDividerLocation(500);

		getContentPane().add(ePanel);
		getContentPane().add(controlPanel);
//		getContentPane().add(splitPane);
		setSize(800,600);
		setVisible(true);
	
	}

}
