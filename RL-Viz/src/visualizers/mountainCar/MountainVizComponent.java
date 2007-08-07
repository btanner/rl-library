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

	Vector<Double> theQueryPositions=null;
	Vector<Double> theHeights=null;

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
		
		
		double maxHeight=Double.MIN_VALUE;
		double minHeight=Double.MAX_VALUE;
		for (Double thisHeight : theHeights) {
			if(thisHeight>maxHeight)maxHeight=thisHeight;
			if(thisHeight<minHeight)minHeight=thisHeight;
		}


		double sizeEachComponent=1.0/(double)theHeights.size();
	

		g.setColor(Color.BLACK);

		double lastX=0.0d;
		double lastY=1.0-UtilityShop.normalizeValue(theHeights.get(0),minHeight,maxHeight);
		for(int i=1;i<theHeights.size();i++){
			double thisX=lastX+sizeEachComponent;
			double thisY=1.0-UtilityShop.normalizeValue(theHeights.get(i),minHeight,maxHeight);
			
			Line2D thisLine=new Line2D.Double(100.0d*lastX, 100.0d*lastY,100.0d* thisX,100.0d*thisY);
			
			lastX=thisX;
			lastY=thisY;

			g.draw(thisLine);
		}

		g.setTransform(origTransform);


	}

	public boolean update() {
		if(theQueryPositions==null){
			double minPosition=theVizualizer.getMinValueForDim(0);
			double maxPosition=theVizualizer.getMaxValueForDim(0);

			int pointsToDraw=100;
			double theRangeSize=maxPosition-minPosition;
			double pointIncrement=theRangeSize/(double)pointsToDraw;

			theQueryPositions=new Vector<Double>();
			for(double i=minPosition;i<maxPosition;i+=pointIncrement){
				theQueryPositions.add(i);
			}

			theHeights=theVizualizer.getHeightsForPositions(theQueryPositions);
			return true;
		}
		return false;
	}

}
