package rlViz;

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;




/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
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
