package visualizers.mountainCar;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import rlVizLib.utilities.UtilityShop;
import rlVizLib.visualization.VizComponent;


public class CarOnMountainVizComponent implements VizComponent {
	private MountainCarVisualizer mcv = null;
	private Image img = null;
	private AffineTransform scaleTransform = null;
	private AffineTransform translateTransform=null;
	private AffineTransform rotateTransform = null;

	
	
	public CarOnMountainVizComponent(MountainCarVisualizer mc){
		this.mcv = mc;
		try {
			String curDir = System.getProperty("user.dir");
			File d= new File(curDir);
			String workSpaceDir=d.getParent();

            this.img = ImageIO.read(new File(workSpaceDir+"/MountainCar/images/Mazda.gif"));
            this.scaleTransform =
                AffineTransform.getScaleInstance((double)0.1*(this.img.getWidth(null)/this.img.getHeight(null))/this.img.getWidth(null),
                    (double)0.1*(this.img.getWidth(null)/this.img.getHeight(null))/this.img.getHeight(null));
		} catch (IOException e) {
        	System.out.println("Cannot open car");
        }   
	}


	public void render(Graphics2D g) {
		
//		g.setColor(Color.LIGHT_GRAY);
//		Rectangle2D fillRect=new Rectangle2D.Double(0d,0d,1d,1d);
//		g.fill(fillRect);
		
		
		g.setColor(Color.RED);
		//Font f = new Font("TimesRoman",Font.BOLD,24);
		//This doesn't work, not sure why
		//g.setFont(f);
		 //g.drawString("CAR", 0.2f, 0.2f);
		
		double transX = UtilityShop.normalizeValue( this.mcv.getCurrentStateInDimension(0),
				this.mcv.getMinValueForDim(0),
				this.mcv.getMaxValueForDim(0));
		
		double transY = UtilityShop.normalizeValue(
				this.mcv.getHeight(),
				-1.0f,
				1.0f
				);
		transY= (1.0-transY);
		
		
        this.translateTransform =AffineTransform.getTranslateInstance(transX,transY+0.01);
        this.rotateTransform = AffineTransform.getRotateInstance((Math.atan((this.mcv.getHeight()-this.mcv.getDeltaHeight())/0.05)),transX, transY);
        translateTransform.concatenate(scaleTransform);
        translateTransform.concatenate(this.rotateTransform);
		//Rectangle2D agentRect=new Rectangle2D.Double(transX,transY,0.2,0.2);
		//g.fill(agentRect);
		((Graphics2D)g).drawRenderedImage((RenderedImage) this.img,translateTransform);

	}

	public boolean update() {
		this.mcv.updateAgentState();
		return true;
	}

}
