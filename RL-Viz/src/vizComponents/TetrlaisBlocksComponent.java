package vizComponents;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import rlViz.TetrlaisVisualizer;

import rlVizLib.visualization.EnvVisualizer;
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
		int blockWidth = tetVis.getWidth();
		int blockHeight = tetVis.getHeight();
		int [] tempWorld = tetVis.getWorld();
		double w = 1.0 / (double)blockWidth;
		double h = 1.0 / (double)blockHeight;
		double x = 0;
		double y = 0;
		for(int i= 0; i<blockHeight; i++){
			for(int j=0; j<blockWidth; j++){
				if(tempWorld[i*blockWidth+j]==1)
					g.setColor(new Color(0.0f,0.0f,(float) (0.0f + (1/(double)blockHeight)*((double)i))));
				else
					g.setColor(Color.WHITE);
				x = (j) * (1.0 / (double)blockWidth);
				y = (i) * (1.0 / (double)blockHeight);
				agentRect = new Rectangle2D.Double(x, y, w, h);	
				g.fill(agentRect);
			}
		}
	}

	public boolean update() {
		this.tetVis.updateAgentState();
		return true;
	}

}
