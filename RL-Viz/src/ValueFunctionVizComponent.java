import interfaces.ValueFunctionDataProvider;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.Vector;

import rlglue.Observation;
import utilities.UtilityShop;

public class ValueFunctionVizComponent implements VizComponent {
//	Point2D relativeVFDrawSize;
//	Point2D valueFunctionPixelSize=null;

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

	public ValueFunctionVizComponent(ValueFunctionDataProvider theDataProvider){
		super();
		currentValueFunctionResolution=50.0;

//		relativeVFDrawSize=VFRelSize;
//		parentView=thePV;
		this.dataProvider=theDataProvider;

		bestV=Double.MIN_VALUE;
		worstV=Double.MAX_VALUE;

		theQueryObservations=null;
	}





	public int getIndexForRow(int row, int col){
		return (row+col*VFRows);
	}




	Vector<Observation>  getQueryStates(){
		Vector<Observation> thePoints = new Vector<Observation>();

		int y=0;
		int x=0;

		for(y=0;y<VFRows;y++){
			for(x=0;x<VFCols;x++){

				//Query the value function in the agent
				double positionVal=dataProvider.getMinValueForDim(0)+x*xQueryIncrement;
				double velocityVal=dataProvider.getMinValueForDim(1)+y*yQueryIncrement;

				Observation thisState=new Observation(0,2);
				thisState.doubleArray[0]=positionVal;
				thisState.doubleArray[1]=velocityVal;
				thePoints.add(thisState);
			}
		}
		return thePoints;
	}


	public void render(Graphics2D g){
		double y=0;
		double x=0;

		double thisBest=Double.MIN_VALUE;
		double thisWorst=Double.MAX_VALUE;

		if(theValues==null){
			Thread.dumpStack();
			System.exit(1);
		}

		int linearIndex=0;

		for(y=0;y<VFRows;y++){
			for(x=0;x<VFCols;x++){
				Rectangle2D valueRect=new Rectangle2D.Double(x*rowGridSize,y*colGridSize,rowGridSize,colGridSize);

				double V=theValues.get(linearIndex);
				if(Double.isInfinite(V)||Double.isNaN(V)){
					System.out.println("The value at linear index: "+linearIndex+" + is "+V+"+ (size is "+theValues.size());
				}

				if(V<thisWorst)thisWorst=V;
				if(V>thisBest)thisBest=V;

				float greenValue=(float)UtilityShop.normalizeValue(V, worstV,  bestV);

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

//		Update the resolution
		double newValueFunctionResolution=30.0d;

		if(newValueFunctionResolution!=currentValueFunctionResolution || theQueryObservations==null){

			currentValueFunctionResolution=newValueFunctionResolution;

			VFRows=(int) currentValueFunctionResolution;
			VFCols=(int) currentValueFunctionResolution;

			//The range of the position and velocity
			double xRangeSize=dataProvider.getMaxValueForDim(0) - dataProvider.getMinValueForDim(0);
			double yRangeSize=dataProvider.getMaxValueForDim(1) - dataProvider.getMinValueForDim(1);

			//QueryIncrements are the number that the query variables will change from cell to cell	
			xQueryIncrement=xRangeSize/VFCols;
			yQueryIncrement=yRangeSize/VFRows;

			rowGridSize=1.0d/VFRows;
			colGridSize=1.0d/VFCols;

			Vector<Observation> theQueryStates=getQueryStates();
			theQueryObservations=dataProvider.getQueryObservations(theQueryStates);
		}

		theValues=dataProvider.queryAgentValues(theQueryObservations);

		//maybe we shouldn't return true all the time
		return true;	
	}




}
