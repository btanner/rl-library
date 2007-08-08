package visualizers.tetrlais;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import rlVizLib.visualization.VizComponent;

public class TetrlaisBlocksComponent implements VizComponent {
	private TetrlaisVisualizer tetVis = null;

	public TetrlaisBlocksComponent(TetrlaisVisualizer ev){
		// TODO Write Constructor
		this.tetVis = ev;
	}

	public void render(Graphics2D g) {
		// TODO Auto-generated method stub
		Rectangle2D agentRect;
		int numCols = tetVis.getWidth();
		int numRows = tetVis.getHeight();
		int [] tempWorld = tetVis.getWorld();
		int w = 100/numCols;
		int h = 100/numRows;
		int x=0;
		int y = 0;
	    AffineTransform saveAT = g.getTransform();
	    g.setColor(Color.GRAY);
   	    g.scale(.01, .01);

		for(int i= 0; i<numRows; i++){
		for(int j=0; j<numCols; j++){
			x = (int)((j) * (100 /numCols));
			y = (int)((i) * (100 /numRows));
			if(tempWorld[i*numCols+j]==1){
				g.setColor(Color.RED);
				g.fill3DRect(x, y, w, h, true);
			}
			else{
				g.setColor(Color.WHITE);
				agentRect = new Rectangle2D.Double(x, y, w, h);	
				g.fill(agentRect);
			}
		}
	}
		g.setColor(Color.GRAY);
   	    g.drawRect(0,0,(int)((100/numCols)*numCols), (int)((100/numRows)*numRows));
	    g.setTransform(saveAT);
	}

	public boolean update() {
		tetVis.updateAgentState();
		return true;
	}

}
