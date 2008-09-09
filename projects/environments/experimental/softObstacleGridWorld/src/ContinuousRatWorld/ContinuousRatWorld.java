/*
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package ContinuousRatWorld;

import ContinuousRatWorld.*;

//import edu.mplab.rubios.node.*;

import DiscreteGridWorld.*;
import ContinuousGridWorld.ContinuousGridWorld;
import ContinuousRatWorld.messages.CRWMapResponse;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Random;
import java.util.Vector;
import rlVizLib.general.ParameterHolder;
import rlVizLib.messaging.NotAnRLVizMessageException;
import rlVizLib.messaging.environment.EnvironmentMessageParser;
import rlVizLib.messaging.environment.EnvironmentMessages;
import rlglue.types.Action;
import rlglue.types.Observation;
import rlglue.types.Reward_observation;
import java.io.IOException;


/**
 * This is very much like the Continuous Grid World, but we have a discretized, 
 * labelled integer state representation instead of continuous state variables, and movement
 * is restricted to be a fixed distance in x,y giving us a more traditional 
 * discrete grid world.  
 * <p>
 * Good time to mention, this code isn't actually meant to work with world that
 * don't start at (0,0).  Positive starting positions will just falsely mean
 * more states.  Negative starting positions will break.
 * 
 * @author Brian Tanner
 */
public class ContinuousRatWorld extends ContinuousGridWorld{
    

        
    protected boolean verbose = false;
    
    protected double perStepReward = -0.5;
    protected Random generator;
    protected double wallWidth = 10.0;
    protected double agentOrientation = 0.0;
	protected double width, height;
	ThreadWrapper rn;
	double[] features;
	int numFeatures = 0;

    public static ParameterHolder getDefaultParameters() {
        ParameterHolder p = new ParameterHolder();
        p.addDoubleParam("cont-grid-world-minX", 0.0d);
        p.addDoubleParam("cont-grid-world-minY", 0.0d);
        p.addDoubleParam("cont-grid-world-width", 200.0d);
        p.addDoubleParam("cont-grid-world-height", 200.0d);
        p.addDoubleParam("cont-grid-world-walk-speed", 5.0d);
        p.addDoubleParam("cont-grid-world-wall-width", 10.0d);        
        p.addBooleanParam("verbose", false);

        return p;
    }

    /**
     * Not going to pass on params from above, we'll go with defaults
     * @return
     */
    public ContinuousRatWorld(ParameterHolder theParams)  {         
        double minX = theParams.getDoubleParam("cont-grid-world-minX");
        double minY = theParams.getDoubleParam("cont-grid-world-minY");
        width = theParams.getDoubleParam("cont-grid-world-width");
        height = theParams.getDoubleParam("cont-grid-world-height");
        walkSpeed = theParams.getDoubleParam("cont-grid-world-walk-speed");
        wallWidth = theParams.getDoubleParam("cont-grid-world-wall-width");
        verbose = theParams.getBooleanParam("verbose");

        makeWorld();
        makeAgent();
        makeResetRegion();
        makeRewardRegion();
        makeBarriers();
    }

    protected void makeWorld() {
        worldRect = new Rectangle2D.Double(0, 0, width, height);
    }

    protected void makeAgent() {
        agentSize = new Point2D.Double(1.0, 1.0);
    }

    protected void makeBarriers() 
    {
        addBarrierRegion(new Rectangle2D.Double(width - 75.0, 0.0d,75.0d,wallWidth),1.0d);
        addBarrierRegion(new Rectangle2D.Double(width - wallWidth,0.0d,wallWidth,50.0d),1.0d);            
        addBarrierRegion(new Rectangle2D.Double(width - 75.0,50.0d,75.0d,wallWidth),1.0d);
        addBarrierRegion(new Rectangle2D.Double(width - 75.0, 0.0d,wallWidth,20.0),1.0d);        
        
        addBarrierRegion(new Rectangle2D.Double(width - 75.0,35.0d,wallWidth,15.0),1.0d);        
//        	addBarrierRegion(new Rectangle2D.Double(150.0d,0.0d,200.0d,0.0d),1.0d);
//		addBarrierRegion(new Rectangle2D.Double(50.0d,50.0d,100.0d,10.0d),1.0d);
//		addBarrierRegion(new Rectangle2D.Double(150.0d,50.0d,10.0d,100.0d),1.0d);     
        
    }

    protected void makeResetRegion() {
//		addResetRegion(new Rectangle2D.Double(75.0d,75.0d,25.0d,25.0d));
        addResetRegion(new Rectangle2D.Double(width - 15.0, 20, 20.0d, 20.0d));
    }

    protected void makeRewardRegion() {
//                		addRewardRegion(new Rectangle2D.Double(75.0d,75.0d,25.0d,25.0d),1.0d);        
        addRewardRegion(new Rectangle2D.Double(width - 15.0, 20, 10.0d, 20.0d), 1.0d);
    }

    /**
     * As a hack for now, am actually putting together BOTH the integer and double
     * state, just because the visualizer is dumb and I don't feel like writing 
     * custom messages right now.
     * @param x
     * @param y
     * @return
     */
    protected Observation makeObservation(double x, double y,double z, double[] xx) {
        Observation currentObs = new Observation(0, 3 + numFeatures);
      
        currentObs.doubleArray[0] = x;
        currentObs.doubleArray[1] = y;
        currentObs.doubleArray[2] = z;
		
		for(int i =0;i<numFeatures;i++)
			currentObs.doubleArray[3+i] = xx[i];
		
        return currentObs;
    }

    @Override
    protected Observation makeObservation() {
        return makeObservation(agentPos.getX(), agentPos.getY(),agentOrientation,features);
    }

    @Override
    //FIX TASKSPEC
    public String env_init() {
	
		rn = new ThreadWrapper();
		rn.start();
		numFeatures = rn.getNumFeatures();
		
        int taskSpecVersion = 2;
        String theTaskSpec = taskSpecVersion + ":e:"+(numFeatures+3)+"_[f,f,f";
		for(int i =0;i<numFeatures;i++)
			theTaskSpec +=",f";	
		theTaskSpec += "]_[";		
        theTaskSpec += getWorldRect().getMinX() + "," + getWorldRect().getMaxX() + "]_[" + getWorldRect().getMinY() + "," + getWorldRect().getMaxY() + "]_["+0.0+","+2.0*Math.PI+"]";
		
		for(int i =0;i<numFeatures;i++)
			theTaskSpec +="_[,]";
        theTaskSpec += ":1_[i]_[0,3]:[-1,1]";

        //System.out.println("\n ts = "+theTaskSpec+"\n");
		//System.exit(0);

        generator = new Random();

        return theTaskSpec;
    }

    public String env_message(String theMessage) {
        EnvironmentMessages theMessageObject;
                System.out.println("entering message routine");        
        try {
            theMessageObject = EnvironmentMessageParser.parseMessage(theMessage);
        } catch (NotAnRLVizMessageException e) {
            System.err.println("Someone sent ContinuousRatWorld a message that wasn't RL-Viz compatible");
            return "I only respond to RL-Viz messages!";
        }

        if (theMessageObject.canHandleAutomatically(this)) {
            return theMessageObject.handleAutomatically(this);
        }


//		If it wasn't handled automatically, maybe its a custom Mountain Car Message
        if (theMessageObject.getTheMessageType() == rlVizLib.messaging.environment.EnvMessageType.kEnvCustom.id()) {
            String theCustomType = theMessageObject.getPayLoad();

            if (theCustomType.equals("GETCRWMAP")) {
                //It is a request for the state
                CRWMapResponse theResponseObject = new CRWMapResponse(getWorldRect(), resetRegions, rewardRegions, theRewards, barrierRegions, thePenalties);
                
                System.out.println("leaving message routine");        
                return theResponseObject.makeStringResponse();
            }
        }
        System.err.println("We need some code written in Env Message for ContinuousGridWorld.. unknown request received: " + theMessage);
        Thread.dumpStack();
        return null;
    }

    /**
     * This should mostly work out ok, even if things are discretized.  I guess we can 
     * do the work to move him to a discrete spot
     */
    @Override
    public Observation env_start() {
        randomizeAgentPosition();

		while (calculateMaxBarrierAtPosition(currentAgentRect)>=1.0f||!getWorldRect().contains(currentAgentRect)) {
			randomizeAgentPosition();
		}
        //agentPos.setLocation(0, height);
        agentOrientation = 0.0;//0.1;

        //System.out.println("START: agent(x,y,theta) = ("+agentPos.getX()+","+agentPos.getY()+","+agentOrientation*180.0/Math.PI+")");
        features = rn.getFeatures();
		
        return makeObservation();

    }

    @Override
    public Reward_observation env_step(Action action)   {
        int theAction = action.intArray[0];

        double dx = 0;
        double dy = 0;
        double ratio = 0;
        
        double noiseX = .25d * (Math.random() - 0.5d);
        double noiseY = .25d * (Math.random() - 0.5d);
        double angleNoise = .025d * (Math.random() - 0.05d);


		features = rn.getFeatures();
		//System.out.println("numFeatures = "+numFeatures);
//		int nMessages = rn.getNumFeatures();
//	System.out.println("-------------------");
//	for (int i=0;i< nMessages; i++)
//	   System.out.println(features[i]);		
//	System.out.println("-------------------");
        //System.out.println("agent(x,y,theta) = ("+agentPos.getX()+","+agentPos.getY()+","+agentOrientation*180.0/Math.PI+")");
        //System.out.println("action = " + theAction);
        
        if(theAction<0 || theAction > 3)
        {
            System.out.println("incorrect action, you get random.");
            theAction = (int)(Math.random()*3);
        }
                
        if (theAction == 0) {
            agentOrientation += Math.PI/2.0;
            //agentOrientation += angleNoise;
            
        }
        if (theAction == 1) {
            agentOrientation -= Math.PI/2.0;            
            //agentOrientation += angleNoise;
            
        }
        if (theAction == 3)
        {
            dx = 0;
            dy = 0;
        }
        if(theAction == 2){
            if(agentOrientation == 0.0){
                dx = walkSpeed;
                dy = 0.0;
            }
            else if(agentOrientation == Math.PI/2.0){
                dx = 0.0;
                dy = -walkSpeed;
            }
            else if(agentOrientation == Math.PI){
                dx = -walkSpeed;
                dy = 0.0;
            }
            else if(agentOrientation == 3.0*Math.PI/2.0){
                dx = 0.0;
                dy = walkSpeed;
            }            
            
            
        }
        
//        if (theAction == 2) {
//            if(agentOrientation < Math.PI/2.0){
//                System.out.println("quad 1");
//               
//                ratio=agentOrientation/(Math.PI/2.0);
//                dy = -walkSpeed*ratio;
//                dx = walkSpeed*(1-ratio);
//            
//            }
//            else if(agentOrientation < Math.PI){
//                 System.out.println("quad 2");
//               
//                ratio= (Math.PI - agentOrientation)/(Math.PI/2.0);
//                dx = -walkSpeed*ratio;
//                dy = -walkSpeed*(1-ratio);
//            }
//       
//            else if(agentOrientation < 3.0*Math.PI/2.0){
//                System.out.println("quad 3");
//                
//                ratio=(3.0*Math.PI/2.0 - agentOrientation)/(Math.PI/2.0);
//                dy = walkSpeed*ratio;
//                dx = -walkSpeed*(1-ratio);            
//            
//            }
//        
//            else if(agentOrientation < 2.0*Math.PI){
//                System.out.println("quad 4");
//                
//                ratio=(2.0*Math.PI - agentOrientation)/(Math.PI/2.0);
//                dx = walkSpeed*ratio;
//                dy = walkSpeed*(1-ratio);                
//            }
//                System.out.println("ratio = " + ratio);
// 
//                System.out.println("before noise "+dx+","+dy);
//                
//            dx += noiseX;
//            dy += noiseY;
//
//                            System.out.println("after noise "+dx+","+dy);
//
//        }
//
//
        
        
          //  dx += noiseX;
          //  dy += noiseY;        
        if(agentOrientation >= 2.0*Math.PI) agentOrientation = agentOrientation - 2.0*Math.PI;        
        if(agentOrientation < 0.0) agentOrientation = 2.0*Math.PI + agentOrientation;
        





        Point2D nextPos = new Point2D.Double(agentPos.getX() + dx, agentPos.getY() + dy);

        boolean agentFail = false;
        double thisReward = perStepReward;

        nextPos = updateNextPosBecauseOfWorldBoundary(nextPos);
        nextPos=updateNextPosBecauseOfBarriers(nextPos);
        
        Rectangle2D tmpAgentRec = currentAgentRect = makeAgentSizedRectFromPosition(nextPos);

        agentPos = nextPos;
        boolean inResetRegion = false;
        if(currentAgentRect.intersects(resetRegions.get(0)) /*&& theAction == 3*/) {
                
            inResetRegion = true;
            thisReward = 0.0;//1000.0;
        }

        updateCurrentAgentRect();

       //System.out.println("NOW: agent(x,y,theta) = ("+agentPos.getX()+","+agentPos.getY()+","+agentOrientation*180.0/Math.PI+")");
        //System.out.println("-------------");

        return makeRewardObservation(thisReward, inResetRegion);
    }

//    @Override
    public int getNumVars() {
       return 3 + numFeatures;
    }

    @Override
    public Observation getObservationForState(Observation theState) {

        //System.out.printf("the size is :: %d\n",theState.doubleArray.length);
        double x = theState.doubleArray[0];
        double y = theState.doubleArray[1];
        double z = agentOrientation;
		double [] xx = new double[numFeatures];
		for (int i=0;i<numFeatures;i++)
			xx[i] = features[i];

       
        return makeObservation(x, y,z,xx);
    }

    	protected void randomizeAgentPosition(){
		double startX=Math.random()*getWorldRect().getWidth();
		double startY=Math.random()*getWorldRect().getHeight();
                
                startX= 10.0;//1.0;
                startY= 50.0;//height-1.0;

		setAgentPosition(new Point2D.Double(startX,startY));
	}
        
    protected boolean agentOutOfBound(Point2D nextPos) {
        if (nextPos.getX() < 0 || nextPos.getX() > width || nextPos.getY() < 0 || nextPos.getY() > height) {
            return true;
        }

        return false;
    }


    public String getVisualizerClassName() {
        return "visualizers.ContinuousRatWorld.ContinuousRatWorldVisualizer";
    }
}
