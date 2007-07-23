import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Vector;

import javax.swing.JPanel;
import rlglue.Observation;

public class ValueFunctionVizComponent implements VizComponent {
	Point2D relativeVFDrawSize;
	Point2D valueFunctionPixelSize=null;
	
	long lastQueryTime=0;
	Vector<Double> theValues=null;
	
	double bestV;
	double worstV;

	int VFRows;
	int VFCols;

	double rowGridSize;
	double colGridSize;

	double xQueryIncrement;
	double yQueryIncrement;

//	id<EnvVisualizerView> parentView;
	ValueFunctionDataProvider dataProvider;

	double currentValueFunctionResolution;

	Vector<Observation> theQueryObservations=null;

	public ValueFunctionVizComponent(Point2D VFRelSize, ValueFunctionDataProvider theDataProvider){
		super();
		currentValueFunctionResolution=50.0;

		relativeVFDrawSize=VFRelSize;
//		parentView=thePV;
		this.dataProvider=theDataProvider;

		bestV=Double.MIN_VALUE;
		worstV=Double.MAX_VALUE;

		theQueryObservations=null;
	}



	public static double normalizeValue(double theValue, double theMin, double theMax){
		return (theValue-theMin)/(theMax-theMin);
	}


	public int getIndexForRow(int row, int col){
		return (row+col*VFRows);
	}








	//I need to know how to do this

	private AffineTransform getScaleTransform(){
		AffineTransform newTransform=new AffineTransform();
		
		Point2D mySize=new Point2D.Double(200.0, 200.0);
		
		Point2D valueFunctionPixelSize=new Point2D.Double(mySize.getX()*relativeVFDrawSize.getX(),mySize.getY()*relativeVFDrawSize.getY());


	double xScale=valueFunctionPixelSize.getX()/(double)VFCols;
	double yScale=valueFunctionPixelSize.getY()/(double)VFRows;
//
//
//	float bottomY=mySize.height-valueFunctionPixelSize.height;
//
//	NSAffineTransform* xform = [NSAffineTransform transform];
//	[xform translateXBy:0.0 yBy:bottomY];
//	[xform scaleXBy:xScale yBy:yScale];
//	return xform;
		
		newTransform.scale(xScale, yScale);
		
		return newTransform;
	}

	public Point2D getWindowLocationForQueryPoint(Point2D QueryPoint){
//	NSAffineTransform *VFTranslation=[self getTranslation];
	//I think we first need to normalize the point, and then we can scale it using the translation matrix
//	NSPoint translatedPoint;
	//This isn't quite the translation we want.
//	translatedPoint.x=(float)VFCols*[ValueFunction normalizeValue:QueryPoint.x withMin:[dataProvider getMinValueForDim:0] andMax:[dataProvider getMaxValueForDim:0]];
//	translatedPoint.y=(float)VFRows*[ValueFunction normalizeValue:QueryPoint.y withMin:[dataProvider getMinValueForDim:1] andMax:[dataProvider getMaxValueForDim:1]];
//	translatedPoint=[VFTranslation transformPoint:translatedPoint];
		
	double transX=(float)VFCols*normalizeValue(QueryPoint.getX(),dataProvider.getMinValueForDim(0),dataProvider.getMaxValueForDim(0));
	double transY=(float)VFRows*normalizeValue(QueryPoint.getY(),dataProvider.getMinValueForDim(1),dataProvider.getMaxValueForDim(1));
		
	return new Point2D.Double(transX,transY);

	}

	Vector<Observation>  getQueryStates(){
		Vector<Observation> thePoints = new Vector<Observation>();

		int y=0;
		int x=0;

		for(y=0;y<VFRows;y++){
			for(x=0;x<VFCols;x++){

				//Query the value function in the agent
				double positionVal=dataProvider.getMinValueForDim(0)+(double)x*xQueryIncrement;
				double velocityVal=dataProvider.getMinValueForDim(1)+(double)y*yQueryIncrement;

				Observation thisState=new Observation(0,2);
				thisState.doubleArray[0]=positionVal;
				thisState.doubleArray[1]=velocityVal;
				thePoints.add(thisState);
			}
		}
		return thePoints;
	}


	public void render(Graphics2D g){

		AffineTransform currentTransform=g.getTransform();
		currentTransform.concatenate(getScaleTransform());
		g.setTransform(currentTransform);

		int y=0;
		int x=0;

		double thisBest=Double.MIN_VALUE;
		double thisWorst=Double.MAX_VALUE;

		if(theValues==null){
			Thread.dumpStack();
			System.exit(1);
		}

		int linearIndex=0;

		for(y=0;y<VFRows;y++){
			for(x=0;x<VFCols;x++){
				Rectangle2D valueRect=new Rectangle2D.Double(x,y,1,1);

				double V=theValues.get(linearIndex);
				if(Double.isInfinite(V)||Double.isNaN(V)){
					System.out.println("The value at linear index: "+linearIndex+" + is "+V+"+ (size is "+theValues.size());
				}

				if(V<thisWorst)thisWorst=V;
				if(V>thisBest)thisBest=V;

				float greenValue=(float)normalizeValue(V, worstV,  bestV);
				
//				System.out.println("GreenValue is: "+greenValue+" because V was: "+V+" and worstV was: "+worstV+" and bestV was: "+bestV);
				
				if(greenValue<0)greenValue=0;
				if(greenValue>1)greenValue=1;
				
				
				Color theColor=new Color(0, greenValue, 0);
				
				g.setColor(theColor);
				g.fill(valueRect);

				linearIndex++;
			}
		}

		worstV=thisWorst;
		bestV=thisBest;
	}




	public boolean update() {

//Update the resolution
		double newValueFunctionResolution=5.0d;//[parentView valueFunctionResolution];
		
		if(newValueFunctionResolution!=currentValueFunctionResolution || theQueryObservations==null){

			currentValueFunctionResolution=newValueFunctionResolution;
			Dimension myDrawSize=new Dimension(200,200);//[parentView envDisplaySize];
			
			
			
			
			//So grid width is the actual number of pixels that each grid cell will be
			valueFunctionPixelSize=new Point2D.Double(myDrawSize.width*relativeVFDrawSize.getX(),myDrawSize.height*relativeVFDrawSize.getY());
			

			VFRows=(int) (valueFunctionPixelSize.getY()/currentValueFunctionResolution);
			VFCols=(int) (valueFunctionPixelSize.getX()/currentValueFunctionResolution);

			//The range of the position and velocity
			double xRangeSize=dataProvider.getMaxValueForDim(0) - dataProvider.getMinValueForDim(0);
			double yRangeSize=dataProvider.getMaxValueForDim(1) - dataProvider.getMinValueForDim(1);

			//QueryIncrements are the number that the query variables will change from cell to cell	
			xQueryIncrement=xRangeSize/(float)VFCols;
			yQueryIncrement=yRangeSize/(float)VFRows;

			rowGridSize=(float)valueFunctionPixelSize.getY()/(float)VFRows;
			colGridSize=(float)valueFunctionPixelSize.getX()/(float)VFCols;

			Vector<Observation> theQueryStates=getQueryStates();
			theQueryObservations=dataProvider.getQueryObservations(theQueryStates);
		}

		theValues=dataProvider.queryAgentValues(theQueryObservations);

		//maybe we shouldn't return true all the time
		return true;	
		}




}
