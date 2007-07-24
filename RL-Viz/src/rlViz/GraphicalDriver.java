package rlViz;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.IOException;

import javax.swing.JLabel;
import javax.swing.JSplitPane;

import rlglue.RLGlue;



public class GraphicalDriver {





	public static void main(String [] args) throws IOException {


		RLVizFrame theFrame=new RLVizFrame();

		
		MountainCarVisualizer theEVisualizer=new MountainCarVisualizer();
//		MountainCarVisualizer theAVisualizer=new MountainCarVisualizer();
		EnvironmentPanel ePanel= new EnvironmentPanel(new Dimension(200,200),theEVisualizer);
//		EnvironmentPanel aPanel= new EnvironmentPanel(new Dimension(200,200),theAVisualizer);
//		
		JSplitPane agentEnvSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,ePanel,new JLabel("Agent viz goes here"));
		agentEnvSplitPane.setOneTouchExpandable(true);
		agentEnvSplitPane.setDividerLocation(150);

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				agentEnvSplitPane, new JLabel("Controls go here"));
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(150);


		theFrame.getContentPane().add(splitPane);
		theFrame.setSize(800,600);
		theFrame.setVisible(true);

		RLGlue.RL_init();

		for(int x = 0; x < 50000; ++x) {
			RLGlue.RL_start();
			while(RLGlue.RL_step().terminal==0){
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		RLGlue.RL_cleanup();


	}
}
