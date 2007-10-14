/* Real Time Strategy Agent in Java for the RL Competition
* Copyright (C) 2007, Marc Lanctot
* 
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 2
* of the License, or (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA. */
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