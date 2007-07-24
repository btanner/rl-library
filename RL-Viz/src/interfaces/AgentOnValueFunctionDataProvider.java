package interfaces;


public interface AgentOnValueFunctionDataProvider {
	public double getCurrentStateInDimension(int whichDimension);
	public double getMinValueForDim(int whichDimension);
	public double getMaxValueForDim(int whichDimension);
	public void updateAgentState();
}
