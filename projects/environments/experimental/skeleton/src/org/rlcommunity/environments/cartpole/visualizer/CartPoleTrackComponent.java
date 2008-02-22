package org.rlcommunity.environments.cartpole.visualizer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import rlVizLib.visualization.VizComponent;

public class CartPoleTrackComponent implements VizComponent  {
	private CartPoleVisualizer cartVis = null;
	boolean drawn = false;
	public CartPoleTrackComponent(CartPoleVisualizer cartpoleVisualizer) {
		cartVis = cartpoleVisualizer;
	}
	public void render(Graphics2D g) {
	    //SET COLOR
	    g.setColor(Color.BLACK);
	    //DRAW 12 Lines with blue ball equalizers.
	    
	    AffineTransform saveAT = g.getTransform();
		    g.scale(.01, .01);
			g.drawLine(10 , 90, 90, 90);
	    g.setTransform(saveAT);		
	}
	public boolean update() {
		if(!drawn){
			drawn = true;
			return cartVis.updateTrack();
		}
		return false;
	}

}
