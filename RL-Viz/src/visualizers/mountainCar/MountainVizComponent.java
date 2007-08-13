package visualizers.mountainCar;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.Vector;

import rlVizLib.utilities.UtilityShop;
import rlVizLib.visualization.VizComponent;

public class MountainVizComponent implements VizComponent {
	private MountainCarVisualizer theVizualizer=null;

	Vector<Double> theHeights=null;
	boolean firstTime = true;

	public MountainVizComponent(MountainCarVisualizer theVizualizer){
		this.theVizualizer=theVizualizer;
	}
	public void render(Graphics2D g) {
		AffineTransform theScaleTransform=new AffineTransform();
		theScaleTransform.scale(1.0d/100.0d,1.0d/100.0d);
		AffineTransform origTransform=g.getTransform();
		
		AffineTransform x = g.getTransform();
		x.concatenate(theScaleTransform);
		g.setTransform(x);
		
		theHeights = theVizualizer.getSampleHeights();

		double sizeEachComponent=1.0/(double)theHeights.size();
	

		g.setColor(Color.BLACK);

		double lastX=0.0d;
		double lastY=1.0-UtilityShop.normalizeValue(theHeights.get(0),theVizualizer.getMinHeight(),theVizualizer.getMaxHeight());
		for(int i=1;i<theHeights.size();i++){
			double thisX=lastX+sizeEachComponent;
			double thisY=1.0-UtilityShop.normalizeValue(theHeights.get(i),theVizualizer.getMinHeight(),theVizualizer.getMaxHeight());
			
			Line2D thisLine=new Line2D.Double(100.0d*lastX, 100.0d*lastY,100.0d* thisX,100.0d*thisY);
			
			lastX=thisX;
			lastY=thisY;

			g.draw(thisLine);
		}

		
		
		
		g.setTransform(origTransform);
		/*
		g.setColor(Color.GREEN);

		//to bring things back into the window
		double minPosition=theVizualizer.getMinValueForDim(0);	
		double maxPosition=theVizualizer.getMaxValueForDim(0);	
		
		double goalHeight = 0;
		Vector<Double> tempVec = new Vector<Double>(0);
		tempVec.add(theVizualizer.getGoalPosition());
		
		goalHeight = theVizualizer.getHeightsForPositions(tempVec).get(0);
		
		double transX = UtilityShop.normalizeValue( theVizualizer.getGoalPosition(),minPosition,maxPosition);
		//need to get he actual height ranges
		double transY = UtilityShop.normalizeValue(
				goalHeight,
				theVizualizer.getMinHeight(),
				theVizualizer.getMaxHeight()
				);
		transY= (1.0-transY);
		
		double rectWidth=.05;
		double rectHeight=.05;
		Rectangle2D fillRect=new Rectangle2D.Double(transX-rectWidth/2.0d,transY-rectHeight/2.0d,rectWidth,rectHeight);
		g.fill(fillRect);
*/

	}

	public boolean update() {
		if(firstTime){
			firstTime = false;
			return true;
		}
			
		return false;
	}

}
