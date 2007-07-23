import java.awt.geom.Point2D;


public interface AgentOnValueFunctionDataProvider {
	public double getCurrentStateInDimension(int whichDimension);
	public Point2D getWindowLocationForQueryPoint(Point2D stateValues);
	public void updateAgentState();
}
