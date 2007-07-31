package rlViz;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import rlVizLib.visualization.EnvVisualizer;


public class EnvironmentPanel extends JPanel implements ComponentListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	BufferedImage latestImage=null;
    EnvVisualizer theVisualizer=null;


    public void setVisualizer(EnvVisualizer theVisualizer){
    	if(this.theVisualizer!=null)this.theVisualizer.stopVisualizing();
    	
    	this.theVisualizer=theVisualizer;
		theVisualizer.setParentComponent(this); 
    }
	public EnvironmentPanel(Dimension initialSize){
		super();
		this.setSize((int)initialSize.getWidth(), (int)initialSize.getHeight());

		addComponentListener(this);
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
		
		g2.setColor(Color.BLUE);
		g2.fillRect(0,0,1000,1000);

		if(theVisualizer!=null)
			g2.drawImage(theVisualizer.getLatestImage(), 0, 0, this);
        
	}

	public void componentHidden(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void componentMoved(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void componentResized(ComponentEvent arg0) {
		if(theVisualizer!=null)
       		theVisualizer.receiveSizeChange(arg0.getComponent().getSize());
	}

	public void componentShown(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}




}
