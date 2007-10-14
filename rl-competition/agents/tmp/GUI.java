
import javax.swing.*;
import java.awt.*;

public class GUI extends JFrame
{
  Parameters parms;
  State state;
  
  Color bgColor;    
  
  int myId;
  
  public GUI(Parameters parms, State state)
  {
    this.parms = parms;
    this.state = state;
    
    myId = 1;
    
    GUIPanel guiPanel = new GUIPanel(parms, state, this);
    guiPanel.setVisible(false);
    
    bgColor = new Color((float)0.6, (float)0.3, (float)0.3);
    setBackground(bgColor);

    add(guiPanel);
    guiPanel.setVisible(true);
  } 
}

class Coord 
{
  int x, y; 
}

class GUIPanel extends JPanel
{
  Parameters parms;
  State state;
  GUI parent;
  
  
  double sf; // scale factor (coordinates -> pixels)
  
  public GUIPanel(Parameters parms, State state, GUI parent)
  {
    this.parms = parms;
    this.state = state;
    this.parent = parent;
    
    sf = 0.9; 
   
    Coord c1 = scale(parms.width + 5, parms.height + 10);
    Coord c2 = scale(parms.width, parms.height);
    
    parent.setSize(c1.x, c1.y);
    setSize(c2.x, c2.y);
    
    setBackground(parent.bgColor);
  } 

  private Coord scale(int x, int y)
  {
    Coord c = new Coord();
    c.x = (int)(sf*x);
    c.y = (int)(sf*y);
    return c;
  }
  
  private int scale(int num) {
    return (int)(sf*num);
  }
  
  private void fillCircle(Graphics g, Color c, int x, int y, int radius)
  {
    g.setColor(c);
    
    int xp = scale(x);
    int yp = scale(y);
    int rp = scale(radius);
    
    int topleft_x = xp - rp;
    int topleft_y = yp - rp;
        
    g.fillOval(topleft_x, topleft_y, 2*rp, 2*rp);
  }

  private void drawCircle(Graphics g, Color c, int x, int y, int radius)
  {
    g.setColor(c);
    
    int xp = scale(x);
    int yp = scale(y);
    int rp = scale(radius);
    
    int topleft_x = xp - rp;
    int topleft_y = yp - rp;
        
    g.drawOval(topleft_x, topleft_y, 2*rp, 2*rp);
  }
  
  private double getSf(GameObj obj)
  {
    if (obj.getType().equals("worker")) 
      return (((double)obj.hp) / ((double)parms.worker_hp));
    else if (obj.getType().equals("marine")) 
      return (((double)obj.hp) / ((double)parms.marine_hp));
    else if (obj.getType().equals("base")) 
      return (((double)obj.hp) / ((double)parms.base_hp));
    else
      return 1.0;
  }
  
  private void drawObject(Graphics g, GameObj obj)
  {
    Color c = null; 
    
    if (obj.hp <= 0)  // obj is dead :(
      return;         // don't draw it
        
    if (obj.owner == parent.myId)
      c = Color.RED;
    else if (obj.owner == 2)
      c = Color.BLUE;
    else
      c = Color.GREEN;
    
    double hpsf = getSf(obj);  // scale by HP    
    int re = c.getRed(), gr = c.getGreen(), bl = c.getBlue();
    re = (re == 0 ? 0 : 50 + (int)(hpsf*(re - 50)));
    gr = (gr == 0 ? 0 : 50 + (int)(hpsf*(gr - 50)));
    bl = (bl == 0 ? 0 : 50 + (int)(hpsf*(bl - 50)));    
    c = new Color(re, gr, bl);
    
    if (obj.getType().equals("worker"))
      drawCircle(g, c, obj.x, obj.y, obj.radius);        
    else 
      fillCircle(g, c, obj.x, obj.y, obj.radius);        
  }
  
  public void paint(Graphics g)
  {
    setBackground(parent.bgColor);
    
    //g.drawOval(20,10,10,10);
    
    // draw visibilities first    
    for (GameObj obj : state.objects)
    {    
      if (obj.owner == parent.myId)
        if (obj.hp > 0)
          fillCircle(g, Color.BLACK, obj.x, obj.y, obj.sight_range);      
    }
    
    // mineral patches and bases next
    for (GameObj obj : state.objects)
    {
      if (obj.getType().equals("mineral_patch"))
        drawObject(g, obj);
    }

    for (GameObj obj : state.objects)
    {
      if (obj.getType().equals("base"))
        drawObject(g, obj);
    }
    
    // workers and marines
    for (GameObj obj : state.objects)
    {
      if (obj.getType().equals("worker"))
        drawObject(g, obj);
    }

    for (GameObj obj : state.objects)
    {
      if (obj.getType().equals("marine"))
        drawObject(g, obj);
    }
    
  }
}
