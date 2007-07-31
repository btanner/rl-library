package vizComponents;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import rlVizLib.visualization.VizComponent;



public class RedBoxVizComponent implements VizComponent {


	public void render(Graphics2D g) {
		
	g.setColor(Color.RED);
		Rectangle2D fillRect=new Rectangle2D.Double(0d,0d,1d,1d);
		g.fill(fillRect);

	}

	public boolean update() {
		return true;
	}

}
