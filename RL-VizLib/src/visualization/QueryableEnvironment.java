package visualization;

import rlglue.Observation;

public interface QueryableEnvironment {
	
	public double getMinValueForQuerableVariable(int dimension);
	public double getMaxValueForQuerableVariable(int dimension);
	public Observation getObservationForState(Observation theState);
	public int getNumVars();

}
