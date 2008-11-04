package org.rlcommunity.environments.NonMarkovSplit;

import rlVizLib.Environments.EnvironmentBase;
import rlVizLib.general.ParameterHolder;
import rlVizLib.general.hasVersionDetails;
import rlVizLib.messaging.NotAnRLVizMessageException;
import rlVizLib.messaging.environment.EnvironmentMessageParser;
import rlVizLib.messaging.environment.EnvironmentMessages;
import rlVizLib.messaging.interfaces.HasAVisualizerInterface;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;

import org.rlcommunity.rlglue.codec.types.Reward_observation_terminal;


/* MDP Structure
 *     (r=+1) State1 (O=1) --> State3 (O=3) --> (r=-5) State5 (O=4)
 *     /                            
 * State0 (O=0)
 *     \                             
 *     (r=-1) State2 (O=2)--> State4 (O=3) -->(r=+5) State6 (O=5)
 */
/**
 * This is a skeleton environment project that can be used as a starting point
 * for other projects.
 * 
 * @author btanner
 */
public class NonMarkovSplit extends EnvironmentBase implements HasAVisualizerInterface {

    //State variables
    private int currentState = 0;

    @Override
    protected Observation makeObservation() {
        //1 Integer, 0 doubles
        int theObservation = 0;

        switch (currentState) {
            case 0:
                theObservation = 0;
                break;
            case 1:
                theObservation = 1;
                break;
            case 2:
                theObservation = 2;
                break;
            case 3:
                theObservation = 3;
                break;
            case 4:
                theObservation = 3;
                break;
            case 5:
                theObservation = 4;
                break;
            case 6:
                theObservation = 5;
                break;
        }

        Observation returnObs = new Observation(1, 0);
        returnObs.intArray[0] = theObservation;

        return returnObs;
    }

    public Reward_observation_terminal env_step(Action action) {
        int theAction = action.intArray[0];
        assert (theAction >= 0);
        assert (theAction < 2);
        int terminalState = 0;
        double reward = 0.0d;

        switch (currentState) {
            case 0:
                if (theAction == 0) {
                    currentState = 1;
                    reward = 1.0d;
                }
                if (theAction == 1) {
                    currentState = 2;
                    reward = -1.0d;
                }
                break;
            case 1:
                currentState = 3;
                break;
            case 2:
                currentState = 4;
                break;
            case 3:
                currentState = 5;
                terminalState = 1;
                reward = -5.0d;
                break;
            case 4:
                currentState = 6;
                reward = 5.0d;
                terminalState = 1;
                break;
            case 5:
            case 6:
                System.err.println("Env_step shouldn't be called in state 5 or 6");
                break;
        }

        return new Reward_observation_terminal(reward, makeObservation(), terminalState);
    }

    public NonMarkovSplit() {
        this(getDefaultParameters());
    }

    public NonMarkovSplit(ParameterHolder p) {
        super();
        if (p != null) {
            if (!p.isNull()) {

            }
        }
    }

    public static ParameterHolder getDefaultParameters() {
        ParameterHolder p = new ParameterHolder();
        rlVizLib.utilities.UtilityShop.setVersionDetails(p, new DetailsProvider());
        return p;
    }


    /*RL GLUE METHODS*/
    public String env_init() {
        currentState = 0;

        String taskSpec = "2:e:1_[i]_";
        taskSpec += "[" + 0 + "," + 5 + "]_";
        taskSpec += ":1_[i]_[0,1]:[-5,5]";

        return taskSpec;
    }

    public Observation env_start() {
        currentState = 0;

        return makeObservation();
    }

    public void env_cleanup() {
    }
    public String env_message(String theMessage) {
        EnvironmentMessages theMessageObject;
        try {
            theMessageObject = EnvironmentMessageParser.parseMessage(theMessage);
        } catch (NotAnRLVizMessageException e) {
            System.err.println("Someone sent " + getClass() + " a message that wasn't RL-Viz compatible");
            return "I only respond to RL-Viz messages!";
        }

        if (theMessageObject.canHandleAutomatically(this)) {
            return theMessageObject.handleAutomatically(this);
        }

        System.err.println("We need some code written in Env Message for " + getClass() + " :: unknown request received: " + theMessage);
        Thread.dumpStack();
        return null;
    }
    /*END OF RL_GLUE FUNCTIONS*/
    /*END OF RL-VIZ REQUIREMENTS*/
    public String getVisualizerClassName() {
        return "org.rlcommunity.environments.NonMarkovSplit.visualizer.NonMarkovSplitVisualizer";
    }
}

/**
 * This is a little helper class that fills in the details about this environment
 * for the fancy print outs in the visualizer application.
 * @author btanner
 */
class DetailsProvider implements hasVersionDetails {

    public String getName() {
        return "Skeleton Environment 1.0 Beta";
    }

    public String getShortName() {
        return "Skeleton";
    }

    public String getAuthors() {
        return "Brian Tanner";
    }

    public String getInfoUrl() {
        return "http://code.google.com/p/rl-library/wiki/Skeleton";
    }

    public String getDescription() {
        return "RL-Library Sample Environment.";
    }
}


