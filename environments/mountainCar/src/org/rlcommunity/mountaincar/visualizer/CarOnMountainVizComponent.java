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
import java.awt.geom.Rectangle2D;

import rlVizLib.messaging.environment.EpisodeSummaryRequest;
import rlVizLib.messaging.environment.EpisodeSummaryResponse;
import rlVizLib.utilities.UtilityShop;
import rlVizLib.visualization.VizComponent;


public class CarOnMountainVizComponent implements VizComponent {
	private MountainCarVisualizer mcv = null;

	int lastUpdateTimeStep=-1;



	public CarOnMountainVizComponent(MountainCarVisualizer mc){
		this.mcv = mc;

	}


	public void render(Graphics2D g) {

		g.setColor(Color.RED);

		//to bring things back into the window
		double minPosition=mcv.getMinValueForDim(0);	
		double maxPosition=mcv.getMaxValueForDim(0);	

		double transX = UtilityShop.normalizeValue( this.mcv.getCurrentStateInDimension(0),minPosition,maxPosition);

		//need to get he actual height ranges
		double transY = UtilityShop.normalizeValue(
				this.mcv.getHeight(),
				mcv.getMinHeight(),
				mcv.getMaxHeight()
		);
		transY= (1.0-transY);

		double rectWidth=.05;
		double rectHeight=.05;
		Rectangle2D fillRect=new Rectangle2D.Double(transX-rectWidth/2.0d,transY-rectHeight/2.0d,rectWidth,rectHeight);
		g.fill(fillRect);
                
	}

	public boolean update() {
		int currentTimeStep=mcv.getTheGlueState().getTotalSteps();

		if(currentTimeStep!=lastUpdateTimeStep||mcv.getForceDrawRefresh()){
			mcv.updateAgentState(mcv.getForceDrawRefresh());
                        mcv.setForceDrawRefresh(false);
			lastUpdateTimeStep=currentTimeStep;
			return true;
		}
		return false;
	}

}
