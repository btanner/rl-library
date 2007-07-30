package rlViz;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.IOException;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;


import rlVizLib.messaging.environmentShell.EnvShellListRequest;
import rlVizLib.messaging.environmentShell.EnvShellListResponse;
import rlVizLib.messaging.environmentShell.EnvShellLoadRequest;
import rlglue.RLGlue;



public class GraphicalDriver {




	public static void main(String [] args) throws IOException {
		

	

		RLVizFrame theFrame=new RLVizFrame();

		
		MountainCarVisualizer theEVisualizer=new MountainCarVisualizer();
		theFrame.setEnvVisualizer(theEVisualizer);
//		MountainCarVisualizer theAVisualizer=new MountainCarVisualizer();

//		RLGlue.RL_init();
//
//		for(int x = 0; x < 50000; ++x) {
//			RLGlue.RL_start();
//			while(RLGlue.RL_step().terminal==0){
//				try {
//					Thread.sleep(1);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		}
//		RLGlue.RL_cleanup();


	}
}
