package org.rlcommunity.visualizers.generic;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import rlVizLib.general.TinyGlue;
import rlVizLib.visualization.VizComponent;
import rlglue.types.Observation;

public class GenericVariableValueComponent implements VizComponent{
	TinyGlue theGlueState=null;

	public GenericVariableValueComponent(TinyGlue theGlueState){
		this.theGlueState=theGlueState;
	}

	public void render(Graphics2D g) {
		//This is some hacky stuff, someone better than me should clean it up
		Font f = new Font("Verdana",0,8);     
		g.setFont(f);
		//SET COLOR
		g.setColor(Color.BLACK);
		//DRAW STRING
		AffineTransform saveAT = g.getTransform();
		g.scale(.005, .005);
		
		float currentHeight=10.0f;
		float heightIncrement=10.0f;
		g.drawString("E/S/T: " +theGlueState.getEpisodeNumber()+"/"+theGlueState.getTimeStep()+"/"+theGlueState.getTotalSteps(),0.0f, currentHeight);

		currentHeight+=heightIncrement;
		
//		Do int observation variables	    
		StringBuffer obsStringBuffer=new StringBuffer("Obs Ints: ");
		Observation lastObservation=theGlueState.getLastObservation();
		if(lastObservation!=null){
			for(int i=0;i<lastObservation.intArray.length;i++){
				obsStringBuffer.append(lastObservation.intArray[i]);
				obsStringBuffer.append('\t');
			}
			g.drawString(obsStringBuffer.toString(),0.0f,currentHeight);
			currentHeight+=heightIncrement;

			//Do double variables
			g.drawString("Obs Doubles",0.0f,currentHeight);
			currentHeight+=heightIncrement;
			for(int i=0;i<lastObservation.doubleArray.length;i++){
				g.drawString(""+lastObservation.doubleArray[i],0.0f,currentHeight);
				currentHeight+=heightIncrement;
			}
		}

		g.drawString("Reward: "+theGlueState.getLastReward(),0.0f,currentHeight);

		g.setTransform(saveAT);
	}

	public boolean update() {
		return true;


	}


}
