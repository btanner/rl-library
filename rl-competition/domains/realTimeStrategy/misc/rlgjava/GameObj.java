
public abstract class GameObj
{
  int id;
  
  int owner;
  
  int x, y, radius, sight_range, hp, armor;
  
  public abstract String getType();
  
  public String toString() 
  { return getType()+" id="+id+" owner="+owner+" x,y="+x+","+y+" r="+radius+" sr="+sight_range+
                     " hp="+hp+" armor="+armor;
  }
}

class Base extends GameObj
{
  public String getType() { return "base"; }
}

class MineralPatch extends GameObj
{
  public String getType() { return "mineral_patch"; }
}

abstract class MobileObj extends GameObj 
{
  int max_speed;
  int is_moving; // 1 = true
  
  public String toString() {
    return super.toString() + " ms=" + max_speed + " im=" + is_moving;
  }
}

class Worker extends MobileObj
{
  int carried_minerals;
  
  public String getType() { return "worker"; }  
}

class Marine extends MobileObj
{
  public String getType() { return "marine"; }  
}