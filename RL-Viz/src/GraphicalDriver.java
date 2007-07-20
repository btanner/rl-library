import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.Vector;

import javax.swing.JPanel;

import messaging.environment.EnvRangeRequest;
import messaging.environment.EnvRangeResponse;

import rlglue.RLGlue;

import java.util.concurrent.locks.*;



public class GraphicalDriver {

	
	
	
	
	   public static void main(String [] args) throws IOException {

		RLGlue.RL_init();

		RLVizFrame theFrame=new RLVizFrame();
			
			//Get the Ranges
			EnvRangeResponse theERResponse=EnvRangeRequest.Execute();
			
			Vector<Double> mins = theERResponse.getMins();
			Vector<Double> maxs = theERResponse.getMaxs();
	
			theFrame.setRanges(mins, maxs);
			
		  ValueFunctionPanel VFPanel=new ValueFunctionPanel(new Point2D.Double(1.0, 1.0), theFrame);
		  VFPanel.updateParameters();

		  theFrame.getContentPane().add(VFPanel);
		   theFrame.setSize(800,600);
		   theFrame.setVisible(true);
		   
		   
	    	for(int x = 0; x < 50000; ++x) {
	    		RLGlue.RL_start();
	    		while(RLGlue.RL_step().terminal==0);
	    	}
	    	RLGlue.RL_cleanup();

		   
	   }
}
