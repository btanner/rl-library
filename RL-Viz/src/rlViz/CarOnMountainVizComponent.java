package rlViz;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import visualization.VizComponent;


public class CarOnMountainVizComponent implements VizComponent {


	public void render(Graphics2D g) {
		
//		g.setColor(Color.LIGHT_GRAY);
//		Rectangle2D fillRect=new Rectangle2D.Double(0d,0d,1d,1d);
//		g.fill(fillRect);
		
		g.setColor(Color.BLACK);
		Font f = new Font("TimesRoman",Font.BOLD,24);
		//This doesn't work, not sure why
		g.setFont(f);
		 g.drawString("The car on mountain will go here", .5f, .5f);
	
	}

	public boolean update() {
		return true;
	}

}
