package visualization;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
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

	private Vector<Point2D> positions=new Vector<Point2D>();
	private Vector<Point2D> sizes=new Vector<Point2D>();


	Dimension currentVisualizerPanelSize=new Dimension(100,100);

	boolean currentlyRunning=false;

	public void receiveSizeChange(Dimension newPanelSize){
		currentVisualizerPanelSize=newPanelSize;
		resizeImages();
	}

	private synchronized void resizeImages() {
		productionEnvImage=new BufferedImage((int)currentVisualizerPanelSize.getWidth(),(int)currentVisualizerPanelSize.getHeight(),BufferedImage.TYPE_INT_ARGB);
		bufferEnvImage=new BufferedImage((int)currentVisualizerPanelSize.getWidth(),(int)currentVisualizerPanelSize.getHeight(),BufferedImage.TYPE_INT_ARGB);

		for(int i=0;i<threadRunners.size();i++){
			threadRunners.get(i).receiveSizeChange(makeSizeForVizComponent(i));
		}

	}

	public EnvVisualizer(){
		resizeImages();
	}

	public BufferedImage getLatestImage() {
		return productionEnvImage;
	}


	private synchronized void redrawCurrentImage(){
		Graphics2D G= bufferEnvImage.createGraphics();
		Color myClearColor=new Color(0.0f,0.0f,0.0f,0.0f);
		G.setColor(myClearColor);
		G.setBackground(myClearColor);
		G.clearRect(0,0,bufferEnvImage.getWidth(),bufferEnvImage.getHeight());

		for(int i=0;i<threadRunners.size();i++){
			ThreadRenderObject thisRunner=threadRunners.get(i);

			Dimension position=makeLocationForVizComponent(i);

			G.drawImage(thisRunner.getProductionImage(),position.width,position.height,null);
		}

		BufferedImage tmpImage=productionEnvImage;
		productionEnvImage=bufferEnvImage;
		bufferEnvImage=tmpImage;

	}

	public void receiveNotification() {
		//One of the guys I draw has updates
		redrawCurrentImage();
		parentComponent.repaint();
	}

	public void setParentComponent(Component theComponent) {
		this.parentComponent=theComponent;
	}

	public void startVisualizing() {
		currentlyRunning=true;
		for (ThreadRenderObject thisRunner : threadRunners) {
			Thread theThread=new Thread(thisRunner);
			theThreads.add(theThread);
			theThread.start();
		}
		
	}
	public void stopVisualizing() {
//		tell them all to die
		for (ThreadRenderObject thisRunner : threadRunners) {
			thisRunner.kill();
		}

//		wait for them all to be done
		for (Thread thisThread : theThreads) {
			try {
				thisThread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		currentlyRunning=false;

	}

	private Dimension makeSizeForVizComponent(int i){
		double width=currentVisualizerPanelSize.getWidth();
		double height=currentVisualizerPanelSize.getHeight();

		double scaledWidth=width*sizes.get(i).getX();
		double scaledHeight=height*sizes.get(i).getY();

		Dimension d=new Dimension();
		d.setSize(scaledWidth,scaledHeight);

		return d;
	}
	private Dimension makeLocationForVizComponent(int i){
		double width=currentVisualizerPanelSize.getWidth();
		double height=currentVisualizerPanelSize.getHeight();

		double startX=width*positions.get(i).getX();
		double startY=height*positions.get(i).getY();

		Dimension d=new Dimension();
		d.setSize(startX,startY);

		return d;
	}

	//All of these should be between 0 and 1
	public void addVizComponentAtPositionWithSize(VizComponent newComponent, double xPos, double yPos, double width, double height){
		threadRunners.add(new ThreadRenderObject(new Dimension(200,200),newComponent,this));
		positions.add(new Point2D.Double(xPos,yPos));
		sizes.add(new Point2D.Double(width,height));

	}

	public boolean isCurrentlyRunning() {
		return currentlyRunning;
	}

	public void setValueFunctionResolution(int theValue) {
		//Maybe nobody uses this and it just does nothing
		//If you override it, you can use it!
	}


}
