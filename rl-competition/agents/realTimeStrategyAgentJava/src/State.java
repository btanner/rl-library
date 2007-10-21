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
import java.util.*; 
import rlglue.types.Observation;

public class State
{
  ArrayList<GameObj> objects;
  ArrayList<MineralPatch> mps;
  Parameters parms;
  
  // global info
  int minerals;
  
  public State(Parameters parms)
  {
    this.parms = parms;
    this.objects = new ArrayList<GameObj>(); 
    this.mps = new ArrayList<MineralPatch>();
  }
  
  public void reset()
  {
    objects.clear();
    objects.addAll(mps);
  }

  public void setMPs(String mpstr)
  {
    String[] coords = mpstr.split("-");

    for (int i = 0; i < coords.length; i++)
    {
      int x = Integer.parseInt(coords[i]);
      i++;
      int y = Integer.parseInt(coords[i]);

      MineralPatch mp = new MineralPatch(); 
      mp.x = x;
      mp.y = y;
      mp.radius = parms.mineral_patch_radius;
      mp.sight_range = parms.mineral_patch_sight_range;
      mp.hp = parms.mineral_patch_hp;
      mp.armor = parms.mineral_patch_armor;  
      mp.capacity = parms.mineral_patch_capacity; 
      
      mps.add(mp);
    }

    objects.addAll(mps);
  }
  
  public void applyObservation(Observation o)
  {
    int[] array = o.intArray;
    int length = o.intArray.length;

    //System.out.println("Obs length = " + length);
    
    minerals = array[0];
    
    int index = 1;
    
    while (index < length)
    {
      int type = array[index++];
      
      GameObj obj = null;      
      if (type == 0) obj = new Worker();
      else if (type == 1) obj = new Marine();
      else if (type == 2) obj = new Base();
      else if (type == 3) obj = new MineralPatch();
      
      // id
      obj.id = array[index++];
      
      // the rest of the stuff
      obj.owner = array[index++];
      obj.x = array[index++];
      obj.y = array[index++];
      obj.radius = array[index++];
      obj.sight_range = array[index++];
      obj.hp = array[index++];
      obj.armor = array[index++];
      
      if (type == 0) // worker
      {
        Worker worker = (Worker)obj;
        worker.max_speed = array[index++];
        worker.is_moving = array[index++];        
        worker.carried_minerals = array[index++];        
      }
      else if (type == 1) // marine
      {
        Marine marine = (Marine)obj;
        marine.max_speed = array[index++];
        marine.is_moving = array[index++];
        index++;
      }
      else
      {
        index++;
        index++;
        index++;
      }
      
      //System.out.println("Adding object " + obj);
      
      objects.add(obj);
    }
  }
}
