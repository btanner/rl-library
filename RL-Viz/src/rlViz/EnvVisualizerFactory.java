package rlViz;

import rlVizLib.visualization.AbstractVisualizer;
import visualizers.mountainCar.MountainCarVisualizer;
import visualizers.tetrlais.TetrlaisVisualizer;

public class EnvVisualizerFactory {
	
	public static AbstractVisualizer createVisualizerFromString(String theEnvName){
		//So this is where we have to hard code things
		AbstractVisualizer theEnvVisualizer=null;
		
		if(theEnvName.equalsIgnoreCase("mountaincar")){
			theEnvVisualizer=new MountainCarVisualizer();
		}
		if(theEnvName.equalsIgnoreCase("tetrlais")){
			theEnvVisualizer=new TetrlaisVisualizer();
		}
		
		return theEnvVisualizer;
	}

}
