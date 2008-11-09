package org.rlcommunity.environments.cartpole.visualizer;


import org.rlcommunity.environments.cartpole.messages.CartpoleTrackRequest;
import org.rlcommunity.environments.cartpole.messages.CartpoleTrackResponse;
import rlVizLib.general.TinyGlue;
import rlVizLib.visualization.AbstractVisualizer;
import rlVizLib.visualization.VizComponent;
import org.rlcommunity.rlglue.codec.types.Observation;
import rlVizLib.visualization.GenericScoreComponent;
import rlVizLib.visualization.interfaces.GlueStateProvider;

public class CartPoleVisualizer extends AbstractVisualizer implements GlueStateProvider {

    private TinyGlue theGlueState = null;
    private CartpoleTrackResponse trackResponse = null;
    private int lastStateUpdateTimeStep = -1;

    /**
     * Creates a new Cart Pile Visualizer
     * @param theGlueState Global glue state object
     */
    public CartPoleVisualizer(TinyGlue theGlueState) {
        super();
        this.theGlueState = theGlueState;

        VizComponent theTrackVisualizer = new CartPoleTrackComponent(this);
        VizComponent theCartVisualizer = new CartPoleCartComponent(this);

        VizComponent scoreComponent = new GenericScoreComponent(this);

        super.addVizComponentAtPositionWithSize(theTrackVisualizer, 0, 0, 1.0, 1.0);
        super.addVizComponentAtPositionWithSize(theCartVisualizer, 0, 0, 1.0, 1.0);
        super.addVizComponentAtPositionWithSize(scoreComponent, 0, 0, 1.0, 1.0);
    }

    public boolean updateTrack() {
        CartpoleTrackResponse newState = CartpoleTrackRequest.Execute();
        if (!newState.equals(trackResponse)) {
            trackResponse = newState;
            return true;
        }
        return false;
    }

    public boolean updateCart() {
//		CartpoleCartResponse newState= CartpoleCartRequest.Execute();
//		if(!newState.equals(cartResponse)){
//			cartResponse=newState;
//			return true;
//		}
//		return false;
        return true;
    }

    public void updateAgentState() {
    //Don't need to do anything for this because it comes from the state variables
    }

    private void checkCartResponse() {
        if (trackResponse == null) {
            updateTrack();
        }

    }

    public double getLeftCartBound() {
        checkCartResponse();
        return trackResponse.getLeftGoal();
    }

    public double getRightCartBound() {
        checkCartResponse();
        return trackResponse.getRightGoal();
    }

    public double currentXPos() {
        Observation lastObservation = theGlueState.getLastObservation();
        if (lastObservation != null) {
            return lastObservation.doubleArray[0];
        } else {
            return 0.0f;
        }
    }

    public double getAngle() {
        Observation lastObservation = theGlueState.getLastObservation();
        if (lastObservation != null) {
            return lastObservation.doubleArray[2] - 2.0 * Math.PI / 4.0;
        } else {
            return 0.0f;
        }

//		return cartResponse.getAngle()- 2*Math.PI/4;
    }

    public TinyGlue getTheGlueState() {
        return theGlueState;
    }

    public String getName() {
        return "Cart-Pole .9 (DEV)";
    }
}
