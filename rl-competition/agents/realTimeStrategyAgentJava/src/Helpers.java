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
import rlglue.types.Action;

public class Helpers
{
  public static Random RNG = new Random();
  
  public static String intArrayToString(int[] arr)
  {
    String s = "";
    
    for (int i : arr)
      s += (i + " ");
    
    s += "\n";
    
    return s;
  }
 
  public static Action convertActionList(ArrayList<Integer> actionList) 
  {
    Action action = new Action(actionList.size(), 0);
    
    for (int i = 0; i < actionList.size(); i++)
      action.intArray[i] = actionList.get(i);
    
    return action;
  }
  
  public static void addMoveAction(ArrayList<Integer> actionList, int objId, int x, int y, int max_speed)
  {
    actionList.add(objId);
    actionList.add(0);        // action id
    actionList.add(x);
    actionList.add(y);
    actionList.add(max_speed);
    actionList.add(-1);       // training type    
  }     
  
  public static void addBuildBaseAction(ArrayList<Integer> actionList, int objId)
  {
    actionList.add(objId);
    actionList.add(1);        // action id
    actionList.add(-1);
    actionList.add(-1);
    actionList.add(-1);
    actionList.add(-1);               
  }

  public static void addStopAction(ArrayList<Integer> actionList, int objId)
  {
    actionList.add(objId);
    actionList.add(2);        // action id
    actionList.add(-1);
    actionList.add(-1);
    actionList.add(-1);
    actionList.add(-1);               
  }

  public static void addTrainWorkerAction(ArrayList<Integer> actionList, int objId)
  {
    actionList.add(objId);
    actionList.add(3);        // action id
    actionList.add(-1);
    actionList.add(-1);
    actionList.add(-1);
    actionList.add(-1);               
  }

  public static void addTrainMarineAction(ArrayList<Integer> actionList, int objId)
  {
    actionList.add(objId);
    actionList.add(4);        // action id
    actionList.add(-1);
    actionList.add(-1);
    actionList.add(-1);
    actionList.add(-1);               
  }
  
  public static void addAttackAction(ArrayList<Integer> actionList, int objId, int targetId)
  {
    actionList.add(objId);
    actionList.add(5);        // action id
    actionList.add(-1);
    actionList.add(-1);
    actionList.add(-1);
    actionList.add(targetId);               
  }
  
}
