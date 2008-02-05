/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package visualizers.Generic;

import rlVizLib.general.TinyGlue;
import rlVizLib.visualization.AbstractVisualizer;
import rlVizLib.visualization.VizComponent;
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
		VizComponent actionPrinter= new GenericActionComponent(glueState);
		addVizComponentAtPositionWithSize(actionPrinter,0,0,1.0,1.0);
	}

    @Override
        public String getName(){return "Default Agent Visualizer";}

}
