/* Tetris Domain
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
package org.rlcommunity.tetris.visualizer;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import rlVizLib.visualization.VizComponent;

public class TetrisScoreComponent implements VizComponent{
	private TetrisVisualizer tetVis = null;
	
	int lastScore=0;

	private int lastUpdateTimeStep=-1;

	public TetrisScoreComponent(TetrisVisualizer ev){
		this.tetVis = ev;
		lastScore=-1;
	}

	public void render(Graphics2D g) {
		//This is some hacky stuff, someone better than me should clean it up
		Font f = new Font("Verdana",0,8);     
		g.setFont(f);
	    //SET COLOR
	    g.setColor(Color.BLACK);
	    //DRAW STRING
	    AffineTransform saveAT = g.getTransform();
   	    g.scale(.01, .01);
	    g.drawString("Lines: " +tetVis.getScore(),0.0f, 10.0f);
	    g.drawString("E/S/T: " +tetVis.getEpisodeNumber()+"/"+tetVis.getTimeStep()+"/"+tetVis.getTotalSteps(),0.0f, 20.0f);
	    g.drawString("CurrentPiece: " + tetVis.getCurrentPiece(), 0.0f, 30.0f);
	    g.setTransform(saveAT);
	}

	public boolean update() {
int currentTimeStep=tetVis.getGlueState().getTotalSteps();

		if(currentTimeStep!=lastUpdateTimeStep){
			tetVis.updateAgentState(false);
			lastUpdateTimeStep=currentTimeStep;
			return true;
		}
		return false;
	}
	
	
}
