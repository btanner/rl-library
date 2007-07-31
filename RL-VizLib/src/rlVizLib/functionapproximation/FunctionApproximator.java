package rlVizLib.functionapproximation;

import rlglue.types.Observation;


public abstract class FunctionApproximator {

	public abstract void init();
	public abstract void start(Observation theObservation, int action);
	public abstract void step(Observation theObservation, double r, int action);
	public abstract void end(double r);
	public abstract void  update(Observation theObservation,int action, double delta);
	public abstract void  plan();
	public abstract double query(Observation theObservation, int theAction);
	
}
