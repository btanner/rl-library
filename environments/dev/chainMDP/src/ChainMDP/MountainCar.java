/* Mountain Car Domain
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
package MountainCar;

import java.util.StringTokenizer;
import java.util.Vector;

import mcMessages.MCGoalResponse;
import mcMessages.MCHeightResponse;
import mcMessages.MCStateResponse;
import rlVizLib.Environments.EnvironmentBase;
import rlVizLib.general.ParameterHolder;
import rlVizLib.general.RLVizVersion;
import rlVizLib.messaging.NotAnRLVizMessageException;
import rlVizLib.messaging.environment.EnvironmentMessageParser;
import rlVizLib.messaging.environment.EnvironmentMessages;
import rlVizLib.messaging.interfaces.HasAVisualizerInterface;
import rlVizLib.messaging.interfaces.RLVizVersionResponseInterface;
import rlVizLib.messaging.interfaces.getEnvMaxMinsInterface;
import rlVizLib.messaging.interfaces.getEnvObsForStateInterface;
import rlglue.types.Action;
import rlglue.types.Observation;
import rlglue.types.Random_seed_key;
import rlglue.types.Reward_observation;
import rlglue.types.State_key;
import java.util.Random;
import rlVizLib.general.hasVersionDetails;
import rlVizLib.utilities.UtilityShop;

/*
 * July 2007
 * This is the Java Version MountainCar Domain from the RL-Library.  
 * Brian Tanner ported it from the Existing RL-Library to Java.
 * I found it here: http://rlai.cs.ualberta.ca/RLR/environment.html
 * 
 * The methods in here are sorted by importance in terms of what's important to know about for playing with the dynamics of the system.
 */


public class MountainCar extends EnvironmentBase implements 
							getEnvMaxMinsInterface, 
							getEnvObsForStateInterface, 
							RLVizVersionResponseInterface, 
							HasAVisualizerInterface{

    static final int numActions = 3;
    
    static double shapeInterval = .15;
    static double shapeSubgoal = 0;
    static double shapeDelta = .3;
    static double shapeReward = 10;
    static boolean shapingOn = true;
	static int shapeMode = 0;
	static double shapeScale;
	
	static double oldPos;
	static double oldVel;
	
	static double physicsTerm = 1;
	static double counter = 0;
	static int action;

    

        protected MountainCarState theState=null;
        protected Vector<MountainCarState> savedStates=null;
        
        //Problem parameters have been moved to MountainCar State

        private Random randomGenerator=new Random();

    private Random getRandom() {
          return randomGenerator;
    }


            double reportedMinPosition;
            double reportedMaxPosition;
            double reportedMinVelocity;
            double reportedMaxVelocity;

	public String env_init() {
                savedStates=new Vector<MountainCarState>();
                //This should be like a final static member or something, or maybe it should be configurable... dunno
                int taskSpecVersion=2;
                
               counter = 0;
			   
            return taskSpecVersion+":e:2_[f,f]_["+reportedMinPosition+","+reportedMaxPosition+"]_["+reportedMinVelocity+","+reportedMaxVelocity+"]:1_[i]_[0,2]:[-1,0]";
	}

	public Observation env_start() {
			counter++;
			
                if(theState.randomStarts){
			double fullRandPosition = (randomGenerator.nextDouble()*(theState.maxPosition + Math.abs((theState.minPosition))) - Math.abs(theState.minPosition));
                        //Want to actually start in a smaller bowl
                        theState.position=theState.defaultInitPosition+fullRandPosition/3.0d;
			//Want inital velocity = 0.0d;
                        theState.velocity = theState.defaultInitVelocity;
		}else{
			theState.position = theState.defaultInitPosition;
			theState.velocity = theState.defaultInitVelocity;
		}
                      
        shapeSubgoal = -0.5 + shapeInterval;
        
        oldPos = theState.position;
        oldVel = theState.velocity;
		
		return makeObservation();
	}

//	The constants of this height function could easily be parameterized
	public Reward_observation env_step(Action theAction) {

                int a=theAction.intArray[0];
				action = a;

		if(a>2||a<0){
			System.err.println("Invalid action selected in mountainCar: "+a);
			a = randomGenerator.nextInt(3);
		}

		if(shapeMode == 2) physicsTerm = Math.min(1, counter/100.0);
		else physicsTerm = 1;
		theState.update(a, physicsTerm);
            
			
		double currentReward =theState.getReward();
		if(!theState.inGoalRegion() && shapingOn){ 
			currentReward += getShapingReward();
		}
		if(theState.inGoalRegion())
			shapeReward *= shapeDelta;

		
        oldPos = theState.position;
        oldVel = theState.velocity;
		

		return makeRewardObservation(currentReward, /*theState.getReward(),*/theState.inGoalRegion());
	}


	//This method creates the object that can be used to easily set different problem parameters
	public static ParameterHolder getDefaultParameters(){

		ParameterHolder p = new ParameterHolder();

                rlVizLib.utilities.UtilityShop.setVersionDetails(p, new DetailsProvider());
                p.addBooleanParam("randomStartStates",false);
                p.addDoubleParam("delta",0.5);
                p.addDoubleParam("interval",0.15);
				p.addIntegerParam("mode",0);
				p.addBooleanParam("shapingOn",false);
				p.addDoubleParam("shapeReward",2);


		return p;
	}
        
	public double getShapingReward() {
	
		double r = 0;
	
// skinner shaping
		if(shapeMode == 0)
			{
			if( theState.position >= shapeSubgoal)
			{
				//System.out.printf("Current Shape %f\n", shapeReward);			

				r = shapeReward;
				shapeSubgoal += shapeInterval;
				
				//System.out.println("the shape is down too:" + shapeReward);
				//System.out.printf("SHAPE (del = %f): The reward is %f\n", shapeDelta,r-1);			

				if (shapeReward < .001) shapingOn = false;
			}
		}
		
// potential function shaping
		else if(shapeMode == 1)
		{
			double term1 = Math.sin(3.0*oldPos);
			double term2 = Math.pow(oldVel,2);
			double term3 = Math.sin(3.0*theState.position);
			double term4 = Math.pow(theState.velocity,2);
			
			r = Math.min(9.8*Math.sin(1.5), -9.8*term3 + (1/2.0)*term4) - (Math.min(9.8*Math.sin(1.5), -9.8*term1 + (1/2.0)*term2));
			
		}
//converging physics shaping
		else if(shapeMode == 2)
		{
			r = 0;
		}
//converging reward function shaping
		else if(shapeMode == 3)
		{
			double term1 = Math.sin(3*oldPos);
			double term2 = Math.pow(oldVel,2);
			double E = -9.8*term1 + (1/2.0)*term2;
			double Eg = -9.8*Math.sin(1.5);
			double w = Math.min(1,counter/200.0);
			
			if(E > Eg){
				if(action == 2) r = (1-w)*.001;
				else  r = -(1-w)*.001;
			}
			else{
				if(action == 2 && oldVel > 0) r = (1-w)*.001;
				if(action == 0 && oldVel < 0) r = (1-w)*.001;
				else  r = -(1-w)*.001;
			}						
		}		
		else
			r = 0;
		return r;
	}
			


	public MountainCar(ParameterHolder p){
            this(p,false);
        }
            public MountainCar(ParameterHolder p, boolean useCompetitionMode){
		super();
                theState=new MountainCarState(randomGenerator);
		if(p!=null){
			if(!p.isNull()){
				theState.randomStarts=p.getBooleanParam("randomStartStates");
				shapeDelta = p.getDoubleParam("delta");
				shapeScale = p.getDoubleParam("interval");
				shapeMode = p.getIntegerParam("mode");
				shapingOn=p.getBooleanParam("shapingOn");
				shapeReward = p.getDoubleParam("shapeReward");



				shapeInterval = 1.0*shapeScale;
				
			}
		}
                setupRangesAndStuff();
	}

        
        private void setupRangesAndStuff(){
                reportedMinPosition=theState.minPosition;
                reportedMaxPosition=theState.maxPosition;

                reportedMinVelocity=theState.minVelocity;
                reportedMaxVelocity=theState.maxVelocity;
        }



	public String env_message(String theMessage) {
		EnvironmentMessages theMessageObject;
		try {
			theMessageObject = EnvironmentMessageParser.parseMessage(theMessage);
		} catch (NotAnRLVizMessageException e) {
			System.err.println("Someone sent mountain Car a message that wasn't RL-Viz compatible");
			return "I only respond to RL-Viz messages!";
		}

		if(theMessageObject.canHandleAutomatically(this)){
                    String theResponseString=theMessageObject.handleAutomatically(this);
                    return theResponseString;
		}

//		If it wasn't handled automatically, maybe its a custom Mountain Car Message
		if(theMessageObject.getTheMessageType()==rlVizLib.messaging.environment.EnvMessageType.kEnvCustom.id()){

			String theCustomType=theMessageObject.getPayLoad();

			if(theCustomType.equals("GETMCSTATE")){
				//It is a request for the state
				double position=theState.position;
                                double velocity=theState.velocity;
				double height= this.getHeight();
				double deltaheight=theState.getHeightAtPosition(position+.05);
				MCStateResponse theResponseObject=new MCStateResponse(position,velocity,height,deltaheight);
				return theResponseObject.makeStringResponse();
			}

			if(theCustomType.startsWith("GETHEIGHTS")){
				Vector<Double> theHeights=new Vector<Double>();

				StringTokenizer theTokenizer = new StringTokenizer(theCustomType,":");
				//throw away the first token
				theTokenizer.nextToken();

				int numQueries=Integer.parseInt(theTokenizer.nextToken());
				for(int i=0;i<numQueries;i++){
					double thisPoint=Double.parseDouble(theTokenizer.nextToken());
					theHeights.add(theState.getHeightAtPosition(thisPoint));
				}

				MCHeightResponse theResponseObject=new MCHeightResponse(theHeights);
				return theResponseObject.makeStringResponse();
			}

			if(theCustomType.startsWith("GETMCGOAL")){
				MCGoalResponse theResponseObject = new MCGoalResponse(theState.goalPosition);
				return theResponseObject.makeStringResponse();
			}

		}
		System.err.println("We need some code written in Env Message for MountainCar.. unknown request received: "+theMessage);
		Thread.dumpStack();
		return null;
	}

	@Override
	protected Observation makeObservation(){
		Observation currentObs= new Observation(0,2);

        currentObs.doubleArray[0]=theState.position;
		currentObs.doubleArray[1]=theState.velocity;

		return currentObs;
	}


	public MountainCar() {
            this(getDefaultParameters());
	}


	public void env_cleanup() {
            if(savedStates!=null)
                savedStates.clear();


	}

	//
//This has a side effect, it changes the random order.
//
    public Random_seed_key env_get_random_seed() {
        Random_seed_key k=new Random_seed_key(2,0);
        long newSeed=getRandom().nextLong();
        getRandom().setSeed(newSeed);
        k.intArray[0]=UtilityShop.LongHighBitsToInt(newSeed);
        k.intArray[1]=UtilityShop.LongLowBitsToInt(newSeed);
        return k;
    }

    public void env_set_random_seed(Random_seed_key k) {
        long storedSeed=UtilityShop.intsToLong(k.intArray[0], k.intArray[1]);
        getRandom().setSeed(storedSeed);
    }


    public State_key env_get_state() {
        savedStates.add(new MountainCarState(theState));
        State_key k=new State_key(1,0);
        k.intArray[0]=savedStates.size()-1;
        return k;
    }

    public void env_set_state(State_key k) {
        int theIndex=k.intArray[0];
        
        if(savedStates==null||theIndex>=savedStates.size()){
            System.err.println("Could not set state to index:"+theIndex+", that's higher than saved size");
            return;
        }
        MountainCarState oldState=savedStates.get(k.intArray[0]);
        this.theState=new MountainCarState(oldState);
    }
    



	public double getMaxValueForQuerableVariable(int dimension) {
		if(dimension==0)
			return reportedMaxPosition;
		else
			return reportedMaxVelocity;
	}

	public double getMinValueForQuerableVariable(int dimension) {
		if(dimension==0)
			return reportedMinPosition;
		else
			return reportedMinVelocity;
	}


	//This is really easy in mountainCar because you observe exactly the state
        //Oops, that's not true anymore, we have noise and offsets...
	public Observation getObservationForState(Observation theState) {
		return theState;
	}

	public int getNumVars(){
		return 2;
	}


	private double getHeight(){
		return theState.getHeightAtPosition(theState.position);
	}

	public RLVizVersion getTheVersionISupport() {
		return new RLVizVersion(1,1);
	}

    public String getVisualizerClassName() {
		return "visualizers.mountainCar.MountainCarVisualizer";
	}

}
class DetailsProvider implements hasVersionDetails{
    public String getName() {
        return "Mountain Car 1.01";
    }

    public String getShortName() {
        return "Mount-Car";
    }

    public String getAuthors() {
        return "Richard Sutton, Adam White, Brian Tanner";
    }

    public String getInfoUrl() {
        return "http://rl-library.googlecode.com";
    }

    public String getDescription() {
        return "RL-Library Java Version of the classic Mountain Car RL-Problem.";
    }


}

