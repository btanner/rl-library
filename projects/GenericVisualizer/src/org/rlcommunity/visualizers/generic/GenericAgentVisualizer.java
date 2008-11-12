/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.rlcommunity.visualizers.generic;

import rlVizLib.general.TinyGlue;
import rlVizLib.visualization.AbstractVisualizer;
import rlVizLib.visualization.SelfUpdatingVizComponent;
import rlVizLib.visualization.interfaces.DynamicControlTarget;

/**
 *
 * @author btanner
 */
public class GenericAgentVisualizer extends AbstractVisualizer {
TinyGlue glueState=null;
DynamicControlTarget theControlTarget=null;

	public GenericAgentVisualizer(TinyGlue glueState, DynamicControlTarget theControlTarget){
		super();
		this.glueState=glueState;
                this.theControlTarget=theControlTarget;
		SelfUpdatingVizComponent actionPrinter= new GenericActionComponent(glueState);
		addVizComponentAtPositionWithSize(actionPrinter,0,0,1.0,1.0);
	}

    @Override
        public String getName(){return "Default Agent Visualizer";}

}
