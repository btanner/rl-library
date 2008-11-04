/*
 * Copyright 2008 Brian Tanner
 * http://rl-library.googlecode.com
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
* 
*  $Revision: 151 $
*  $Date: 2008-09-17 20:09:24 -0600 (Wed, 17 Sep 2008) $
*  $Author: brian@tannerpages.com $
*  $HeadURL: https://rl-glue-ext.googlecode.com/svn/trunk/projects/codecs/Java/src/org/rlcommunity/rlglue/codec/types/Observation_action.java $
* 
*/

import org.rlcommunity.rlglue.codec.EnvironmentInterface;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;

import org.rlcommunity.rlglue.codec.types.Reward_observation_terminal;

import org.rlcommunity.rlglue.codec.util.EnvironmentLoader;

/**
 *  This is a very simple environment with discrete observations corresponding to states labeled {0,1,...,19,20}
    The starting state is 10.

    There are 2 actions = {0,1}.  0 decrements the state, 1 increments the state.

    The problem is episodic, ending when state 0 or 20 is reached, giving reward -1 or +1, respectively.  The reward is 0 on 
    all other steps.
 * @author Brian Tanner
 */
public class SampleEnvironment implements EnvironmentInterface {
    private int currentState=10;
    
    public String env_init() {
	   int minState=0;
	   int maxState=20;
	   int minAction=0;
	   int maxAction=1;
	   int minReward=-1;
	   int maxReward=1;
	
       return "2:e:1_[i]_["+minState+","+maxState+"]:1_[i]_["+minAction+","+maxAction+"]:["+minReward+","+maxReward+"]";
    }

    public Observation env_start() {
        currentState=10;

        //This means create an observation with 1 int, no doubles, no chars
        Observation returnObservation=new Observation(1,0,0);
        returnObservation.intArray[0]=currentState;
        return returnObservation;
    }

    public Reward_observation_terminal env_step(Action thisAction) {
        boolean episodeOver=false;
        double theReward=0.0d;
        
        if(thisAction.intArray[0]==0)
            currentState--;
        if(thisAction.intArray[0]==1)
            currentState++;
        
        if(currentState<=0){
            currentState=0;
            theReward=-1.0d;
            episodeOver=true;
        }
        
        if(currentState>=20){
            currentState=20;
            episodeOver=true;
            theReward=1.0d;
        }

        Observation returnObservation=new Observation(1,0,0);
        returnObservation.intArray[0]=currentState;
        
        Reward_observation_terminal returnRewardObs=new Reward_observation_terminal(theReward,returnObservation,episodeOver);
        return returnRewardObs;
    }

    public void env_cleanup() {
    }
    public String env_message(String message) {
		return "I don't know how to respond to your message";
    }
    
   /**
     * This is a trick we can use to make the environment easily loadable.
     * @param args
     */
    public static void main(String[] args){
        EnvironmentLoader theLoader=new EnvironmentLoader(new SampleEnvironment());
        theLoader.run();
    }


}
