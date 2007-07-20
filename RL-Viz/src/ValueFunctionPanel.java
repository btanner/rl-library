import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Vector;

import javax.swing.JPanel;
import rlglue.Observation;

public class ValueFunctionPanel extends JPanel {
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
	
	BufferedImage latestImage=null;


	public ValueFunctionPanel(Point2D VFRelSize, ValueFunctionDataProvider theDataProvider){
		super();
		currentValueFunctionResolution=50.0;

		relativeVFDrawSize=VFRelSize;
//		parentView=thePV;
		this.dataProvider=theDataProvider;

		bestV=Double.MIN_VALUE;
		worstV=Double.MAX_VALUE;

		theQueryObservations=null;
		this.setSize(200,200);
	}


	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paint(java.awt.Graphics)
	 */
	@Override
	public void paint(Graphics g) {

		super.paint(g);
		
		
		if(valueFunctionPixelSize==null)updateParameters();
		if(latestImage==null){
            latestImage = (BufferedImage)createImage((int)valueFunctionPixelSize.getX(),(int)valueFunctionPixelSize.getY());
		}
		
		
		if(latestImage!=null){
			

//		g.setColor(Color.red);
//		g.fillRect(10, 10, 100, 200);
		
		long currentTime=System.currentTimeMillis();
		if(currentTime-lastQueryTime>1000){
			theValues=dataProvider.queryAgentValues(theQueryObservations);
			lastQueryTime=currentTime;
			render();
		}
		
			Graphics2D g2=(Graphics2D)g;
			g2.drawImage(latestImage, 0, 0, this);
		}
		
		repaint();
	}




//	 public void update(Graphics g){
//	        Graphics2D g2 = (Graphics2D)g;
//	        if(firstTime){
//	            Dimension dim = getSize();
//	            int w = dim.width;
//	            int h = dim.height;
//	            area = new Rectangle(dim);
//	            bi = (BufferedImage)createImage(w, h);
//	            big = bi.createGraphics();
//	            rect.setLocation(w/2-50, h/2-25);
//	            big.setStroke(new BasicStroke(8.0f));
//	            firstTime = false;
//	        } 
//
//	        // Clears the rectangle that was previously drawn.
//	        big.setColor(Color.white);
//	        big.clearRect(0, 0, area.width, area.height);
//
//	        // Draws and fills the newly positioned rectangle to the buffer.
//	        big.setPaint(strokePolka);
//	        big.draw(rect);
//	        big.setPaint(fillPolka);
//	        big.fill(rect);
//
//	        // Draws the buffered image to the screen.
//	        g2.drawImage(bi, 0, 0, this);
//	            
//	    }

	public static double normalizeValue(double theValue, double theMin, double theMax){
		return (theValue-theMin)/(theMax-theMin);
	}


	public int getIndexForRow(int row, int col){
		return (row+col*VFRows);
	}

	public void updateParameters(){
		System.out.println("Update parameters was called in VF Panel!");
		
		double newValueFunctionResolution=5.0d;//[parentView valueFunctionResolution];

		
		System.out.println("\t\t updateParameters is called!");
		if(newValueFunctionResolution!=currentValueFunctionResolution){
			System.out.println("\t\t passed the gate!");
			currentValueFunctionResolution=newValueFunctionResolution;
			Dimension myDrawSize=this.getSize();//new Dimension(200,200);//[parentView envDisplaySize];
			
			
			
			
			//So grid width is the actual number of pixels that each grid cell will be
			valueFunctionPixelSize=new Point2D.Double(myDrawSize.width*relativeVFDrawSize.getX(),myDrawSize.height*relativeVFDrawSize.getY());
			

			VFRows=(int) (valueFunctionPixelSize.getY()/currentValueFunctionResolution);
			VFCols=(int) (valueFunctionPixelSize.getX()/currentValueFunctionResolution);

			System.out.println("My draw size is: "+myDrawSize);
			System.out.println("valueFunctionPixelSize is: "+valueFunctionPixelSize);

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
		
			System.out.println("Update parameters is done... and the Query observations have size: "+theQueryObservations.size()+" (the query states had size: "+theQueryStates.size());

		}else
			System.out.println("\t\t NOT passed the gate!");

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

//	-(NSPoint)getWindowLocationForQueryPoint:(NSPoint)QueryPoint{
//	NSAffineTransform *VFTranslation=[self getTranslation];
//	//I think we first need to normalize the point, and then we can scale it using the translation matrix
//	NSPoint translatedPoint;
//	//This isn't quite the translation we want.
//	translatedPoint.x=(float)VFCols*[ValueFunction normalizeValue:QueryPoint.x withMin:[dataProvider getMinValueForDim:0] andMax:[dataProvider getMaxValueForDim:0]];
//	translatedPoint.y=(float)VFRows*[ValueFunction normalizeValue:QueryPoint.y withMin:[dataProvider getMinValueForDim:1] andMax:[dataProvider getMaxValueForDim:1]];

//	translatedPoint=[VFTranslation transformPoint:translatedPoint];
//	return translatedPoint;
//	}

	Vector<Observation>  getQueryStates(){

		Vector<Observation> thePoints = new Vector<Observation>();

		int y=0;
		int x=0;

		System.out.println("VFRows is: "+VFRows+" and VFCols is: "+VFCols);
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


	public void render(){
		
		if(latestImage==null)updateParameters();
		
		if(latestImage==null)return;

		Graphics2D g=latestImage.createGraphics();

		AffineTransform currentTransform=g.getTransform();
		currentTransform.concatenate(getScaleTransform());
		g.setTransform(currentTransform);

//		[[self getTranslation] concat];

		//We are not going to clear our draw space every frame, we'll just paint over the last version so that we get a wipe instead of a flicker effect
//		[NSBezierPath setDefaultLineWidth:0];

		int y=0;
		int x=0;

		double thisBest=Double.MIN_VALUE;
		double thisWorst=Double.MAX_VALUE;

		
		long time1=System.currentTimeMillis();
		

		if(theValues==null){
			Thread.dumpStack();
			System.exit(1);
		}

		int linearIndex=0;

		for(y=0;y<VFRows;y++){
			for(x=0;x<VFCols;x++){
				Rectangle2D valueRect=new Rectangle2D.Double(x,y,1,1);
//				NSRect valueRect=NSMakeRect(x,y,1,1);

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

		updateParameters();
		


		worstV=thisWorst;
		bestV=thisBest;
		
		long time2=System.currentTimeMillis();
		System.out.println("Time to draw was: "+(time2-time1));
	}




}
