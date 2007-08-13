package Tetrlais;

import java.util.Vector;

import messages.TetrlaisStateResponse;
import rlVizLib.Environments.EnvironmentBase;
import rlVizLib.general.ParameterHolder;
import rlVizLib.general.RLVizVersion;
import rlVizLib.messaging.environment.EnvironmentMessageParser;
import rlVizLib.messaging.environment.EnvironmentMessages;
import rlVizLib.messaging.interfaces.RLVizEnvInterface;
import rlglue.types.Action;
import rlglue.types.Observation;
import rlglue.types.Random_seed_key;
import rlglue.types.Reward_observation;
import rlglue.types.State_key;

public class Tetrlais extends EnvironmentBase implements RLVizEnvInterface {

	private int timeStep=0;
	private int episodeNumber=0;
	private int totalSteps=0;
	private int currentScore =0;
	private GameState gameState = null;

	static final int terminalScore = 0;

//	/*Hold all the possible bricks that can fall*/
	Vector<TetrlaisPiece> possibleBlocks=new Vector<TetrlaisPiece>();

//Defaults
	int width=4;
	int height=8;

	public Tetrlais(){
		super();
		//Defaults
		possibleBlocks.add(TetrlaisPiece.makeLine());
		gameState=new GameState(width,height,possibleBlocks);
		timeStep=0;
		episodeNumber=0;	
		totalSteps=0;
	}
	public Tetrlais(ParameterHolder p){
		super();
		if(p!=null){
			if(!p.isNull()){
				width=p.getIntegerParam("Width");
				height=p.getIntegerParam("Height");
				System.out.println("Running tetris with: "+width+" x "+height);
				if(p.getBooleanParam("LongBlock"))	possibleBlocks.add(TetrlaisPiece.makeLine());
				if(p.getBooleanParam("SquareBlock"))	possibleBlocks.add(TetrlaisPiece.makeSquare());
				if(p.getBooleanParam("TriBlock"))	possibleBlocks.add(TetrlaisPiece.makeTri());
				if(p.getBooleanParam("SBlock"))	possibleBlocks.add(TetrlaisPiece.makeSShape());
				if(p.getBooleanParam("ZBlock"))	possibleBlocks.add(TetrlaisPiece.makeZShape());
				if(p.getBooleanParam("LBlock")) possibleBlocks.add(TetrlaisPiece.makeLShape());
				if(p.getBooleanParam("JBlock")) possibleBlocks.add(TetrlaisPiece.makeJShape());
			}
		}
		gameState=new GameState(width,height,possibleBlocks);
		timeStep=0;
		episodeNumber=0;
		totalSteps=0;
	}


	//This method creates the object that can be used to easily set different problem parameters
	public static ParameterHolder getDefaultParameters(){
		ParameterHolder p = new ParameterHolder();
		p.addIntegerParam("Width",6);
		p.addIntegerParam("Height",12);
		p.addBooleanParam("LongBlock",true);
		p.addBooleanParam("SquareBlock",true);
		p.addBooleanParam("TriBlock",true);
		p.addBooleanParam("SBlock", true);
		p.addBooleanParam("ZBlock", true);
		p.addBooleanParam("LBlock", true);
		p.addBooleanParam("JBlock", true);
		return p;
	}



	/*Base RL-Glue Functions*/
	public String env_init() {
		/* initialize the environment, construct a task_spec to pass on. The tetris environment
		 * has 200 binary observation variables and 1 action variable which ranges between 0 and 4. These are all 
		 * integer values. */
		/*NOTE: THE GAME STATE WIDTH AND HEIGHT MUST MULTIPLY TO EQUAL THE NUMBER OF OBSERVATION VARIABLES*/
		int numStates=gameState.getHeight()*gameState.getWidth();
		timeStep=0;
		episodeNumber=0;
		String task_spec = "2.0:e:"+numStates+"_[";
		for(int i = 0; i< numStates-1; i++)
			task_spec = task_spec + "i,";
		task_spec = task_spec + "i]";
		for(int i=0; i<numStates;i++)
			task_spec = task_spec + "_[0,1]";
		task_spec = task_spec + ":1_[i]_[0,5]";

		return task_spec;
	}

	public Observation env_start() {
		gameState.reset();
		currentScore =0;
		timeStep=0;
		episodeNumber++;
		Observation o = gameState.get_observation();

		return o;
	}

	public Reward_observation env_step(Action actionObject) {
		int theAction=actionObject.intArray[0];
		if(theAction>5||theAction<0){
			System.err.println("Invalid action selected in Tetrlais: "+theAction);
			Thread.dumpStack();
			System.exit(1);
		}

		
		Reward_observation ro = new Reward_observation();
		timeStep++;
		totalSteps++;
		ro.terminal = 1;

		if(!gameState.gameOver())
		{
			ro.terminal=0;
			gameState.update();
			gameState.take_action(theAction);
			ro.r = gameState.get_score() - currentScore;
			currentScore = gameState.get_score();
		}
		else{
			ro.r = Tetrlais.terminalScore;
			currentScore = 0;	
		}

		ro.o = gameState.get_observation();
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


	public String env_message(String theMessage) {
		EnvironmentMessages theMessageObject;
		try {
			theMessageObject = EnvironmentMessageParser.parseMessage(theMessage);
		} catch (Exception e) {
			System.err.println("Someone sent Tetrlais a message that wasn't RL-Viz compatible");
			return "I only respond to RL-Viz messages!";
		}



		if(theMessageObject.canHandleAutomatically(this)){
			return theMessageObject.handleAutomatically(this);
		}

		if(theMessageObject.getTheMessageType()==rlVizLib.messaging.environment.EnvMessageType.kEnvCustom.id()){

			String theCustomType=theMessageObject.getPayLoad();

			if(theCustomType.equals("GETTETRLAISSTATE")){
				//It is a request for the state
				TetrlaisStateResponse theResponseObject=new TetrlaisStateResponse(episodeNumber,timeStep, totalSteps,currentScore,gameState.getWidth(), gameState.getHeight(), gameState.getWorldState(), gameState.getCurrentPiece());
				return theResponseObject.makeStringResponse();
			}
			System.out.println("We need some code written in Env Message for Tetrlais.. unknown custom message type received");
			Thread.dumpStack();

			return null;
		}

		System.out.println("We need some code written in Env Message for  Tetrlais!");
		Thread.dumpStack();

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
		return gameState.get_observation();
	}
	/*End of RL-Viz Methods*/

	public RLVizVersion getTheVersionISupport() {
		return new RLVizVersion(1,0);
	}

	/*Tetris Helper Functions*/

	/*End of Tetris Helper Functions*/
}
