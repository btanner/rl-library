package visualizers.mountainCar;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import rlVizLib.visualization.VizComponent;

public class MountainCarScoreComponent implements VizComponent{
	private MountainCarVisualizer mcVis = null;
	
	public MountainCarScoreComponent(MountainCarVisualizer mcVis){
		this.mcVis = mcVis;
	}

	public void render(Graphics2D g) {
		//This is some hacky stuff, someone better than me should clean it up
		Font f = new Font("Verdana",0,8);     
		g.setFont(f);
	    //SET COLOR
	    g.setColor(Color.BLACK);
	    //DRAW STRING
	    AffineTransform saveAT = g.getTransform();
   	    g.scale(.005, .005);
	    g.drawString("E/S/T: " +mcVis.getEpisodeNumber()+"/"+mcVis.getTimeStep()+"/"+mcVis.getTotalTimeSteps(),0.0f, 10.0f);
	    g.setTransform(saveAT);
	}

	public boolean update() {
		mcVis.updateAgentState();
		return true;
	}
	
	
}
