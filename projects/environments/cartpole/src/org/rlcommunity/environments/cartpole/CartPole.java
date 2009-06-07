package org.rlcommunity.environments.cartpole;

import java.net.URL;
import org.rlcommunity.environments.cartpole.messages.*;
import org.rlcommunity.environments.cartpole.visualizer.CartPoleVisualizer;
import org.rlcommunity.rlglue.codec.taskspec.TaskSpec;
import org.rlcommunity.rlglue.codec.taskspec.TaskSpecVRLGLUE3;
import org.rlcommunity.rlglue.codec.taskspec.ranges.DoubleRange;
import org.rlcommunity.rlglue.codec.taskspec.ranges.IntRange;
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
import org.rlcommunity.rlglue.codec.util.EnvironmentLoader;
import rlVizLib.messaging.interfaces.HasImageInterface;
import rlVizLib.messaging.environmentShell.TaskSpecPayload;

/**
 * This is based on David Finton's code from:
 * http://pages.cs.wisc.edu/~finton/poledriver.html which in turn is credited to
 * The Barto, Sutton, and Anderson cart-pole simulation. 
 * Available (not in 2008) by anonymous ftp from ftp.gte.com, as 
 * /pub/reinforcement-learning/pole.c.
 * Update (May 2009): the original pole.c is available from the UMass RL Repository
 *    Umass RLR: http://www-anw.cs.umass.edu/rlr/domains.html
 *    pole.c in cpole.tar at: http://www-anw.cs.umass.edu/rlr/distcode/cpole.tar
 * @author btanner
 */
public class CartPole extends EnvironmentBase implements HasAVisualizerInterface, HasImageInterface {

    private CartPoleState theState=new CartPoleState();



    public CartPole() {
        this(getDefaultParameters());
    }

    public CartPole(ParameterHolder p) {
        super();
        if (p != null) {
            if (!p.isNull()) {
                theState.leftAngleBound = p.getDoubleParam("leftAngle");
                theState.rightAngleBound = p.getDoubleParam("rightAngle");
                theState.leftCartBound = p.getDoubleParam("leftCart");
                theState.rightCartBound = p.getDoubleParam("rightCart");

            }
        }
    }

    public static TaskSpecPayload getTaskSpecPayload(ParameterHolder P) {
        CartPole theWorld = new CartPole(P);
        String taskSpec = theWorld.makeTaskSpec();
        return new TaskSpecPayload(taskSpec, false, "");
    }

    public static ParameterHolder getDefaultParameters() {
        ParameterHolder p = new ParameterHolder();
        rlVizLib.utilities.UtilityShop.setVersionDetails(p, new DetailsProvider());

        p.addDoubleParam("Left Terminal Angle", CartPoleState.DEFAULTLEFTANGLEBOUND);
        p.addDoubleParam("Right Terminal Angle", CartPoleState.DEFAULTRIGHTANGLEBOUND);
        p.addDoubleParam("Terminal Left Cart Position", CartPoleState.DEFAULTLEFTCARTBOUND);
        p.addDoubleParam("Terminal Right Cart Position", CartPoleState.DEFAULTRIGHTCARTBOUND);

        p.setAlias("leftCart", "Terminal Left Cart Position");
        p.setAlias("rightCart", "Terminal Right Cart Position");
        p.setAlias("leftAngle", "Left Terminal Angle");
        p.setAlias("rightAngle", "Right Terminal Angle");
        return p;
    }


    /*RL GLUE METHODS*/
    public String env_init() {
        theState.reset();

        return makeTaskSpec();
    }

    public Observation env_start() {
        theState.reset();
        return makeObservation();
    }

    public Reward_observation_terminal env_step(Action action) {

        assert(action.intArray.length==1);
        assert(action.intArray[0]>=0);
        assert(action.intArray[0]<=1);

        theState.update(action.intArray[0]);


        if (theState.inFailure()) {
            return new Reward_observation_terminal(-1.0d, makeObservation(), 1);
        } else {
            return new Reward_observation_terminal(1.0d, makeObservation(), 0);
        }
    }

    public void env_cleanup() {
    }

    public String env_message(String theMessage) {
        EnvironmentMessages theMessageObject;
        try {
            theMessageObject = EnvironmentMessageParser.parseMessage(theMessage);
        } catch (NotAnRLVizMessageException e) {
            System.err.println("Someone sent Cartpole a message that wasn't RL-Viz compatible");
            return "I only respond to RL-Viz messages!";
        }

        if (theMessageObject.canHandleAutomatically(this)) {
            return theMessageObject.handleAutomatically(this);
        }

//		If it wasn't handled automatically, maybe its a custom message
        if (theMessageObject.getTheMessageType() == rlVizLib.messaging.environment.EnvMessageType.kEnvCustom.id()) {

            String theCustomType = theMessageObject.getPayLoad();


            if (theCustomType.equals("GETCARTPOLETRACK")) {
                //It is a request for the state
                CartpoleTrackResponse theResponseObject = new CartpoleTrackResponse(theState.getLeftCartBound(), theState.getRightCartBound(), theState.getLeftAngleBound(), theState.getRightAngleBound());
                return theResponseObject.makeStringResponse();
            }
            if (theCustomType.equals("GETCPSTATE")) {
                //It is a request for the state
                StateResponse theResponseObject = new StateResponse(theState.getLastAction(),theState.getX(), theState.getXDot(), theState.getTheta(), theState.getThetaDot());
                return theResponseObject.makeStringResponse();
            }
        }
        System.err.println("We need some code written in Env Message for Cartpole.. unknown request received: " + theMessage);
        Thread.dumpStack();
        return null;
    }

    /*END OF RL_GLUE FUNCTIONS*/

    /*RL-VIZ Requirements*/
    @Override
    protected Observation makeObservation() {
        Observation returnObs = new Observation(0, 4);
        returnObs.doubleArray[0] = theState.getX();
        returnObs.doubleArray[1] = theState.getXDot();
        returnObs.doubleArray[2] = theState.getTheta();
        returnObs.doubleArray[3] = theState.getThetaDot();

        return returnObs;
    }

    /*END OF RL-VIZ REQUIREMENTS*/


    public String getVisualizerClassName() {
        return CartPoleVisualizer.class.getName();
    }
    public URL getImageURL() {
       URL imageURL = CartPole.class.getResource("/images/cartpole.png");
       return imageURL;
   }      



    private String makeTaskSpec() {

        double xMin = theState.getLeftCartBound();
        double xMax = theState.getRightCartBound();

        //Dots are guesses
        double xDotMin = -6.0d;
        double xDotMax = 6.0d;
        double thetaMin = theState.getLeftAngleBound();
        double thetaMax = theState.getRightAngleBound();
        double thetaDotMin = -6.0d;
        double thetaDotMax = 6.0d;

        TaskSpecVRLGLUE3 theTaskSpecObject = new TaskSpecVRLGLUE3();
        theTaskSpecObject.setEpisodic();
        theTaskSpecObject.setDiscountFactor(1.0d);
        theTaskSpecObject.addContinuousObservation(new DoubleRange(xMin, xMax));
        theTaskSpecObject.addContinuousObservation(new DoubleRange(xDotMin, xDotMax));
        theTaskSpecObject.addContinuousObservation(new DoubleRange(thetaMin, thetaMax));
        theTaskSpecObject.addContinuousObservation(new DoubleRange(thetaDotMin, thetaDotMax));
        theTaskSpecObject.addDiscreteAction(new IntRange(0, 1));
        theTaskSpecObject.setRewardRange(new DoubleRange(-1, 0));
        theTaskSpecObject.setExtra("EnvName:CartPole");

        String newTaskSpecString = theTaskSpecObject.toTaskSpec();
        TaskSpec.checkTaskSpec(newTaskSpecString);

        return newTaskSpecString;
    }

    public static void main(String[] args){
        EnvironmentLoader L=new EnvironmentLoader(new CartPole());
        L.run();
    }

    public CartPoleState getState(){
        return theState;
    }
}



/**
 * This is a little helper class that fills in the details about this environment
 * for the fancy print outs in the visualizer application.
 * @author btanner
 */
class DetailsProvider implements hasVersionDetails {

    public String getName() {
        return "Cart-Pole .9 Beta";
    }

    public String getShortName() {
        return "Cart-Pole";
    }

    public String getAuthors() {
        return "Brian Tanner from David Finton from Sutton and Anderson";
    }

    public String getInfoUrl() {
        return "http://library.rl-community.org/cartpole";
    }

    public String getDescription() {
        return "RL-Library Java Version of the classic Cart-Pole RL-Problem.";
    }
}


