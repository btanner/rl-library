import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;


public class ThreadRenderObject extends Thread {
	BufferedImage tmpImage=null;
	BufferedImage prodImage=null;

	VizComponent theComponent=null;
	Point2D theSize=null;
	
	volatile boolean shouldDie=false;
	
	int defaultSleepTime=50;
	ImageAggregator theBoss;
	
	public ThreadRenderObject(Point2D theSize, VizComponent theComponent, ImageAggregator theBoss){
		this.theComponent=theComponent;
		this.theSize=theSize;
		
		tmpImage=new BufferedImage((int)theSize.getX(),(int)theSize.getY(),BufferedImage.TYPE_INT_RGB);
		prodImage=new BufferedImage((int)theSize.getX(),(int)theSize.getY(),BufferedImage.TYPE_INT_RGB);
		this.theBoss=theBoss;
	}

	public void run() {
		
		while(!shouldDie){

			if(theComponent.update()){
				theComponent.render(tmpImage.createGraphics());
				Graphics2D G=(Graphics2D)prodImage.getGraphics();
				G.drawImage(tmpImage, null, 0, 0);
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
