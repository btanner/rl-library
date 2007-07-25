package interfaces;
import java.util.Vector;

import rlglue.Observation;


public interface ValueFunctionDataProvider {
	
	public double getMinValueForDim(int whichDimension);
	public double getMaxValueForDim(int whichDimension);
	public Vector<Observation> getQueryObservations(Vector<Observation> theQueryStates);
	public Vector<Double> queryAgentValues(Vector<Observation> theQueryObs);
	public double getValueFunctionResolution();	
}
