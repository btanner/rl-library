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

package org.rlcommunity.tetris.visualizer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import rlVizLib.visualization.VizComponent;

public class TetrisBlocksComponent implements VizComponent {
	private TetrisVisualizer tetVis = null;
	private int lastUpdateTimeStep=-1;

	public TetrisBlocksComponent(TetrisVisualizer ev){
		// TODO Write Constructor
		this.tetVis = ev;
	}

	public void render(Graphics2D g) {
		// TODO Auto-generated method stub
		Rectangle2D agentRect;
		int numCols = tetVis.getWidth();
		int numRows = tetVis.getHeight();
		int [] tempWorld = tetVis.getWorld();
                
                //Desired abstract block size
                int DABS=10;
                int scaleFactorX=numCols*DABS;
                int scaleFactorY=numRows*DABS;
                
		int w = DABS;
		int h = DABS;
		int x=0;
		int y = 0;
	    AffineTransform saveAT = g.getTransform();
	    g.setColor(Color.GRAY);
   	    g.scale(1.0d/(double)scaleFactorX,1.0d/(double)scaleFactorY);

		for(int i= 0; i<numRows; i++){
		for(int j=0; j<numCols; j++){
			x = j*DABS;
			y = i*DABS;
                        int thisBlockColor=tempWorld[i*numCols+j];
			if(thisBlockColor!=0){
                                switch(thisBlockColor){
                                case 1:
                                    g.setColor(Color.PINK);
                                    break;
                                case 2:
                                    g.setColor(Color.RED);
                                    break;
                                case 3:
                                    g.setColor(Color.GREEN);
                                    break;
                                case 4:
                                    g.setColor(Color.YELLOW);
                                    break;
                                case 5:
                                    g.setColor(Color.LIGHT_GRAY);
                                    break;
                                case 6:
                                    g.setColor(Color.ORANGE);
                                    break;
                                case 7:
                                    g.setColor(Color.MAGENTA);
                                    break;
                                        
                                }
				g.fill3DRect(x, y, w, h, true);
			}
			else{
				g.setColor(Color.WHITE);
				agentRect = new Rectangle2D.Double(x, y, w, h);	
                                if(tetVis.printGrid())
                                    g.fill3DRect(x,y,w,h,true);
                                else
                                    g.fill(agentRect);
			}
		}
	}
            g.setColor(Color.GRAY);
   	    g.drawRect(0,0,DABS*numCols,DABS*numRows);
	    g.setTransform(saveAT);
	}

	public boolean update() {
		int currentTimeStep=tetVis.getGlueState().getTotalSteps();
                 //Basically if load got called, then we should force a viz update
		if(currentTimeStep!=lastUpdateTimeStep||tetVis.getForceBlocksRefresh()){
			tetVis.updateAgentState(tetVis.getForceBlocksRefresh());
			lastUpdateTimeStep=currentTimeStep;
                        tetVis.setForceBlocksRefresh(false);
		return false;
		}
	return true;
	}

}
