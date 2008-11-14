/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.rlcommunity.agents.keyboard.visualizer;

import rlVizLib.general.TinyGlue;
import rlVizLib.visualization.AbstractVisualizer;
import rlVizLib.visualization.SelfUpdatingVizComponent;
import rlVizLib.visualization.interfaces.DynamicControlTarget;

/**
 *
 * @author btanner
 */
public class KeyboardAgentVisualizer extends AbstractVisualizer {
TinyGlue glueState=null;
DynamicControlTarget theControlTarget=null;

	public KeyboardAgentVisualizer(TinyGlue glueState, DynamicControlTarget theControlTarget){
		super();
		this.glueState=glueState;
                this.theControlTarget=theControlTarget;
		SelfUpdatingVizComponent keyVizComponent= new KeyboardActionVizComponent(glueState,theControlTarget);
		addVizComponentAtPositionWithSize(keyVizComponent,0,0,1.0,1.0);
	}

    @Override
        public String getName(){return "Default Agent Visualizer";}

}
