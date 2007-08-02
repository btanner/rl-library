package rlViz;

import rlVizLib.visualization.EnvVisualizer;
import visualizers.mountainCar.MountainCarVisualizer;
import visualizers.tetrlais.TetrlaisVisualizer;

public class EnvVisualizerFactory {
	
	public static EnvVisualizer createVisualizerFromString(String theEnvName){
		//So this is where we have to hard code things
		EnvVisualizer theEnvVisualizer=null;
		
		if(theEnvName.equalsIgnoreCase("mountaincar")){
			theEnvVisualizer=new MountainCarVisualizer();
		}
		if(theEnvName.equalsIgnoreCase("tetrlais")){
			theEnvVisualizer=new TetrlaisVisualizer();
		}
		
		return theEnvVisualizer;
	}

}
