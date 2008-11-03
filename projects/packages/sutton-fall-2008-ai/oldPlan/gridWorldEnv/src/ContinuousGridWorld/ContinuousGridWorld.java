package ContinuousGridWorld;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Vector;


import rlVizLib.Environments.EnvironmentBase;
import rlVizLib.general.ParameterHolder;
import rlVizLib.general.RLVizVersion;
import rlVizLib.messaging.interfaces.HasAVisualizerInterface;
import rlVizLib.messaging.interfaces.getEnvMaxMinsInterface;
import rlVizLib.messaging.interfaces.getEnvObsForStateInterface;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.types.Random_seed_key;
import org.rlcommunity.rlglue.codec.types.Reward_observation_terminal;
import org.rlcommunity.rlglue.codec.types.State_key;

/*
 *  ContinuousGridWorld
 *
 *  Created by Brian Tanner on 02/03/07.
 *  Copyright 2007 Brian Tanner. All rights reserved.
 *
 */

public abstract class ContinuousGridWorld extends EnvironmentBase implements 
									getEnvMaxMinsInterface, 
									getEnvObsForStateInterface{

	protected  Point2D agentPos;
	protected  Point2D agentSize;
	protected  Rectangle2D currentAgentRect;
        protected Rectangle2D worldRect;
	protected Vector<Rectangle2D> resetRegions=new Vector<Rectangle2D>();
	protected Vector<Rectangle2D> barrierRegions=new Vector<Rectangle2D>();
	protected Vector<Double> thePenalties=new Vector<Double>();
        protected double walkSpeed=25.0d;
        

	protected Vector<Rectangle2D> rewardRegions=new Vector<Rectangle2D>();
	protected Vector<Double> theRewards=new Vector<Double>();
	
	public static ParameterHolder getDefaultParameters(){
		ParameterHolder p = new ParameterHolder();
                p.addDoubleParam("cont-grid-world-minX",0.0d);
                p.addDoubleParam("cont-grid-world-minY",0.0d);
                p.addDoubleParam("cont-grid-world-width",200.0d);
                p.addDoubleParam("cont-grid-world-height",200.0d);
                p.addDoubleParam("cont-grid-world-walk-speed",10.0d);
		return p;
	}
	
        public ContinuousGridWorld(){
            this(getDefaultParameters());
        }
        
                
	public ContinuousGridWorld(ParameterHolder theParams){
            double minX=theParams.getDoubleParam("cont-grid-world-minX");
            double minY=theParams.getDoubleParam("cont-grid-world-minY");
            double width=theParams.getDoubleParam("cont-grid-world-width");
            double height=theParams.getDoubleParam("cont-grid-world-height");
            walkSpeed=theParams.getDoubleParam("cont-grid-world-walk-speed");

            worldRect=new Rectangle2D.Double(minX,minY,width,height);
            agentSize=new Point2D.Double(1.0d,1.0d);
	}

	protected  Rectangle2D makeAgentSizedRectFromPosition(Point2D thePos){
		return new Rectangle2D.Double(thePos.getX(),thePos.getY(),agentSize.getX(),agentSize.getY());
	}
	protected  void updateCurrentAgentRect(){
		currentAgentRect=makeAgentSizedRectFromPosition(agentPos);
	}
	@Override
	protected Observation makeObservation() {
		Observation currentObs= new Observation(0,2);
		currentObs.doubleArray[0]=agentPos.getX();
		currentObs.doubleArray[1]=agentPos.getY();
		return currentObs;
	}

	public void env_cleanup() {

	}

	public Random_seed_key env_save_random_seed() {
		// TODO Auto-generated method stub
		return null;
	}

	public State_key env_save_state() {
		// TODO Auto-generated method stub
		return null;
	}
        /*This used to be the main class, I'm iteratively making it abstract and moving
         * functionality into the DiscreteGridWorld subclass*/

        
	public abstract String env_init();
	public abstract String env_message(String theMessage);

        public void env_load_random_seed(Random_seed_key key) {
		// TODO Auto-generated method stub

	}

	public void env_load_state(State_key key) {
		// TODO Auto-generated method stub

	}

	public Observation env_start() {
		randomizeAgentPosition();

//		setAgentPosition(new Point2D.Double(startX,startY));

		while (calculateMaxBarrierAtPosition(currentAgentRect)>=1.0f||!getWorldRect().contains(currentAgentRect)) {
			randomizeAgentPosition();
		}

		return makeObservation();

	}

	public Reward_observation_terminal env_step(Action action) {
		int theAction=action.intArray[0];
		
		double dx=0;
		double dy=0;
		
		//Should find a good way to abstract actions and add them in like the old wya, that was good
		
		if(theAction==0)dx=walkSpeed;
		if(theAction==1)dx=-walkSpeed;
		if(theAction==2)dy=walkSpeed;
		if(theAction==3)dy=-walkSpeed;

                //Add a small bit of random noise
                double noiseX=.125d*(Math.random()-0.5d);
                double noiseY=.125d*(Math.random()-0.5d);

                dx+=noiseX;
                dy+=noiseY;
		Point2D nextPos=new Point2D.Double(agentPos.getX()+dx,agentPos.getY()+dy);


		nextPos=updateNextPosBecauseOfWorldBoundary(nextPos);
		nextPos=updateNextPosBecauseOfBarriers(nextPos);

		agentPos=nextPos;
		updateCurrentAgentRect();
		boolean inResetRegion=false;

		for(int i=0;i<resetRegions.size();i++){
			if(resetRegions.get(i).contains(currentAgentRect)){
				inResetRegion=true;
			}
		}

		return makeRewardObservation(getReward(),inResetRegion);
	}


	protected double getReward() {
		double reward=0.0d;

		for(int i=0;i<rewardRegions.size();i++){
			if(rewardRegions.get(i).contains(currentAgentRect)){
				reward+=theRewards.get(i);
			}
		}
		return reward;
	}

	protected Rectangle2D getAgent(){
		return currentAgentRect;

	}


	protected void setAgentPosition(Point2D dp){
		this.agentPos=dp;
		updateCurrentAgentRect();
	}

	protected void addResetRegion(Rectangle2D resetRegion){
		resetRegions.add(resetRegion);
	}

	//Penalty is between 0 and 1, its a movement penalty
	protected void addBarrierRegion(Rectangle2D barrierRegion, double penalty){
		barrierRegions.add(barrierRegion);

		assert(penalty>=0);
		assert(penalty<=1);
		thePenalties.add(penalty);
	}

	protected void addRewardRegion(Rectangle2D rewardRegion, double reward){
		rewardRegions.add(rewardRegion);
		theRewards.add(reward);
	}

	protected Rectangle2D getWorldRect(){
		return worldRect;
	}
	protected void randomizeAgentPosition(){
		double startX=Math.random()*getWorldRect().getWidth();
		double startY=Math.random()*getWorldRect().getHeight();
                
                startX=.1;
                startY=.1;

		setAgentPosition(new Point2D.Double(startX,startY));
	}

	protected double calculateMaxBarrierAtPosition(Rectangle2D r) {
		double maxPenalty=0.0f;

		for(int i=0;i<barrierRegions.size();i++){
			if(barrierRegions.get(i).intersects(r)){
				double penalty=thePenalties.get(i);
				if(penalty>maxPenalty)
					maxPenalty=penalty;
			}
		}
		return maxPenalty;
	}

	protected boolean  intersectsResetRegion(Rectangle2D r) {
		for(int i=0;i<resetRegions.size();i++){
			if(resetRegions.get(i).intersects(r)){
				return true;
			}
		}
		return false;
	}

	protected  Point2D updateNextPosBecauseOfBarriers(Point2D nextPos){
		//See if the agent's current position is in a wall, if so we want to impede his movement.
		double penalty=calculateMaxBarrierAtPosition(currentAgentRect);

		double currentX=agentPos.getX();
		double currentY=agentPos.getY();

		double nextX=nextPos.getX();
		double nextY=nextPos.getY();

		double newNextX=currentX+((nextX-currentX)*(1.0f-penalty));
		double newNextY=currentY+((nextY-currentY)*(1.0f-penalty));

		nextPos.setLocation(newNextX,newNextY);
		//Now, find out if he's in an immobile obstacle... and if so move him out
		float fudgeCounter=0;

		Rectangle2D nextPosRect=makeAgentSizedRectFromPosition(nextPos);
		while(calculateMaxBarrierAtPosition(nextPosRect)==1.0f){
			nextPos=findMidPoint(nextPos,agentPos);
			fudgeCounter++;
			if(fudgeCounter==4){
				nextPos=(Point2D) agentPos.clone();
				break;
			}
		}
		return nextPos;
	}

	protected  Point2D findMidPoint(Point2D a,Point2D b){
		double newX=(a.getX()+b.getX())/2.0d;					
		double newY=(a.getY()+b.getY())/2.0d;					
		return new Point2D.Double(newX,newY);
	}

	protected  Point2D updateNextPosBecauseOfWorldBoundary(Point2D nextPos){
		//Gotta do this somewhere
		int fudgeCounter=0;
		Rectangle2D nextPosRect=makeAgentSizedRectFromPosition(nextPos);
		while (!getWorldRect().contains(nextPosRect)) {
			nextPos=findMidPoint(nextPos,agentPos);

			fudgeCounter++;
			if(fudgeCounter==4){
				nextPos=agentPos;
				break;
			}
		}
		return nextPos;
	}

	public double getMaxValueForQuerableVariable(int dimension) {
		if(dimension==0)return getWorldRect().getMaxX();
		return getWorldRect().getMaxY();
	}

	public double getMinValueForQuerableVariable(int dimension) {
		if(dimension==0)return getWorldRect().getMinX();
		return getWorldRect().getMinY();
	}

	public int getNumVars() {
		return 2;
	}

	public Observation getObservationForState(Observation theState) {
		return theState;
	}




}
