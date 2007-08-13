package visualizers.mountainCar;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import rlVizLib.utilities.UtilityShop;
import rlVizLib.visualization.VizComponent;


public class CarOnMountainVizComponent implements VizComponent {
	private MountainCarVisualizer mcv = null;


	
	
	public CarOnMountainVizComponent(MountainCarVisualizer mc){
		this.mcv = mc;

	}


	public void render(Graphics2D g) {
		
		g.setColor(Color.RED);

		//to bring things back into the window
		double minPosition=mcv.getMinValueForDim(0);	
		double maxPosition=mcv.getMaxValueForDim(0);	
		
		double transX = UtilityShop.normalizeValue( this.mcv.getCurrentStateInDimension(0),	minPosition,maxPosition);
		
		//need to get he actual height ranges
		double transY = UtilityShop.normalizeValue(
				this.mcv.getHeight(),
				mcv.getMinHeight(),
				mcv.getMaxHeight()
				);
		transY= (1.0-transY);
		
		double rectWidth=.05;
		double rectHeight=.05;
		Rectangle2D fillRect=new Rectangle2D.Double(transX-rectWidth/2.0d,transY-rectHeight/2.0d,rectWidth,rectHeight);
		g.fill(fillRect);

	}

	public boolean update() {
		this.mcv.updateAgentState();
		return true;
	}

}
