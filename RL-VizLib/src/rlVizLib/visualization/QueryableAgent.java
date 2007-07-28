package rlVizLib.visualization;

import rlglue.Observation;
public interface QueryableAgent {
	public double getValueForState(Observation theObservation);
}
