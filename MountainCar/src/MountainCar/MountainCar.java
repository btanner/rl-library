package MountainCar;

import messages.MCStateResponse;
import rlVizLib.Environments.EnvironmentBase;
import rlVizLib.general.ParameterHolder;
import rlVizLib.general.RLVizVersion;
import rlVizLib.messaging.NotAnRLVizMessageException;
import rlVizLib.messaging.environment.EnvironmentMessageParser;
import rlVizLib.messaging.environment.EnvironmentMessages;
import rlVizLib.messaging.interfaces.RLVizEnvInterface;
import rlVizLib.messaging.interfaces.ReceivesRunTimeParameterHolderInterface;
import rlVizLib.messaging.interfaces.getEnvMaxMinsInterface;
import rlVizLib.messaging.interfaces.getEnvObsForStateInterface;
import rlVizLib.utilities.TaskSpecObject;
import rlVizLib.utilities.TaskSpecParser;
import rlglue.environment.Environment;
import rlglue.types.Action;
import rlglue.types.Observation;
import rlglue.types.Random_seed_key;
import rlglue.types.Reward_observation;
import rlglue.types.State_key;

/*
 * July 2007
 * This is the Java Version MountainCar Domain from the RL-Library.  
 * Brian Tanner ported it from the Existing RL-Library to Java.
 * I found it here: http://rlai.cs.ualberta.ca/RLR/environment.html
 * 
 * The methods in here are sorted by importance in terms of what's important to know about for playing with the dynamics of the system.
 */

public class MountainCar extends EnvironmentBase implements getEnvMaxMinsInterface, getEnvObsForStateInterface, RLVizEnvInterface, ReceivesRunTimeParameterHolderInterface{


//	Current State Information
	private double position;
	private double velocity;


	static final int numActions = 3;


//	Problem parameters (you can add more if you like)
	private  boolean randomStarts=false;
	private double minPosition = -1.2;
	private double maxPosition = 0.6;
	private double minVelocity = -0.07;    
	private double maxVelocity = 0.07;    
	private double goalPosition = 0.5;

	private double defaultInitPosition=-0.5d;
	private double defaultInitVelocity=0.0d;

	private double rewardPerStep=-1.0d;
	private double rewardAtGoal=0.0d;

//	Stopping condition
	private boolean inGoalRegion(){
		return position >= goalPosition;
	}

	public String env_init() {
		position = defaultInitPosition;
		velocity = defaultInitVelocity;
		return "1:e:2_[f,f]_["+minPosition+","+maxPosition+"]_["+(-maxVelocity)+","+maxVelocity+"]:1_[i]_[0,3]";
	}

	public Observation env_start() {
		if(randomStarts){
			position = (Math.random()*(maxPosition + Math.abs((minPosition))) - Math.abs(minPosition));
			velocity = (Math.random()*maxVelocity*2) - Math.abs(maxVelocity);
		}else{
			position = defaultInitPosition;
			velocity = defaultInitVelocity;
		}

		return makeObservation();
	}

//	The constants of this height function could easily be parameterized
	public Reward_observation env_step(Action theAction) {
		int a=theAction.intArray[0];
		velocity += ((a-1))*0.001 + Math.cos(3.0f*position)*(-0.0025);
		if (velocity > maxVelocity) velocity = maxVelocity;
		if (velocity < minVelocity) velocity = minVelocity;
		position += velocity;
		if (position > maxPosition) position = maxPosition;
		if (position < minPosition) position = minPosition;
		if (position==minPosition && velocity<0) velocity = 0;		


		return makeRewardObservation(getReward(),inGoalRegion());
	}


	//This method creates the object that can be used to easily set different problem parameters
	public static ParameterHolder getDefaultParameters(){
		ParameterHolder p = new ParameterHolder();
		p.addBooleanParam("randomStartStates",false);

		p.addDoubleParam("minVelocity",-.07d);
		p.addDoubleParam("maxVelocity",.07d);

		return p;
	}

	public MountainCar(ParameterHolder p){
		super();
		if(p!=null){
			if(!p.isNull()){
				this.randomStarts=p.getBooleanParam("randomStartStates");
				this.minVelocity=p.getDoubleParam("minVelocity");
				this.maxVelocity=p.getDoubleParam("maxVelocity");
				
				System.out.println("MountainCar Max and Min Velocity are: "+minVelocity+" and "+maxVelocity);
			}
		}
	}
	
	

	private double getReward(){
		if(inGoalRegion())
			return rewardAtGoal;
		else
			return rewardPerStep;
	}


	public String env_message(String theMessage) {
		EnvironmentMessages theMessageObject;
		try {
			theMessageObject = EnvironmentMessageParser.parseMessage(theMessage);
		} catch (NotAnRLVizMessageException e) {
			System.err.println("Someone sent mountain Car a message that wasn't RL-Viz compatible");
			return "I only respond to RL-Viz messages!";
		}

		if(theMessageObject.canHandleAutomatically(this))return theMessageObject.handleAutomatically(this);

//		If it wasn't handled automatically, maybe its a custom Mountain Car Message
		if(theMessageObject.getTheMessageType()==rlVizLib.messaging.environment.EnvMessageType.kEnvCustom.id()){

			String theCustomType=theMessageObject.getPayLoad();

			if(theCustomType.equals("GETMCSTATE")){
				//It is a request for the state
				double position=this.getPosition();
				double velocity=this.getVelocity();
				double height= this.getHeight();
				double deltaheight=this.getHeightAtPosition(position+.05);
				MCStateResponse theResponseObject=new MCStateResponse(position,velocity,height,deltaheight);
				return theResponseObject.makeStringResponse();
			}
		}
		System.err.println("We need some code written in Env Message for MountainCar.. unknown request received: "+theMessage);
		Thread.dumpStack();
		return null;
	}


	//If you wanted to add noise to things this would be the place to do it.
	@Override
	protected Observation makeObservation(){
		Observation currentObs= new Observation(0,2);
		currentObs.doubleArray[0]=position;
		currentObs.doubleArray[1]=velocity;
		return currentObs;
	}



	public MountainCar() {
		this(null);
	}


	public void env_cleanup() {
		// TODO Auto-generated method stub

	}

	public Random_seed_key env_get_random_seed() {
		// TODO Auto-generated method stub
		return null;
	}

	public State_key env_get_state() {
		// TODO Auto-generated method stub
		return null;
	}


	public void env_set_random_seed(Random_seed_key arg0) {
		// TODO Auto-generated method stub

	}

	public void env_set_state(State_key arg0) {
		// TODO Auto-generated method stub

	}



	public double getMaxValueForQuerableVariable(int dimension) {
		if(dimension==0)
			return this.getMaxPosition();
		else
			return this.getMaxVelocity();
	}

	public double getMinValueForQuerableVariable(int dimension) {
		if(dimension==0)
			return this.getMinPosition();
		else
			return this.getMinVelocity();
	}


	//This is really easy in mountainCar because you observe exactly the state
	public Observation getObservationForState(Observation theState) {
		return theState;
	}

	public int getNumVars(){
		return 2;
	}

	public RLVizVersion getTheVersionISupport() {
		return new RLVizVersion(1,1);
	}


	public static void main(String args[]){
		Environment mcEnv=new MountainCar();
		String taskSpec=mcEnv.env_init();
		TaskSpecObject parsedTaskSpec = TaskSpecParser.parse(taskSpec);
		System.out.println(parsedTaskSpec);

	}

	public boolean receiveRunTimeParameters(ParameterHolder theParams) {
		System.out.println("MountainCar received some parameters!");
		System.out.println("They look like:"+theParams.stringSerialize());
		//return that I acknowledge this
		return true;
	}



	public double getHeight(){
		return getHeightAtPosition(position);
	}

	public double getHeightAtPosition(double queryPosition){
		return -Math.cos(3*(queryPosition-(Math.PI/2.0f)));
	}

	public double getPosition() {
		return position;
	}

	public double getVelocity() {
		return velocity;
	}

	public double getMinPosition() {
		return minPosition;
	}

	public double getMaxPosition() {
		return maxPosition;
	}

	public double getMinVelocity() {
		return -maxVelocity;
	}
	public double getMaxVelocity() {
		return maxVelocity;
	}

	public void setPosition(double position) {
		this.position = position;
	}

	public void setVelocity(double velocity) {
		this.velocity = velocity;
	}


}

