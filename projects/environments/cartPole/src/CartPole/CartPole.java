package CartPole;

import CartPole.messages.*;
import rlVizLib.Environments.EnvironmentBase;
import rlVizLib.general.ParameterHolder;
import rlVizLib.general.RLVizVersion;
import rlVizLib.messaging.NotAnRLVizMessageException;
import rlVizLib.messaging.environment.EnvironmentMessageParser;
import rlVizLib.messaging.environment.EnvironmentMessages;
import rlVizLib.messaging.interfaces.HasAVisualizerInterface;
import rlVizLib.messaging.interfaces.RLVizEnvInterface;
import rlVizLib.messaging.interfaces.ReceivesRunTimeParameterHolderInterface;
import rlVizLib.messaging.interfaces.getEnvMaxMinsInterface;
import rlVizLib.messaging.interfaces.getEnvObsForStateInterface;
import rlglue.types.Action;
import rlglue.types.Observation;
import rlglue.types.Random_seed_key;
import rlglue.types.Reward_observation;
import rlglue.types.State_key;

public class CartPole extends EnvironmentBase implements getEnvMaxMinsInterface, getEnvObsForStateInterface, RLVizEnvInterface, HasAVisualizerInterface{
	/*used for vis*/
	int numEpisodes=0;
	int numSteps=0;
	int totalNumSteps=0;
	
	final static double one_degree = 0.0174532;	/* 2pi/360 */
	final static double six_degrees = 0.1047192;
	final static double twelve_degrees = 0.2094384;
	final static double fifty_degrees = 0.87266;
	
	final static double N_BOXES = 162;         /* Number of disjoint boxes of state space. */
	final static double GRAVITY = 9.8;
	final static double MASSCART = 1.0;
	final static double MASSPOLE = 0.1;
	final static double TOTAL_MASS = (MASSPOLE + MASSCART);
	final static double LENGTH = 0.5;	  /* actually half the pole's length */
	final static double POLEMASS_LENGTH = (MASSPOLE * LENGTH);
	final static double FORCE_MAG = 10.0;
	final static double TAU = 0.02;	  /* seconds between state updates */
	final static double FOURTHIRDS = 1.3333333333333;
	final static double DEFAULTLEFTCARTBOUND = -2.4;
	final static double DEFAULTRIGHTCARTBOUND = 2.4;
	final static double DEFAULTLEFTANGLEBOUND = -twelve_degrees;
	final static double DEFAULTRIGHTANGLEBOUND = twelve_degrees;
	
	double leftCartBound;
	double rightCartBound;
	double leftAngleBound;
	double rightAngleBound;
	double x;			/* cart position, meters */
	double x_dot;			/* cart velocity */
	double theta;			/* pole angle, radians */
	double theta_dot;		/* pole angular velocity */
	double p, oldp, rhat, r;
	int tsteps=0;
	int box;
	int i;
	int y;
	int steps = 0;
	int failures=0;
	int failed;
	
	public CartPole(){
		this(getDefaultParameters());
	}
	
	public CartPole(ParameterHolder p){
		super();
		if(p!=null){
			if(!p.isNull()){
				leftAngleBound=p.getDoubleParam("leftAngle");
				rightAngleBound=p.getDoubleParam("rightAngle");
				this.leftCartBound=p.getDoubleParam("leftCart");
				rightCartBound=p.getDoubleParam("rightCart");

			}
		}
	}
	
	public static ParameterHolder getDefaultParameters(){
		ParameterHolder p = new ParameterHolder();

		p.addDoubleParam("Left Terminal Angle", DEFAULTLEFTANGLEBOUND);
		p.addDoubleParam("Right Terminal Angle", DEFAULTRIGHTANGLEBOUND);
		p.addDoubleParam("Terminal Left Cart Position",DEFAULTLEFTCARTBOUND);
		p.addDoubleParam("Terminal Right Cart Position",DEFAULTRIGHTCARTBOUND);

		p.setAlias("leftCart", "Terminal Left Cart Position");
		p.setAlias("rightCart", "Terminal Right Cart Position");
		p.setAlias("leftAngle", "Left Terminal Angle");
		p.setAlias("rightAngle", "Right Terminal Angle");
		return p;
	}


	/*RL GLUE METHODS*/

	public String env_init() {
		String taskSpec = "2:e:4_[f,f,f,f]_[]_[]_[]_[]:1_[i]_[0,1]:[-1,0]";
		x = 0.0f;
		x_dot = 0.0f;
		theta = 0.0f;
		theta_dot = 0.0f;
		
		 box = getBox(x, x_dot, theta, theta_dot);
		return taskSpec;
	}
	
	public Observation env_start() {
		numEpisodes++;
		this.numSteps = 0;
		this.totalNumSteps++;
		x = 0.0f;
		x_dot = 0.0f;
		theta = 0.0f;
		theta_dot = 0.0f;
		box = getBox(x, x_dot, theta, theta_dot);
		return makeObservation();
	}
	
	public Reward_observation env_step(Action action) {
		this.numSteps++;
		this.totalNumSteps++;
		double xacc;
		double thetaacc;
	    double force;
	    double costheta;
	    double sintheta;
	    double temp;
	    int tempBox;

	    if(action.intArray[0] > 0)
	    	force = FORCE_MAG;
	    else
	    	force = -FORCE_MAG;
	    
	    costheta = Math.cos(theta);
	    sintheta = Math.sin(theta);

	    temp = (force + POLEMASS_LENGTH * theta_dot * theta_dot * sintheta)
			         / TOTAL_MASS;

	    thetaacc = (GRAVITY * sintheta - costheta* temp)
		       / (LENGTH * (FOURTHIRDS - MASSPOLE * costheta * costheta
	                                              / TOTAL_MASS));

	    xacc  = temp - POLEMASS_LENGTH * thetaacc* costheta / TOTAL_MASS;

	/*** Update the four state variables, using Euler's method. ***/

	    x  += TAU * x_dot;
	    x_dot += TAU * xacc;
	    theta += TAU * theta_dot;
	    theta_dot += TAU * thetaacc;
            
            while(theta>Math.PI)theta-=2.0d*Math.PI;
            while(theta<-Math.PI)theta+=2.0d*Math.PI;

            
	    tempBox = getBox(x,x_dot,theta,theta_dot);
            
	    if(tempBox < 0)
	    	//failure occured
	    	return new Reward_observation(-1.0d, makeObservation(),1);
	    else 
	    	return new Reward_observation(0.0d, makeObservation(),0);
	}
	
	public void env_cleanup() {
	}

	public Random_seed_key env_get_random_seed() {
		// TODO Auto-generated method stub
		return null;
	}

	public State_key env_get_state() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String env_message(String theMessage) {
		EnvironmentMessages theMessageObject;
		try {
			theMessageObject = EnvironmentMessageParser.parseMessage(theMessage);
		} catch (NotAnRLVizMessageException e) {
			System.err.println("Someone sent Cartpole a message that wasn't RL-Viz compatible");
			return "I only respond to RL-Viz messages!";
		}

		if(theMessageObject.canHandleAutomatically(this))return theMessageObject.handleAutomatically(this);

//		If it wasn't handled automatically, maybe its a custom Mountain Car Message
		if(theMessageObject.getTheMessageType()==rlVizLib.messaging.environment.EnvMessageType.kEnvCustom.id()){

			String theCustomType=theMessageObject.getPayLoad();

                        
			if(theCustomType.equals("GETCARTPOLETRACK")){
				//It is a request for the state
				CartpoleTrackResponse theResponseObject=new CartpoleTrackResponse(leftCartBound, rightCartBound, leftAngleBound, rightAngleBound);
				return theResponseObject.makeStringResponse();
			}
		}
		System.err.println("We need some code written in Env Message for Cartpole.. unknown request received: "+theMessage);
		Thread.dumpStack();
		return null;
	}

	public void env_set_random_seed(Random_seed_key key) {
		// TODO Auto-generated method stub
		
	}

	public void env_set_state(State_key key) {
		// TODO Auto-generated method stub
		
	}
	
	/*END OF RL_GLUE FUNCTIONS*/

	/*RL-VIZ Requirements*/
	@Override
	protected Observation makeObservation() {
		Observation returnObs = new Observation(0,4);
		returnObs.doubleArray[0] = x;
		returnObs.doubleArray[1] = x_dot;
		returnObs.doubleArray[2] = theta;
		returnObs.doubleArray[3] = theta_dot;
	
		return returnObs;
	}

	public RLVizVersion getTheVersionISupport() {
		return new RLVizVersion(1,0);
	}
	/*END OF RL-VIZ REQUIREMENTS*/
	
	/*CART POLE SPECIFIC FUNCTIONS*/
	private int getBox(double x2, double x_dot2, double theta2, double theta_dot2) {
		  int box=0;

		  if (x < leftCartBound || x > rightCartBound  || theta < leftAngleBound || theta > rightAngleBound)
			  return(-1); /* to signal failure */

		  if (x < -0.8)  
			  box = 0;
		  else if (x < 0.8)    
			  box = 1;
		  else		    	
			  box = 2;

		  if (x_dot < -0.5);
		  else if (x_dot < 0.5)     
			  box += 3;
		  else 			       
			  box += 6;

		  if (theta < -six_degrees) ;
		  else if (theta < -one_degree)     
			  box += 9;
		  else if (theta < 0) 		  
			  box += 18;
		  else if (theta < one_degree) 	  
			  box += 27;
		  else if (theta < six_degrees)    
			  box += 36;
		  else	    		
			  box += 45;

		  if (theta_dot < -fifty_degrees) ;
		  else if (theta_dot < fifty_degrees) 
			  box += 54;
		  else                   
			  box += 108;

		  return(box);
	}
	
	public double getLeftCartBound(){
		return this.leftCartBound;
	}

	public double getRightCartBound(){
		return this.rightCartBound;
	}
	public double getRightAngleBound(){
		return this.rightAngleBound;
	}
	
	public double getLeftAngleBound(){
		return this.leftAngleBound;
	}
	public double getMaxValueForQuerableVariable(int dimension) {
		if(dimension==0)
			return this.rightCartBound;
		else
			return this.rightAngleBound;
	}

	public double getMinValueForQuerableVariable(int dimension) {
		if(dimension==0)
			return this.leftCartBound;
		else
			return this.leftAngleBound;
	}

	public Observation getObservationForState(Observation theState) {
		return theState;
	}

	public int getNumVars(){
		return 2;
	}

	public boolean receiveRunTimeParameters(ParameterHolder theParams) {
		// TODO Auto-generated method stub
		return true;
	}

    public String getVisualizerClassName() {
        return "visualizers.CartPoleVisualizer.CartPoleVisualizer";
    }
}
