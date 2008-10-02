/* 
 * Copyright (C) 2007, Adam White
 * 
http://rl-library.googlecode.com/
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package ChainMDP;

import java.util.StringTokenizer;
import java.util.Vector;


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
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.types.Random_seed_key;
import org.rlcommunity.rlglue.codec.types.Reward_observation;
import org.rlcommunity.rlglue.codec.types.State_key;
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
public class ChainMDP extends EnvironmentBase implements
        getEnvMaxMinsInterface,
        getEnvObsForStateInterface,
        RLVizVersionResponseInterface {

    protected ChainMDPState theState = null;
    protected Vector<ChainMDPState> savedStates = null;
    //Problem parameters have been moved to MountainCar State
    private Random randomGenerator = new Random();
    double shapeDelta = 0;
    int shapeInterval = 0;
    int subGoal = 0;
    double shapeReward = 0.0;
    int count;
    int oldState;
    int shapeMode;
    int length;
    int numActions;
    double rewardPercentage;

    private Random getRandom() {
        return randomGenerator;
    }

    public ChainMDP(ParameterHolder p) {
        super();
        theState = new ChainMDPState(randomGenerator);
        if (p != null) {
            if (!p.isNull()) {
                theState.randomStarts = p.getBooleanParam("randomStartStates");
                shapeDelta = p.getDoubleParam("shapeDelta");
                shapeInterval = p.getIntegerParam("shapeInterval");
                shapeReward = p.getDoubleParam("shapeReward");
                shapeMode = p.getIntegerParam("shapeMode");
                length = p.getIntegerParam("length");
                numActions = p.getIntegerParam("numActions");
                rewardPercentage = p.getDoubleParam("rewardPercentage");

            }
        }

        theState.initState(length, numActions);

    //System.out.println("the state is created");
    }

    public String env_init() {
        //System.out.println("calling init");    
        savedStates = new Vector<ChainMDPState>();
        //This should be like a final static member or something, or maybe it should be configurable... dunno
        int taskSpecVersion = 2;


        return taskSpecVersion + ":e:1_[i]_[0," + (length - 1) + "]:1_[i]_[0," + (numActions - 1) + "]:[-1,0]";
    }

    public Observation env_start() {


        if (theState.randomStarts) {
            int fullRandPosition = (randomGenerator.nextInt() * (0 + Math.abs((length) - 1)) - 0);
            //Want to actually start in a smaller bowl
            theState.index = theState.defaultInitState + fullRandPosition;
        //Want inital velocity = 0.0d;
        } else {
            theState.index = theState.defaultInitState;
        }


        subGoal = 0 + shapeInterval;
        oldState = theState.index;
        count = 0;

        // System.out.println("new trial :"+theState.index);

        return makeObservation();
    }

//	The constants of this height function could easily be parameterized
    public Reward_observation env_step(Action theAction) {

        int a = theAction.intArray[0];

        if (a > numActions - 1 || a < 0) {
            System.err.println("Invalid action selected in chain MDP (max " + numActions + "): " + a);
            a = randomGenerator.nextInt(4);
        }

        count++;

        theState.update(a);

        // System.out.println("subgoal = "+subGoal+"the state = "+ theState.index);


        double r = theState.getReward();
        if (!theState.inGoalRegion() && shapeMode != 0) {
            r = getShapeReward();
        }
        if (theState.inGoalRegion()) {
            shapeReward *= shapeDelta;
        //System.out.println("the end:" + count);
        }
        oldState = theState.index;

        return makeRewardObservation(r, theState.inGoalRegion());
    }

    private double getShapeReward() 
    {
        if (Math.random() < rewardPercentage) 
        {
            if (theState.index == subGoal && shapeMode == 1) 
            {
                double r = shapeReward;
                subGoal += shapeInterval;
                //System.out.println("the shape = "+r);
                return r;
            } 
            else if (theState.index < oldState && shapeMode == 2 /*&& Math.random() < .5*/) 
                return -shapeReward;
            else 
                return 0;
            
        }
        return 0;

    }
    //This method creates the object that can be used to easily set different problem parameters
    public static ParameterHolder getDefaultParameters() {

        ParameterHolder p = new ParameterHolder();

        rlVizLib.utilities.UtilityShop.setVersionDetails(p, new DetailsProvider());
        p.addBooleanParam("randomStartStates", false);
        p.addDoubleParam("shapeReward", 0.0);
        p.addDoubleParam("shapeDelta", 0.0);
        p.addIntegerParam("shapeInterval", 1);
        p.addIntegerParam("shapeMode", 0);
        p.addIntegerParam("length", 4);
        p.addIntegerParam("numActions", 3);
        p.addDoubleParam("rewardPercentage",1.0);



        return p;
    }

    public String env_message(String theMessage) {
        EnvironmentMessages theMessageObject;
        try {
            theMessageObject = EnvironmentMessageParser.parseMessage(theMessage);
        } catch (NotAnRLVizMessageException e) {
            System.err.println("Someone sent CHAINMDP a message that wasn't RL-Viz compatible");
            return "I only respond to RL-Viz messages!";
        }

        if (theMessageObject.canHandleAutomatically(this)) {
            String theResponseString = theMessageObject.handleAutomatically(this);
            return theResponseString;
        }

        System.err.println("We need some code written in Env Message for CHAINMDP.. unknown request received: " + theMessage);
        //Thread.dumpStack();
        return null;
    }

    @Override
    protected Observation makeObservation() {
        Observation currentObs = new Observation(1, 0);

        currentObs.intArray[0] = theState.index;

        return currentObs;
    }

    public ChainMDP() {
        this(getDefaultParameters());
    }

    public void env_cleanup() {
        if (savedStates != null) {
            savedStates.clear();
        }


    }

    //
//This has a side effect, it changes the random order.
//
    public Random_seed_key env_get_random_seed() {
        Random_seed_key k = new Random_seed_key(2, 0);
        long newSeed = getRandom().nextLong();
        getRandom().setSeed(newSeed);
        k.intArray[0] = UtilityShop.LongHighBitsToInt(newSeed);
        k.intArray[1] = UtilityShop.LongLowBitsToInt(newSeed);
        return k;
    }

    public void env_set_random_seed(Random_seed_key k) {
        long storedSeed = UtilityShop.intsToLong(k.intArray[0], k.intArray[1]);
        getRandom().setSeed(storedSeed);
    }

    public State_key env_get_state() {
        savedStates.add(new ChainMDPState(theState));
        State_key k = new State_key(1, 0);
        k.intArray[0] = savedStates.size() - 1;
        return k;
    }

    public void env_set_state(State_key k) {
        int theIndex = k.intArray[0];

        if (savedStates == null || theIndex >= savedStates.size()) {
            System.err.println("Could not set state to index:" + theIndex + ", that's higher than saved size");
            return;
        }
        ChainMDPState oldState = savedStates.get(k.intArray[0]);
        this.theState = new ChainMDPState(oldState);
    }

    public double getMaxValueForQuerableVariable(int dimension) {
        return length - 1;
    }

    public double getMinValueForQuerableVariable(int dimension) {
        return 0;
    }


    //This is really easy in mountainCar because you observe exactly the state
    //Oops, that's not true anymore, we have noise and offsets...
    public Observation getObservationForState(Observation theState) {
        return theState;
    }

    public int getNumVars() {
        return 1;
    }

    public RLVizVersion getTheVersionISupport() {
        return new RLVizVersion(1, 1);
    }
}

class DetailsProvider implements hasVersionDetails {

    public String getName() {
        return "Chain MDP 1.0";
    }

    public String getShortName() {
        return "Chain";
    }

    public String getAuthors() {
        return "Adam White";
    }

    public String getInfoUrl() {
        return "http://rl-library.googlecode.com";
    }

    public String getDescription() {
        return "RL-Library Java Version of a simple chain MDP.";
    }
}

