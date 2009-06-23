/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.rlcommunity.environments.continuousgridworld.visualizer;

import java.util.Vector;
import org.rlcommunity.environments.continuousgridworld.BarrierRegion;
import org.rlcommunity.environments.continuousgridworld.State;
import rlVizLib.visualization.interfaces.AgentOnValueFunctionDataProvider;
import rlVizLib.visualization.interfaces.GlueStateProvider;
import rlVizLib.visualization.interfaces.ValueFunctionDataProvider;

/**
 *
 * @author btanner
 */
public interface GridWorldVisualizerInterface extends AgentOnValueFunctionDataProvider, GlueStateProvider, ValueFunctionDataProvider {

    State getState();
    void updateAgentState();


}
