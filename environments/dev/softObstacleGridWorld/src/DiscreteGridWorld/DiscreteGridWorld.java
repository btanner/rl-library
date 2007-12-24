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
package DiscreteGridWorld;

import ContinuousGridWorld.ContinuousGridWorld;
import java.awt.geom.Point2D;
import rlVizLib.general.ParameterHolder;
import rlglue.types.Action;
import rlglue.types.Observation;
import rlglue.types.Reward_observation;

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
public class DiscreteGridWorld extends ContinuousGridWorld {

    protected int xDiscFactor = 25;
    protected int yDiscFactor = 25;

   /**
    * Not going to pass on params from above, we'll go with defaults

    * @return
    */ 	
    
    public DiscreteGridWorld(ParameterHolder theParams){
        super();
        this.xDiscFactor=theParams.getIntegerParam("discrete-gridworld-xgridsize");
        this.yDiscFactor=theParams.getIntegerParam("discrete-gridworld-ygridsize");
    }

    public static ParameterHolder getDefaultParameters(){
		ParameterHolder p = new ParameterHolder();
                p.addIntegerParam("discrete-gridworld-xgridsize",25);
                p.addIntegerParam("discrete-gridworld-ygridsize",25);
                return p;
                
    }
    protected  int getMaxState() {
        int maxState=getState(getWorldRect().getWidth(), getWorldRect().getHeight());
        return maxState;
    }

    /**
     * Return a labeled state from a continuous (x,y) pair.
     * @param x
     * @param y
     * @return
     */
    protected  int getState(double x, double y) {
        int discX = (int) (x / xDiscFactor);
        int discY = (int) (y / yDiscFactor);

        int maxDiscY = (int) (getWorldRect().getHeight() / yDiscFactor);

        int theState = discY * maxDiscY + discX;
        
//        if(theState>200){
//            System.out.println(discX+" = (int)"+x+"/"+xDiscFactor);
//            System.out.println(discY+" = (int)"+y+"/"+yDiscFactor);
//            System.out.println("maxDiscY = "+maxDiscY);
//            
//            System.out.println("theState = "+theState);
//        }
        return theState;
    }

    /**
     * As a hack for now, am actually putting together BOTH the integer and double
     * state, just because the visualizer is dumb and I don't feel like writing 
     * custom messages right now.
     * @param x
     * @param y
     * @return
     */
    protected  Observation makeObservation(double x, double y) {
        Observation currentObs = new Observation(1, 2);
        currentObs.intArray[0] = getState(x, y);
        currentObs.doubleArray[0] = x;
        currentObs.doubleArray[1] = y;

        return currentObs;
    }

    @Override
    protected Observation makeObservation() {
        return makeObservation(agentPos.getX(), agentPos.getY());
    }

    @Override
    public String env_init() {
        int taskSpecVersion = 2;
        String theTaskSpec=taskSpecVersion + ":e:3_[f,f,i]_[";
        theTaskSpec+=getWorldRect().getMinX()+","+getWorldRect().getMaxX()+"]_["+getWorldRect().getMinY()+","+getWorldRect().getMaxY()+"]_";
        theTaskSpec+="_["+0 + "," + getMaxState() + "]:1_[i]_[0,3]:[-1,1]";
        return theTaskSpec;
    }

    protected  void discretizeAgentPos() {
        double x = agentPos.getX();
        double y = agentPos.getY();
        x = xDiscFactor*(x/xDiscFactor);
        y = yDiscFactor*(y/yDiscFactor);
        setAgentPosition(new Point2D.Double(x, y));
    }

    /**
     * This should mostly work out ok, even if things are discretized.  I guess we can 
     * do the work to move him to a discrete spot
     */
    @Override
    public Observation env_start() {
        randomizeAgentPosition();
        discretizeAgentPos();

//		setAgentPosition(new Point2D.Double(startX,startY));

        while (calculateMaxBarrierAtPosition(currentAgentRect) >= 1.0f || !getWorldRect().contains(currentAgentRect)) {
            randomizeAgentPosition();
            discretizeAgentPos();
        }
        return makeObservation();

    }

    @Override
    public Reward_observation env_step(Action action) {
        int theAction = action.intArray[0];

        double dx = 0;
        double dy = 0;

        if (theAction == 0) {
            dx = xDiscFactor;
        }
        if (theAction == 1) {
            dx = -xDiscFactor;
        }
        if (theAction == 2) {
            dy = yDiscFactor;
        }
        if (theAction == 3) {
            dy = -yDiscFactor;
        }

        Point2D nextPos = new Point2D.Double(agentPos.getX() + dx, agentPos.getY() + dy);

        nextPos = updateNextPosBecauseOfWorldBoundary(nextPos);
        nextPos = updateNextPosBecauseOfBarriers(nextPos);

        agentPos = nextPos;
        discretizeAgentPos();
        updateCurrentAgentRect();
        boolean inResetRegion = false;

        for (int i = 0; i < resetRegions.size(); i++) {
            if (resetRegions.get(i).contains(currentAgentRect)) {
                inResetRegion = true;
            }
        }

        return makeRewardObservation(getReward(), inResetRegion);
    }

    @Override
    public Observation getObservationForState(Observation theState) {
        double x = theState.doubleArray[0];
        double y = theState.doubleArray[1];
        return makeObservation(x, y);
    }
}
