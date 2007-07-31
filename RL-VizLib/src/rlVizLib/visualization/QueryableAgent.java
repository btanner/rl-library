package rlVizLib.visualization;

import rlglue.types.Observation;

public interface QueryableAgent {
	public double getValueForState(Observation theObservation);
}
