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
package StochasticDiscreteGridWorld;

import DiscreteGridWorld.*;
import ContinuousGridWorld.ContinuousGridWorld;
import java.awt.geom.Point2D;
import java.util.Random;
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
public class StochasticDiscreteGridWorld extends DiscreteGridWorld {

        double randomProb=.1;
        Random theRandom=new Random();
        
        
        public StochasticDiscreteGridWorld(ParameterHolder theParams){
        super(theParams);
        this.randomProb=theParams.getDoubleParam("stochastic-randomprob");
    }

    public static ParameterHolder getDefaultParameters(){
		ParameterHolder p = DiscreteGridWorld.getDefaultParameters();
                p.addDoubleParam("stochastic-randomprob",.1);
                return p;
                
    }

    @Override
    public Reward_observation env_step(Action action) {
        int theAction = action.intArray[0];
        
        boolean chooseRandom=theRandom.nextDouble()<=randomProb;
        
        if(chooseRandom)theAction=theRandom.nextInt(4);
        
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

}
