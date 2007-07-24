import java.awt.Graphics2D;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

import visualization.EnvVisualizer;

public class EnvironmentPanel extends JPanel {

	BufferedImage latestImage=null;
    EnvVisualizer theVisualizer=null;

	public EnvironmentPanel(Dimension theSize, EnvVisualizer theVisualizer){
		super();
		this.setSize((int)theSize.getWidth(), (int)theSize.getHeight());
		this.theVisualizer=theVisualizer;
		theVisualizer.setParentComponent(this);
	}

	int paintCount=0;

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paint(java.awt.Graphics)
	 */
	@Override
	public void paint(Graphics g) {
		paintCount++;
		
		
		super.paint(g);
		
		Graphics2D g2=(Graphics2D)g;
		
//		g2.setColor(Color.RED);
//		g2.fillRect(0,0,100,100);

		g2.drawImage(theVisualizer.getLatestImage(), 0, 0, this);
//		
//		BufferedImage b=new BufferedImage(150,150,BufferedImage.TYPE_INT_ARGB);
//		Graphics2D gSECOND=b.createGraphics();
////		Color myClearColor=new Color(0.0f,0.0f,0.0f,0.0f);
//////		
//////		gSECOND.setColor(myClearColor);
//////		gSECOND.fillRect(0,0,150,150);
////		
//		gSECOND.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
//		Rectangle2D.Double rect = new Rectangle2D.Double(0,0,150,150); 
//		gSECOND.fill(rect);
//		gSECOND.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
////		gSECOND.clearRect(0,0,150,150);
////		
//		gSECOND.setColor(Color.BLUE);
//		gSECOND.fillRect(0, 0, 10+(paintCount/25), 10+(paintCount/25));
////		
//		g2.drawImage(b,0,0,null);
//		
//		this.repaint();
	}





}
