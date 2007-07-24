import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;


public class ThreadRenderObject extends Thread {
	BufferedImage workImage=null;
	BufferedImage prodImage=null;

	VizComponent theComponent=null;
	Point2D theSize=null;
	
	volatile boolean shouldDie=false;
	
	AffineTransform theScaleTransform=null;
	int defaultSleepTime=50;
	ImageAggregator theBoss;
	
	public ThreadRenderObject(Point2D theSize, VizComponent theComponent, ImageAggregator theBoss){
		this.theComponent=theComponent;
		this.theSize=theSize;
		
		workImage=new BufferedImage((int)theSize.getX(),(int)theSize.getY(),BufferedImage.TYPE_INT_ARGB);

		//Set the transform on the image so we can draw everything in [0,1]


		theScaleTransform=new AffineTransform();
		theScaleTransform.scale(theSize.getX(), theSize.getY());

		
		prodImage=new BufferedImage((int)theSize.getX(),(int)theSize.getY(),BufferedImage.TYPE_INT_ARGB);
		
		
		this.theBoss=theBoss;
	}
	

	@Override
	public void run() {
		
		while(!shouldDie){

			if(theComponent.update()){
				Graphics2D g=workImage.createGraphics();
				
				//Set the scaling transform
				AffineTransform currentTransform=g.getTransform();
				currentTransform.concatenate(theScaleTransform);
				g.setTransform(currentTransform);

				//Clear the screen to transparent
				Color myClearColor=new Color(0.0f,0.0f,0.0f,0.0f);
				g.setColor(myClearColor);
				g.setBackground(myClearColor);
				g.clearRect(0,0,1,1);

				theComponent.render(g);
				
				BufferedImage tmpImage=prodImage;
				prodImage=workImage;
				workImage=tmpImage;
				
				theBoss.receiveNotification();

				try {
					Thread.yield();
//					Thread.sleep(defaultSleepTime);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}

	}

	public BufferedImage getProductionImage() {
		return prodImage;
	}

	public void kill() {
		shouldDie=true;
	}

}
