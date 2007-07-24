package visualization;
import interfaces.AgentOnValueFunctionDataProvider;
import utilities.UtilityShop;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;


public class AgentOnValueFunctionVizComponent implements VizComponent {
	private AgentOnValueFunctionDataProvider dataProvider=null;

	public AgentOnValueFunctionVizComponent(AgentOnValueFunctionDataProvider dataProvider){
		this.dataProvider=dataProvider;
	}

	public void render(Graphics2D g) {
		g.setColor(Color.BLUE);

		double transX=UtilityShop.normalizeValue( dataProvider.getCurrentStateInDimension(0),
				dataProvider.getMinValueForDim(0),
				dataProvider.getMaxValueForDim(0));

		double transY=UtilityShop.normalizeValue( dataProvider.getCurrentStateInDimension(1),
				dataProvider.getMinValueForDim(1),
				dataProvider.getMaxValueForDim(1));


		Rectangle2D agentRect=new Rectangle2D.Double(transX,transY,.02,.02);
		g.fill(agentRect);
	}

	public boolean update() {
		dataProvider.updateAgentState();
		return true;
	}

}
