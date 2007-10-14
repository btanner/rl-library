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
public class Parameters
{
  int width, height;
  
  int base_radius, base_sight_range, base_hp, base_armor, base_cost, 
      base_build_time;
  
  int marine_radius, marine_sight_range, marine_hp, marine_armor, marine_max_speed, 
      marine_cost, marine_training_time, marine_attack_value, marine_attack_range;
    
  int worker_radius, worker_sight_range, worker_hp, worker_armor, worker_max_speed,
      worker_cost, worker_training_time, worker_mining_time, worker_mineral_capacity;

  int mineral_patch_radius, mineral_patch_sight_range, mineral_patch_hp,
      mineral_patch_armor, mineral_patch_capacity;
  
  public Parameters() 
  {
  }
  
  public void parseTaskSpec(String spec)
  {
    String[] keyvalPairs = spec.split(",");
    
    for (String s : keyvalPairs) {
      String[] keyval = s.split("=");
      
      String key = keyval[0];
      String val = keyval[1];

      if       (key.equals("width"))                           width = Integer.parseInt(val);
      else if  (key.equals("height"))                          height = Integer.parseInt(val);               
      else if  (key.equals("base_radius"))                     base_radius = Integer.parseInt(val);
      else if  (key.equals("base_sight_range"))                base_sight_range = Integer.parseInt(val);               
      else if  (key.equals("base_hp"))                         base_hp = Integer.parseInt(val);               
      else if  (key.equals("base_armor"))                      base_armor = Integer.parseInt(val);               
      else if  (key.equals("base_cost"))                       base_cost = Integer.parseInt(val);               
      else if  (key.equals("base_build_time"))                 base_build_time = Integer.parseInt(val);               
      else if  (key.equals("marine_radius"))                   marine_radius = Integer.parseInt(val);               
      else if  (key.equals("marine_sight_range"))              marine_sight_range = Integer.parseInt(val);               
      else if  (key.equals("marine_hp"))                       marine_hp = Integer.parseInt(val);               
      else if  (key.equals("marine_armor"))                    marine_armor = Integer.parseInt(val);               
      else if  (key.equals("marine_max_speed"))                marine_max_speed = Integer.parseInt(val);               
      else if  (key.equals("marine_cost"))                     marine_cost = Integer.parseInt(val);               
      else if  (key.equals("marine_training_time"))            marine_training_time = Integer.parseInt(val);               
      else if  (key.equals("marine_attack_value"))             marine_attack_value = Integer.parseInt(val);               
      else if  (key.equals("marine_attack_range"))             marine_attack_range = Integer.parseInt(val);               
      else if  (key.equals("worker_radius"))                   worker_radius = Integer.parseInt(val);               
      else if  (key.equals("worker_sight_range"))              worker_sight_range = Integer.parseInt(val);               
      else if  (key.equals("worker_hp"))                       worker_hp = Integer.parseInt(val);               
      else if  (key.equals("worker_armor"))                    worker_armor = Integer.parseInt(val);               
      else if  (key.equals("worker_max_speed"))                worker_max_speed = Integer.parseInt(val);               
      else if  (key.equals("worker_cost"))                     worker_cost = Integer.parseInt(val);               
      else if  (key.equals("worker_training_time"))            worker_training_time = Integer.parseInt(val);               
      else if  (key.equals("worker_mining_time"))              worker_mining_time = Integer.parseInt(val);               
      else if  (key.equals("worker_mineral_capacity"))         worker_mineral_capacity = Integer.parseInt(val);               
      else if  (key.equals("mineral_patch_radius"))            mineral_patch_radius = Integer.parseInt(val);               
      else if  (key.equals("mineral_patch_sight_range"))       mineral_patch_sight_range = Integer.parseInt(val);               
      else if  (key.equals("mineral_patch_hp"))                mineral_patch_hp = Integer.parseInt(val);               
      else if  (key.equals("mineral_patch_armor"))             mineral_patch_armor = Integer.parseInt(val);               
      else if  (key.equals("mineral_patch_capacity"))          mineral_patch_capacity = Integer.parseInt(val);               
    }
  }
}