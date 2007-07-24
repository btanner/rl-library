import java.awt.Color;
import java.awt.Graphics2D;

import visualization.VizComponent;


public class RedBoxVizComponent implements VizComponent {

	int count=0;
	
	public void render(Graphics2D g) {
		count++;
		System.out.println("Render on red box called: "+count+" times");
		
		g.setColor(Color.RED);
		
		g.fillRect(0, 0, 100, 100);
	}

	public boolean update() {
		return true;
	}

}
