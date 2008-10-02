/*
Copyright 2007 Brian Tanner
http://rl-library.googlecode.com/
brian@tannerpages.com
http://brian.tannerpages.com

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

package org.rlcommunity.environments.tetris;

import java.util.Vector;

import org.rlcommunity.environments.tetris.messages.TetrisStateResponse;
import org.rlcommunity.environments.tetris.visualizer.TetrisVisualizer;
import rlVizLib.Environments.EnvironmentBase;
import rlVizLib.general.ParameterHolder;
import rlVizLib.messaging.environment.EnvironmentMessageParser;
import rlVizLib.messaging.environment.EnvironmentMessages;
import rlVizLib.messaging.interfaces.HasAVisualizerInterface;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.types.Random_seed_key;
import org.rlcommunity.rlglue.codec.types.Reward_observation;
import org.rlcommunity.rlglue.codec.types.State_key;
import rlVizLib.general.hasVersionDetails;
import rlVizLib.utilities.UtilityShop;





public class Tetris extends EnvironmentBase implements HasAVisualizerInterface {
    private int currentScore = 0;
    protected TetrisState gameState = null;
    static final int terminalScore = 0;
    Vector<TetrisState> savedStates = new Vector<TetrisState>();

    
    
    public Tetris() {
        this(getDefaultParameters());
    }

    public Tetris(ParameterHolder p) {
        super();
        gameState = new TetrisState();
    }


    /**
     * Tetris doesn't really have any parameters
     * @return
     */
    public static ParameterHolder getDefaultParameters() {
        ParameterHolder p = new ParameterHolder();
        rlVizLib.utilities.UtilityShop.setVersionDetails(p, new DetailsProvider());
        return p;
    }

    /*Base RL-Glue Functions*/
    public String env_init() {
        int boardSize = gameState.getHeight() * gameState.getWidth();
        int numPieces = gameState.possibleBlocks.size();
        int boardSizeObservations = 2;
        int intObsCount = boardSize + numPieces + boardSizeObservations;
        
        //adding the plus 2 because we are sending the weidth and height also
        String task_spec = "2.0:e:" + (boardSize + numPieces + 2) + "_[";
        for (int i = 0; i < intObsCount - 1; i++) {
            task_spec = task_spec + "i,";
        }

        //Add 2 more for the num Rows and num Cols
        task_spec = task_spec + " i]";
        for (int i = 0; i < boardSize + numPieces; i++) {
            task_spec = task_spec + "_[0,1]";
        }
        //Added what the width and height are
        task_spec = task_spec + "_[" + gameState.getHeight() + "," + gameState.getHeight() + "]_[" + gameState.getWidth() + "," + gameState.getWidth() + "]";
        task_spec = task_spec + ":1_[i]_[0,5]:[0,1]";

        return task_spec;
    }

    public Observation env_start() {
        gameState.reset();
        gameState.spawn_block();
        gameState.blockMobile = true;
        currentScore = 0;

        Observation o = gameState.get_observation();
        return o;
    }

    public Reward_observation env_step(Action actionObject) {
        int theAction = 0;
        try{
            theAction=actionObject.intArray[0];
        }catch(ArrayIndexOutOfBoundsException e){
            System.err.println("Error: Action was expected to have 1 dimension but got ArrayIndexOutOfBoundsException when trying to get element 0:"+e);
            System.err.println("Error: Choosing action 0");
            theAction=0;
        }

        if (theAction > 5 || theAction < 0) {
            System.err.println("Invalid action selected in Tetrlais: " + theAction);
            theAction = gameState.getRandom().nextInt(5);
        }

        if (gameState.blockMobile) {
            gameState.take_action(theAction);
            gameState.update();
        } else {
            gameState.spawn_block();
        }

        Reward_observation ro = new Reward_observation();

        ro.terminal = 1;
        ro.o = gameState.get_observation();

        if (!gameState.gameOver()) {
            ro.terminal = 0;
            ro.r = gameState.get_score() - currentScore;
            currentScore = gameState.get_score();
        } else {
            ro.r = Tetris.terminalScore;
            currentScore = 0;
        }

        return ro;
    }

    public void env_cleanup() {
        savedStates.clear();
    }

    public String env_message(String theMessage) {
        EnvironmentMessages theMessageObject;
        try {
            theMessageObject = EnvironmentMessageParser.parseMessage(theMessage);
        } catch (Exception e) {
            System.err.println("Someone sent Tetris a message that wasn't RL-Viz compatible");
            return "I only respond to RL-Viz messages!";
        }

        
        if (theMessageObject.canHandleAutomatically(this)) {
            return theMessageObject.handleAutomatically(this);
        }

        if (theMessageObject.getTheMessageType() == rlVizLib.messaging.environment.EnvMessageType.kEnvCustom.id()) {

            String theCustomType = theMessageObject.getPayLoad();

            if (theCustomType.equals("GETTETRLAISSTATE")) {
                //It is a request for the state
                TetrisStateResponse theResponseObject = new TetrisStateResponse(currentScore, gameState.getWidth(), gameState.getHeight(), gameState.getNumberedStateSnapShot(), gameState.getCurrentPiece());
                return theResponseObject.makeStringResponse();
            }
            System.out.println("We need some code written in Env Message for Tetrlais.. unknown custom message type received");
            Thread.dumpStack();

            return null;
        }

        System.out.println("We need some code written in Env Message for  Tetrlais!");
        Thread.dumpStack();

        return null;
    }

    public State_key env_get_state() {
            savedStates.add(new TetrisState(gameState));
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
            TetrisState oldState = savedStates.get(theIndex);
            this.gameState = new TetrisState(oldState);
    }

 /**
 * Provides a random seed that can be used with env_set_random_seed to sample
 * multiple transitions from a single state.
 * <p>
 * Note that calling this method has a side effect, it creates a new seed and 
 * sets it.
 * @return
 */public Random_seed_key env_get_random_seed() {
            Random_seed_key k = new Random_seed_key(2, 0);
            long newSeed = gameState.getRandom().nextLong();
            gameState.getRandom().setSeed(newSeed);
            k.intArray[0] = UtilityShop.LongHighBitsToInt(newSeed);
            k.intArray[1] = UtilityShop.LongLowBitsToInt(newSeed);
            return k;
    }

    public void env_set_random_seed(Random_seed_key k) {
            long storedSeed = UtilityShop.intsToLong(k.intArray[0], k.intArray[1]);
            gameState.getRandom().setSeed(storedSeed);
    }

    /*End of Base RL-Glue Functions */
    /*RL-Viz Methods*/
    @Override
    protected Observation makeObservation() {
        return gameState.get_observation();
    }

    public String getVisualizerClassName() {
        return TetrisVisualizer.class.getName();
    }


}
class DetailsProvider implements hasVersionDetails {

    public String getName() {
        return "Tetris 1.1";
    }

    public String getShortName() {
        return "Tetris";
    }

    public String getAuthors() {
        return "Brian Tanner, Leah Hackman, Matt Radkie, Andrew Butcher";
    }

    public String getInfoUrl() {
        return "http://code.google.com/p/rl-library/wiki/Tetris";
    }

    public String getDescription() {
        return "Tetris problem from the reinforcement learning library.";
    }
}
