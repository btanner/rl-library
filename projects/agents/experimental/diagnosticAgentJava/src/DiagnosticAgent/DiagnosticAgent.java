/* Diagnostic Agent that works in all domains and prints out messages for every method call
* Copyright (C) 2007, Brian Tanner brian@tannerpages.com (http://brian.tannerpages.com/)
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
package DiagnosticAgent;

import java.util.Random;

import rlVizLib.utilities.TaskSpecObject;

import org.rlcommunity.rlglue.codec.AgentInterface;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;

public class DiagnosticAgent implements AgentInterface {
	private Action action;
	private int numInts =1;
	private int numDoubles =0;
	private Random random = new Random();

        TaskSpecObject TSO=null;
	

        
   public DiagnosticAgent(){
		System.out.println("JAVA::DiagnosticAgent\tDiagnosticAgent()");
   }

	public void agent_cleanup() {
		System.out.println("JAVA::DiagnosticAgent\t\tagent_cleanup()");
	}

	public void agent_end(double arg0) {
		System.out.println("JAVA::DiagnosticAgent\t\t\tagent_end(), reward: "+arg0);
	}

	public void agent_freeze() {
		System.out.println("JAVA::DiagnosticAgent\t\t\t\tagent_freeze()");
	}

	public void agent_init(String taskSpec) {
		System.out.println("JAVA::DiagnosticAgent\t\tagent_init(), taskspec: "+taskSpec);
            TSO = new TaskSpecObject(taskSpec);

            action = new Action(TSO.num_discrete_action_dims,TSO.num_continuous_action_dims);	
	}

	public String agent_message(String arg0) {
		System.out.println("JAVA::DiagnosticAgent\t\tagent_message(), message: "+arg0);
        return null;
	}

	public Action agent_start(Observation o) {
		System.out.println("JAVA::DiagnosticAgent\t\t\tagent_start()");
            randomify(action);
            return action;
	}

	public Action agent_step(double reward, Observation o) {
		System.out.printf("JAVA::DiagnosticAgent\t\t\t\tagent_step(), Obs %.3f %.3f %d reward: %.3f \n",o.doubleArray[0], o.doubleArray[1], o.intArray[0],reward);
            randomify(action);
            return action;
	}

	private void randomify(Action action){
            for(int i=0;i<TSO.num_discrete_action_dims;i++){
                action.intArray[i]=random.nextInt(((int)TSO.action_maxs[i]+1)-(int)TSO.action_mins[i]) + ((int)TSO.action_mins[i]);
            }
            for(int i=0;i<TSO.num_continuous_action_dims;i++){
                action.doubleArray[i]=random.nextDouble()*(TSO.action_maxs[i] - TSO.action_mins[i]) + TSO.action_mins[i];
            }
       	}
	


}
