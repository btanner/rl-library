/*
Copyright 2007 Brian Tanner
http://rl-library.googlecode.com/
brian@tannerpages.com
http://brian.tannerpages.com

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
*/

package org.rlcommunity.environments.mountaincar.visualizer;

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
