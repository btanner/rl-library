package Tetrlais;

import rlglue.*;
import visualization.QueryableEnvironment;
import Environments.EnvironmentBase;

public class Tetrlais extends EnvironmentBase implements QueryableEnvironment{

	
	private int tet_global_score =0;
	private GameState gameState = new GameState(10,20);
	static final int terminal_score = -100;
	
	/*Base RL-Glue Functions*/
	public String env_init() {
		/* initialize the environment, construct a task_spec to pass on. The tetris environment
		 * has 200 binary observation variables and 1 action variable which ranges between 0 and 4. These are all 
		 * integer values. */
		/*NOTE: THE GAME STATE WIDTH AND HEIGHT MUST MULTIPLY TO EQUAL THE NUMBER OF OBSERVATION VARIABLES*/
		String task_spec = "2.0:e:200_[";
		for(int i = 0; i< 199; i++)
				task_spec = task_spec + "i, ";
		task_spec = task_spec + "i]";
		for(int i=0; i<200;i++)
			task_spec = task_spec + "_[0,1]";
		task_spec = task_spec + ":1_[i]_[0,4]";
		
		return task_spec;
	}

	public Observation env_start() {
		// TODO Auto-generated method stub
		
		gameState.reset();
		tet_global_score =0;
		
		Observation o = gameState.get_observation();
		/*System.out.println("Current Game");
		String line = null;
		for(int i = 20 -1; i>=0; --i){
			line = null;
			line = ""  +o.intArray[i*10];
				for(int j = 1; j<10; ++j )
					line += o.intArray[i*10 + j];
			System.out.println(line);
		}
		System.out.println();*/
		return o;
		
	}
	
	public Reward_observation env_step(Action action) {
		// TODO Auto-generated method stub
		Reward_observation ro = new Reward_observation();
		
		ro.terminal = (int) gameState.game_over();
		
		if(gameState.game_over() == 0)
		{
			gameState.take_action(action);
			gameState.update();
			ro.r = gameState.get_score() - tet_global_score;
			tet_global_score = gameState.get_score();
		}
		else{
			ro.r = Tetrlais.terminal_score;
			tet_global_score = 0;	
		}
		
		ro.o = gameState.get_observation();
		
		/*System.out.println("Current Game");
		String line = null;
		for(int i = 20 -1; i>=0; --i){
			line = null;
			line = ""  + ro.o.intArray[i*10];
				for(int j = 1; j<10; ++j )
					line += ro.o.intArray[i*10 + j];
			System.out.println(line);
		}
		System.out.println();*/
		return ro;
	}
	
	public void env_cleanup() {
		// TODO Auto-generated method stub
		
	}

	public Random_seed_key env_get_random_seed() {
		// TODO Auto-generated method stub
		System.out.println("The Tetris Environment does not implement env_get_random_seed. Sorry.");
		return null;
	}

	public State_key env_get_state() {
		// TODO Auto-generated method stub
		System.out.println("The Tetris Environment does not implement env_get_state. Sorry.");
		return null;
	}


	public String env_message(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public void env_set_random_seed(Random_seed_key arg0) {
		// TODO Auto-generated method stub
		
		System.out.println("The Tetris Environment does not implement env_set_random_seed. Sorry.");
		
	}

	public void env_set_state(State_key arg0) {
		// TODO Auto-generated method stub
		System.out.println("The Tetris Environment does not implement env_set_state. Sorry.");
	}
	/*End of Base RL-Glue Functions */
	
	
	/*RL-Viz Methods*/
	@Override
	protected Observation makeObservation() {
		// TODO Auto-generated method stu
		
		return gameState.get_observation();
	}

	public double getMaxValueForQuerableVariable(int dimension) {
		// TODO Auto-generated method stub
		return 0;
	}

	public double getMinValueForQuerableVariable(int dimension) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getNumVars() {
		// TODO Auto-generated method stub
		return 0;
	}

	public Observation getObservationForState(Observation theState) {
		// TODO Auto-generated method stub
		return null;
	}
	/*End of RL-Viz Methods*/

	/*Tetris Helper Functions*/

	/*End of Tetris Helper Functions*/
}
