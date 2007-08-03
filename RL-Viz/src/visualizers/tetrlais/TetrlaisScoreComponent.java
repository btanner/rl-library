package visualizers.tetrlais;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import rlVizLib.visualization.VizComponent;

public class TetrlaisScoreComponent implements VizComponent{
	private TetrlaisVisualizer tetVis = null;
	
	int lastScore=0;

	public TetrlaisScoreComponent(TetrlaisVisualizer ev){
		this.tetVis = ev;
		lastScore=-1;
	}

	public void render(Graphics2D g) {
		//This is some hacky stuff, someone better than me should clean it up
		Font f = new Font("Verdana",0,8);     
		g.setFont(f);
	    //SET COLOR
	    g.setColor(Color.BLACK);
	    //DRAW STRING
	    AffineTransform saveAT = g.getTransform();
   	    g.scale(.01, .01);
	    g.drawString("Lines: " +tetVis.getScore(),0.0f, 10.0f);
	    g.drawString("E/S: " +tetVis.getEpisodeNumber()+"/"+tetVis.getTimeStep(),0.0f, 20.0f);
	    g.setTransform(saveAT);
	}

	public boolean update() {
		this.tetVis.updateAgentState();
		return true;
		
		
	}
	
	
}
