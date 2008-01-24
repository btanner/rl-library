/* RL-Viz Visualizer for Mountain Car Domain
* Copyright (C) 2007, Brian Tanner brian@tannerpages.com (http://brian.tannerpages.com/)
* 
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 2
* of the License, or (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA. */
package org.rlcommunity.mountaincar.visualizer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.Vector;

import rlVizLib.utilities.UtilityShop;
import rlVizLib.visualization.VizComponent;

public class MountainVizComponent implements VizComponent {
	private MountainCarVisualizer theVizualizer=null;

	Vector<Double> theHeights=null;

        
	double theGoalPosition=0.0d;
	double theGoalHeight=0.0d;
	
        int lastDrawStep=-1;
	public MountainVizComponent(MountainCarVisualizer theVizualizer){
		this.theVizualizer=theVizualizer;
	}
	public void render(Graphics2D g) {
		AffineTransform theScaleTransform=new AffineTransform();

                //Draw a rectangle
            g.setColor(Color.WHITE);
   	    g.fillRect(0,0,1,1);

                double scaleFactor=1000.0d;
		theScaleTransform.scale(1.0d/scaleFactor,1.0d/scaleFactor);
		AffineTransform origTransform=g.getTransform();
		
		AffineTransform x = g.getTransform();
		x.concatenate(theScaleTransform);
		g.setTransform(x);

		
		theHeights = theVizualizer.getSampleHeights();

		double sizeEachComponent=1.0/(double)theHeights.size();
                    

		g.setColor(Color.BLACK);

		double lastX=0.0d;
		double lastY=1.0-UtilityShop.normalizeValue(theHeights.get(0),theVizualizer.getMinHeight(),theVizualizer.getMaxHeight());
		for(int i=1;i<theHeights.size();i++){
			double thisX=lastX+sizeEachComponent;
			double thisY=1.0-UtilityShop.normalizeValue(theHeights.get(i),theVizualizer.getMinHeight(),theVizualizer.getMaxHeight());
			
			Line2D thisLine=new Line2D.Double(scaleFactor*lastX, scaleFactor*lastY,scaleFactor* thisX,scaleFactor*thisY);
			
			lastX=thisX;
			lastY=thisY;

			g.draw(thisLine);
		}

		
		
		
		g.setTransform(origTransform);

		g.setColor(Color.GREEN);

		//to bring things back into the window
		double minPosition=theVizualizer.getMinValueForDim(0);	
		double maxPosition=theVizualizer.getMaxValueForDim(0);	
		
		
		double transX = UtilityShop.normalizeValue(theGoalPosition,minPosition,maxPosition);
		//need to get he actual height ranges
		double transY = UtilityShop.normalizeValue(
				theGoalHeight,
				theVizualizer.getMinHeight(),
				theVizualizer.getMaxHeight()
				);
		transY= (1.0-transY);
		
		double rectWidth=.05/4;
		double rectHeight=0.1;
		Rectangle2D fillRect=new Rectangle2D.Double(transX-rectWidth/2.0d,transY-rectHeight/2.0d,rectWidth,rectHeight);
		g.fill(fillRect);
	}

	public boolean update() {
                
                int currentTimeStep=theVizualizer.getTheGlueState().getTotalSteps();
                
                if(currentTimeStep>lastDrawStep){
                    lastDrawStep=currentTimeStep;
		//Get the Goal Position and height
			theGoalPosition=theVizualizer.getGoalPosition();

			Vector<Double> tempVec = new Vector<Double>();
			tempVec.add(theGoalPosition);
                        
                        Vector<Double> returnVector=theVizualizer.getHeightsForPositions(tempVec);
			theGoalHeight = returnVector.get(0);
		//This just draws every time... we should fix it so that it only draws after the screen is resized (And the first time)
			return true;
		}
		return false;
		

		
	}

}
