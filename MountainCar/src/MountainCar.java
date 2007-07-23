
import java.util.Vector;

import messages.MCStateRequest;
import messages.MCStateResponse;
import messaging.environment.EnvCustomRequest;
import messaging.environment.EnvMessageType;
import messaging.environment.EnvObsForStateRequest;
import messaging.environment.EnvObsForStateResponse;
import messaging.environment.EnvRangeResponse;
import messaging.environment.EnvironmentMessageParser;
import messaging.environment.EnvironmentMessages;
import rlglue.Action;
import rlglue.Observation;
import rlglue.Random_seed_key;
import rlglue.Reward_observation;
import rlglue.State_key;

/*===========================

Dynamics

============================*/
public class MountainCar extends EnvironmentBase{
	
	
	private double position;
	private double velocity;



	private  boolean randomStarts=false;

	static final int numActions = 3;
	

	private double mcar_min_position = -1.2;
	private double mcar_max_position = 0.6;
	private double mcar_max_velocity = 0.07;            // the negative of this is also the minimum velocity
	private double mcar_goal_position = 0.5;
	

	private boolean inGoalRegion(){
		return position >= mcar_goal_position;
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

	public double getMcar_min_position() {
		return mcar_min_position;
	}

	public double getMcar_max_position() {
		return mcar_max_position;
	}

	public double getMcar_min_velocity() {
		return -mcar_max_velocity;
	}
	public double getMcar_max_velocity() {
		return mcar_max_velocity;
	}

	public void setPosition(double position) {
		this.position = position;
	}

	public void setVelocity(double velocity) {
		this.velocity = velocity;
	}


	public String env_init() {
		position = -0.5;
	    velocity = 0.0;
		return "1:e:2_[f,f]_[,]_[,]:1_[i]_[0,3]";
	}

	public String env_message(String theMessage) {
		EnvironmentMessages theMessageObject=EnvironmentMessageParser.parseMessage(theMessage);
		String theResponseString=null;
		
		System.out.println("mountain car got it");
		
		if(theMessageObject.getTheMessageType().id()==EnvMessageType.kEnvQueryVarRanges.id()){
			System.out.println("and treating it like var range request");
//			//Handle a request for the ranges
			Vector<Double> mins = new Vector<Double>();
			Vector<Double> maxs = new Vector<Double>();
			
				mins.add(getMcar_min_position());
				mins.add(getMcar_min_velocity());
				maxs.add(getMcar_max_position());
				maxs.add(getMcar_max_velocity());
				
			EnvRangeResponse theResponse=new EnvRangeResponse(mins, maxs);
			
			theResponseString=theResponse.makeStringResponse();
			
			//System.out.println("MountainCar: Sending response over network: "+theResponseString);
			return theResponseString;

		}
		if(theMessageObject.getTheMessageType().id()==messaging.environment.EnvMessageType.kEnvQueryObservationsForState.id()){
			System.out.println("and treating it like kEnvQueryObservationsForState request");
			
			EnvObsForStateRequest theCastedRequest=(EnvObsForStateRequest)theMessageObject;
			
			Vector<Observation> theQueryStates=theCastedRequest.getTheRequestStates();
			Vector<Observation> theObservations= new Vector<Observation>();
			
			for(int i=0;i<theQueryStates.size();i++){
				Observation thisObs=getObservationForState(theQueryStates.get(i));
				theObservations.add(thisObs);
			}
			
			EnvObsForStateResponse theResponse = new EnvObsForStateResponse(theObservations);
			
			theResponseString=theResponse.makeStringResponse();
			
			//System.out.println("MountainCar: Sending response over network: "+theResponseString);
			return theResponseString;
		}


		if(theMessageObject.getTheMessageType().id()==messaging.environment.EnvMessageType.kEnvCustom.id()){
			System.out.println("and treating it like kEnvCustom request");
			

			EnvCustomRequest theCastedRequest=(EnvCustomRequest)theMessageObject;
			
			String theCustomType=theCastedRequest.getThePayload();
			
			if(theCustomType.equals("GETMCSTATE")){
				//It is a request for the state
				double position=this.getPosition();
				double velocity=this.getVelocity();
				double height= this.getHeight();
				double deltaheight=this.getHeightAtPosition(position+.05);
				MCStateResponse theResponseObject=new MCStateResponse(position,velocity,height,deltaheight);
				return theResponseObject.makeStringResponse();
			}
			System.out.println("We need some code written in Env Message for MountainCar.. unknown custom message type received");

		}

	
		

		System.out.println("We need some code written in Env Message for MountainCar!");
		Thread.dumpStack();
		// TODO Auto-generated method stub
		return null;
	}

	
//This is really easy in mountainCar because you observe exactly the state
	private Observation getObservationForState(Observation observation) {
		return observation;
	}

	@Override
	protected Observation makeObservation(){
		Observation currentObs= new Observation(0,2);
		currentObs.doubleArray[0]=position;
		currentObs.doubleArray[1]=velocity;
		return currentObs;
	}


	public Observation env_start() {
	if(randomStarts){
		position = (Math.random()*(mcar_max_position + Math.abs((mcar_min_position))) - Math.abs(mcar_min_position));
		velocity = (Math.random()*mcar_max_velocity*2) - Math.abs(mcar_max_velocity);
	}else{
		position = -0.5;
		velocity = 0.0;
	}
	
		return makeObservation();
	}

	public Reward_observation env_step(Action theAction) {
		int a=theAction.intArray[0];
				velocity += ((a-1))*0.001 + Math.cos(3.0f*position)*(-0.0025);
			    if (velocity > mcar_max_velocity) velocity = mcar_max_velocity;
			    if (velocity < -mcar_max_velocity) velocity = -mcar_max_velocity;
			    position += velocity;
			    if (position > mcar_max_position) position = mcar_max_position;
			    if (position < mcar_min_position) position = mcar_min_position;
			    if (position==mcar_min_position && velocity<0) velocity = 0;		
	

		return makeRewardObservation(getReward(),inGoalRegion());
		}
	
	
	public MountainCar() {
		this(false);
	}
	public MountainCar(boolean randomStarts) {
		super();
		this.randomStarts = randomStarts;
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



//	void addParametersEnv(ParameterHolder *theParameters){
//				theParameters->addBoolParam("Random Start States");
//				theParameters->setBoolParam("Random Start States",false);
//				theParameters->setAlias("rss","Random Start States");
//	}
//	Environment *maker(ParameterHolder *theParams){
//			if(theParams==NULL)
//				return new MountainCarEnv();
//			else{
//				bool randomStarts=theParams->getBoolParam("Random Start States");
//				return new MountainCarEnv(randomStarts);
//			}
//		}
//


//
	private double getReward(){
		if(inGoalRegion())
			return 0.0f;
		else
			return -1.0f;
	}

}

