import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;


public class AgentOnValueFunctionVizComponent implements VizComponent {
private AgentOnValueFunctionDataProvider dataProvider=null;
	
	public AgentOnValueFunctionVizComponent(AgentOnValueFunctionDataProvider dataProvider){
		this.dataProvider=dataProvider;
	}

	public void render(Graphics2D g) {
		g.setColor(Color.BLUE);

		double dim1 = dataProvider.getCurrentStateInDimension(0);
		double dim2   = dataProvider.getCurrentStateInDimension(1);
		
		Point2D queryState=new Point2D.Double(dim1,dim2);
		
		Point2D agentOnVFLocation=dataProvider.getWindowLocationForQueryPoint(queryState);
		
		g.fillRect((int)agentOnVFLocation.getX(),(int)agentOnVFLocation.getY(),5,5);
	}

	public boolean update() {
		dataProvider.updateAgentState();
		return true;
	}

}
