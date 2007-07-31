package rlViz;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import rlVizLib.visualization.VizComponent;

public class TetrlaisScoreComponent implements VizComponent{
	private TetrlaisVisualizer tetVis = null;
	

	public TetrlaisScoreComponent(TetrlaisVisualizer ev){
		// TODO Write Constructor
		this.tetVis = ev;
	}

	public void render(Graphics2D g) {
		// TODO Auto-generated method stub
//		g.setColor(new Color(0.0f, 0.0f, (float) (0.0f + this.tetVis.getScore()*0.1)));
//		Rectangle2D.Double agentRect = new Rectangle2D.Double(0.0f,0.0f, 1.0f,1.0f);
//		g.fill(agentRect);
		
		Font f = new Font("Courier",Font.ITALIC + Font.BOLD,1);     
		g.setFont(f);
	    g.setBackground(Color.BLACK);
	    //SET COLOR
	    g.setColor(Color.WHITE);
	    //DRAW STRING
	    g.drawString("" +this.tetVis.getScore(),0,1);
		
	}

	public boolean update() {
		this.tetVis.updateAgentState();
		return true;
	}
	
	
}
