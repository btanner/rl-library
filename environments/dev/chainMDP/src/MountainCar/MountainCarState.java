
package MountainCar;

import java.util.Random;

public class MountainCarState {
//	Current State Information
	public double position;
	public double velocity;

//Some of these are fixed
	public double minPosition = -1.2;
	public double maxPosition = 0.6;
	public double minVelocity = -0.07;    
	public double maxVelocity = 0.07;    
	public double goalPosition = 0.5;

	public double accelerationFactor = 0.001;
	public double gravityFactor = -0.0025;
	public double hillPeakFrequency = 3.0;

	public double defaultInitPosition=-0.5d;
	public double defaultInitVelocity=0.0d;



	public double rewardPerStep=-1.0d;
	public double rewardAtGoal=0.0d;

//These are configurable
	public  boolean randomStarts=false;

	private Random randomGenerator;




            MountainCarState(Random randomGenerator) {
                this.randomGenerator=randomGenerator;
            }

        public MountainCarState(MountainCarState stateToCopy){
            this.position=stateToCopy.position;
            this.velocity=stateToCopy.velocity;
            this.minPosition=stateToCopy.minPosition;
            this.maxPosition=stateToCopy.maxPosition;
            this.minVelocity=stateToCopy.minVelocity;
            this.maxVelocity=stateToCopy.maxVelocity;
            this.goalPosition=stateToCopy.goalPosition;
            this.accelerationFactor=stateToCopy.accelerationFactor;
            this.gravityFactor=stateToCopy.gravityFactor;
            this.hillPeakFrequency=stateToCopy.hillPeakFrequency;
            this.defaultInitPosition=stateToCopy.defaultInitPosition;
            this.defaultInitVelocity=stateToCopy.defaultInitVelocity;
            this.rewardPerStep=stateToCopy.rewardPerStep;
            this.rewardAtGoal=stateToCopy.rewardAtGoal;

            this.randomStarts=stateToCopy.randomStarts;

//These are pointers but that's ok
            this.randomGenerator=stateToCopy.randomGenerator;
        }

        
        //	Stopping condition
	public boolean inGoalRegion(){
		return position >= goalPosition;
	}

    void update(int a, double physicsTerm) {
                double variedAccel=accelerationFactor;

     		velocity += ((a-1))*variedAccel + physicsTerm*getSlope(position)*(gravityFactor);
		if (velocity > maxVelocity) velocity = maxVelocity;
		if (velocity < minVelocity) velocity = minVelocity;
		position += velocity;
		if (position > maxPosition) position = maxPosition;
		if (position < minPosition) position = minPosition;
		if (position==minPosition && velocity<0) velocity = 0;		

    }
    
    	public double getHeightAtPosition(double queryPosition){
		return -Math.sin(hillPeakFrequency*(queryPosition));
	}

	public double getSlope(double queryPosition){
		/*The curve is generated by cos(hillPeakFrequency(x-pi/2)) so the 
		 * pseudo-derivative is cos(hillPeakFrequency* x) 
		 */
		return Math.cos(hillPeakFrequency*queryPosition);
	}
        
        
	public double getReward(){
		if(inGoalRegion())
			return rewardAtGoal;
		else
			return rewardPerStep;
	}


}
