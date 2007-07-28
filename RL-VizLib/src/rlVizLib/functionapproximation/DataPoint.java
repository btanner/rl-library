package rlVizLib.functionapproximation;

import rlglue.Observation;

public class DataPoint {
	public Observation s=null;
	public Observation sprime=null;
	public int action=0;
	public double reward=0;;

	public DataPoint(Observation s, int action, double reward, Observation sprime){
		this.s=s;
		this.action=action;
		this.reward=reward;
		this.sprime=sprime;
	}
}
