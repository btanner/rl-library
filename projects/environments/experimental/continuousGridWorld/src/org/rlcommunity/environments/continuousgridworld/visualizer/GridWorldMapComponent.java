/*
 *  Copyright 2009 Brian Tanner.
 *
 *  brian@tannerpages.com
 *  http://research.tannerpages.com
 *
 *  This source file is from one of:
 *  {rl-coda,rl-glue,rl-library,bt-agentlib,rl-viz}.googlecode.com
 *  Check out http://rl-community.org for more information!
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
package org.rlcommunity.environments.continuousgridworld.visualizer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import org.rlcommunity.environments.continuousgridworld.map.BarrierRegion;
import org.rlcommunity.environments.continuousgridworld.State;
import org.rlcommunity.environments.continuousgridworld.map.TerminalRegion;
import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.types.Reward_observation_terminal;
import rlVizLib.visualization.SelfUpdatingVizComponent;
import rlVizLib.visualization.VizComponentChangeListener;

public class GridWorldMapComponent implements SelfUpdatingVizComponent, Observer {

    GridWorldVisualizerInterface theVisualizer;
    private VizComponentChangeListener theChangeListener;

    public GridWorldMapComponent(GridWorldVisualizerInterface theVisualizer) {
        this.theVisualizer = theVisualizer;
        theVisualizer.getTheGlueState().addObserver(this);
    }

    public void render(Graphics2D g) {
        State theState=theVisualizer.getState();
        Rectangle2D theWorldRect = new Rectangle2D.Double(0,0,100,100);

        AffineTransform theScaleTransform = new AffineTransform();
        theScaleTransform.scale(1.0d / theWorldRect.getWidth(), 1.0d / theWorldRect.getHeight());
        AffineTransform x = g.getTransform();
        x.concatenate(theScaleTransform);
        g.setTransform(x);


        Vector<TerminalRegion> resetRegions = theState.getResetRegions();

            g.setColor(Color.blue);
        for (TerminalRegion terminalState : resetRegions) {
            g.fill(terminalState.getShape());

        }


        Vector<BarrierRegion> barrierRegions = theState.getBarriers();

        for (BarrierRegion barrier : barrierRegions) {
            Color theColor = new Color((float) barrier.getPenalty(), 0, 0);
            g.setColor(theColor);
            g.fill(barrier.getShape());
        }

        
    }

    public void setVizComponentChangeListener(VizComponentChangeListener theChangeListener) {
        this.theChangeListener = theChangeListener;
    }

    public void update(Observable o, Object arg) {
        if (theChangeListener != null) {
            if (arg instanceof Observation) {
                theVisualizer.updateAgentState();
                theChangeListener.vizComponentChanged(this);
            }
            if (arg instanceof Reward_observation_terminal) {
                theVisualizer.updateAgentState();
                theChangeListener.vizComponentChanged(this);
            }
        }
    }
}
