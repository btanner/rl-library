package org.rlcommunity.visualizers.generic;

import rlVizLib.general.TinyGlue;
import rlVizLib.visualization.AbstractVisualizer;
import rlVizLib.visualization.VizComponent;
import rlVizLib.visualization.interfaces.DynamicControlTarget;

public class GenericEnvVisualizer  extends AbstractVisualizer {
TinyGlue glueState=null;
DynamicControlTarget theControlTarget=null;

	public GenericEnvVisualizer(TinyGlue glueState, DynamicControlTarget theControlTarget){
		super();
		this.glueState=glueState;
                this.theControlTarget=theControlTarget;
		VizComponent variablePrinter= new GenericVariableValueComponent(glueState);
		addVizComponentAtPositionWithSize(variablePrinter,0,0,1.0,1.0);
	}

    @Override
        public String getName(){return "Default Env Visualizer";}
}
