package RandomAgent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

import rlVizLib.visualization.QueryableAgent;
import rlglue.agent.Agent;
import rlglue.types.Action;
import rlglue.types.Observation;

public class RandomAgent implements Agent, QueryableAgent {
	private Action action;
	private int numInts =1;
	private int numDoubles =0;
	private Random random = new Random();
	private InputStreamReader stdin = new InputStreamReader(System.in);
	private BufferedReader console = new BufferedReader(stdin);
	
	public void RandomAgent(){
	}

	public void agent_cleanup() {
		// TODO Auto-generated method stub
		
	}

	public void agent_end(double arg0) {
		// TODO Auto-generated method stub
		
	}

	public void agent_freeze() {
		// TODO Auto-generated method stub
		
	}

	public void agent_init(String arg0) {
		// TODO Auto-generated method stub
		action = new Action(numInts, numDoubles);	
	}

	public String agent_message(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Action agent_start(Observation o) {
		// TODO Auto-generated method stub
		randomify(action);
		//ask(o,action);
		return action;
	}

	public Action agent_step(double arg0, Observation o) {
		// TODO Auto-generated method stub
		randomify(action);
		//ask(o,action);
		return action;
	}

	public double getValueForState(Observation theObservation) {
		// TODO Auto-generated method stub
		return 0;
	}

	private void randomify(Action action){
		action.intArray[0] = (int)(random.nextInt(5));
	}
	
	private void ask(Observation obs,Action action) {
		
		System.out.print("Enter Action 0-4 ");
		String s1;
		try {
			s1 = console.readLine();
			int i1 = Integer.parseInt(s1);
			if((i1>=0)|| (i1<5))
				action.intArray[0] = i1;	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			action.intArray[0] =4;
		}
		
	}
}
