import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Vector;


public abstract class EnvVisualizer implements ImageAggregator {
	private BufferedImage productionEnvImage=null;
	private BufferedImage bufferEnvImage=null;
	private Component parentComponent=null;

	private Vector<ThreadRenderObject> threadRunners=new Vector<ThreadRenderObject>();
	private Vector<Thread> theThreads=new Vector<Thread>();

	
	public EnvVisualizer(){
		productionEnvImage=new BufferedImage(200,200,BufferedImage.TYPE_INT_RGB);

	}
	
	public BufferedImage getLatestImage() {
		return productionEnvImage;
	}

	//Maybe addVizComponent at location with size
	public void addVizComponent(VizComponent newComponent){
		threadRunners.add(new ThreadRenderObject(new Point2D.Double(200.0,200.0),newComponent,this));
	}

	private void redrawCurrentImage(){
		bufferEnvImage=new BufferedImage(200,200,BufferedImage.TYPE_INT_RGB);
		Graphics2D G= bufferEnvImage.createGraphics();
		for (ThreadRenderObject thisRunner : threadRunners) {
			//this needs to actually just return something that draws when it gets an update signal or something
			G.drawImage(thisRunner.getProductionImage(),0,0,null);
		}
		productionEnvImage=bufferEnvImage;
		bufferEnvImage=null;

	}

	public void receiveNotification() {
		//One of the guys I draw has updates
		redrawCurrentImage();
		parentComponent.repaint();
	}

	public void setParentComponent(Component theComponent) {
		this.parentComponent=theComponent;
	}

	protected void startVisualizing() {
		for (ThreadRenderObject thisRunner : threadRunners) {
			Thread theThread=new Thread(thisRunner);
			theThreads.add(theThread);
			theThread.start();
		}
	}
	protected void stopVisualizing() {
//tell them all to die
		for (ThreadRenderObject thisRunner : threadRunners) {
			thisRunner.kill();
		}

//wait for them all to be done
		for (Thread thisThread : theThreads) {
			try {
				thisThread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
