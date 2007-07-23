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

public class EnvironmentPanel extends JPanel {

	BufferedImage latestImage=null;
    EnvVisualizer theVisualizer=null;

	public EnvironmentPanel(Dimension theSize, EnvVisualizer theVisualizer){
		super();
		this.setSize((int)theSize.getWidth(), (int)theSize.getHeight());
		this.theVisualizer=theVisualizer;
		theVisualizer.setParentComponent(this);
	}


	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paint(java.awt.Graphics)
	 */
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2=(Graphics2D)g;
		g2.drawImage(theVisualizer.getLatestImage(), 0, 0, this);
	}





}
