package visualizers.tetrlais;

import java.awt.Color;
import java.awt.Graphics2D;
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
		double w = 1.0 / (double)numCols;
		double h = 1.0 / (double)numRows;
		double x = 0;
		double y = 0;
		for(int i= 0; i<numRows; i++){
			for(int j=0; j<numCols; j++){
				if(tempWorld[i*numCols+j]==1)
					g.setColor(new Color(0.0f,0.0f,(float) (0.0f + (1/(double)numRows)*((double)i))));
				else
					g.setColor(Color.WHITE);
				x = (j) * (1.0 / (double)numCols);
				y = (i) * (1.0 / (double)numRows);
				agentRect = new Rectangle2D.Double(x, y, w, h);	
				g.fill(agentRect);
			}
		}
	}

	public boolean update() {
		tetVis.updateAgentState();
		return true;
	}

}
