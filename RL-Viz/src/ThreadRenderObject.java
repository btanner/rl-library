import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Point2D;


public class ThreadRenderObject extends Thread {
	Image tmpImage=null;
	Image prodImage=null;

	VizComponent theComponent=null;
	Point2D theSize=null;
	
	volatile boolean shouldDie=false;
	
	int defaultSleepTime=50;
	
	
	public ThreadRenderObject(Point2D theSize, VizComponent theComponent){
		this.theComponent=theComponent;
		this.theSize=theSize;
	}

	public void run() {
		
		while(!shouldDie){
			if(theComponent.update()){
				theComponent.render(tmpImage);
				Graphics2D G=(Graphics2D)prodImage.getGraphics();
				//G.drawImage(arg0, arg1, arg2, arg3);
			}
			
		}

	}

}
