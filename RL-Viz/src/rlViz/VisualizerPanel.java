package rlViz;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import rlVizLib.general.ParameterHolder;
import rlVizLib.general.TinyGlue;
import rlVizLib.visualization.AbstractVisualizer;
import rlVizLib.visualization.VisualizerPanelInterface;


public class VisualizerPanel extends JPanel implements ComponentListener, VisualizerPanelInterface,visualizerLoadListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	BufferedImage latestImage=null;
	AbstractVisualizer theVisualizer=null;



	public VisualizerPanel(Dimension initialSize){
		super();
		this.setSize((int)initialSize.getWidth(), (int)initialSize.getHeight());
		addComponentListener(this);
	}


	@Override
	public void paint(Graphics g) {
		super.paint(g);

		Graphics2D g2=(Graphics2D)g;

//		g2.setColor(Color.BLUE);
//		g2.fillRect(0,0,1000,1000);

		if(theVisualizer!=null)	g2.drawImage(theVisualizer.getLatestImage(), 0, 0, this);

	}

	public void componentHidden(ComponentEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void componentMoved(ComponentEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void componentResized(ComponentEvent arg0) {
		if(theVisualizer!=null)theVisualizer.notifyPanelSizeChange();
	}

	public void componentShown(ComponentEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void startVisualizing() {
		if(theVisualizer!=null)	theVisualizer.startVisualizing();
	}

	public void stopVisualizing() {
		if(theVisualizer!=null)	theVisualizer.stopVisualizing();
	}

	public void receiveNotificationVizChanged() {
		this.repaint();
	}


	public void notifyVisualizerLoaded(AbstractVisualizer theNewVisualizer) {
		if(this.theVisualizer!=null)this.theVisualizer.stopVisualizing();
		this.theVisualizer=theNewVisualizer;
		
		theVisualizer.setParentPanel(this);
		theVisualizer.notifyPanelSizeChange();
	}


	public void notifyVisualizerUnLoaded() {
			theVisualizer=null;
	}

	




}
