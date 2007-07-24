package visualization;
import java.awt.Graphics2D;


public interface VizComponent {
	public boolean update();
	public void render(Graphics2D g);
}
