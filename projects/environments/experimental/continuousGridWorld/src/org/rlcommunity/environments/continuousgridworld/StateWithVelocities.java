/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rlcommunity.environments.continuousgridworld;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.Vector;
import org.rlcommunity.rlglue.codec.types.Observation;

/**
 *
 * @author btanner
 */
public class StateWithVelocities extends State {

    /** Change this when you make new versions that are not compatible **/
    private static final long serialVersionUID = 1L;
    private Point2D agentVelocity = new SerializablePoint(0.0d, 0.0d);

    ;
    private double xVelIncrement = .5d;
    private double yVelIncrement = .5d;

    public StateWithVelocities() {
    }

    @Override
    void reset() {
        super.reset();
        agentVelocity = new SerializablePoint(0.0d, 0.0d);
    }

    @Override
    void update(int theAction) {
        impactFromMovement = false;

        double dx = 0;
        double dy = 0;
        //Should find a good way to abstract actions and add them in like the old wya, that was good
        if (theAction == 0) {
            dx = xVelIncrement;
        }
        if (theAction == 1) {
            dx = -xVelIncrement;
        }
        if (theAction == 2) {
            dy = yVelIncrement;
        }
        if (theAction == 3) {
            dy = -yVelIncrement;
        }
        double noiseX = 0.05 * (Math.random() - 0.5);
        double noiseY = 0.05 * (Math.random() - 0.5);
        dx += noiseX;
        dy += noiseY;

        double newXVel = agentVelocity.getX() + dx;
        double newYVel = agentVelocity.getY() + dy;


        //Limits
        if (newXVel > 5.0d) {
            newXVel = 5.0d;
        }
        if (newYVel > 5.0d) {
            newYVel = 5.0d;
        }
        if (newXVel < -5.0d) {
            newXVel = -5.0d;
        }
        if (newYVel < -5.0d) {
            newYVel = -5.0d;
        }


        agentVelocity.setLocation(newXVel, newYVel);

        Point2D currentPos = getAgentPosition();

        Point2D nextPos = new SerializablePoint(currentPos.getX() + agentVelocity.getX(), currentPos.getY() + agentVelocity.getY());
        nextPos = updateNextPosBecauseOfWorldBoundary(nextPos);
        nextPos = updateNextPosBecauseOfBarriers(nextPos);
        setAgentPosition(nextPos);

        if (impactFromMovement) {
            newXVel = -newXVel * Math.random();
            newYVel = -newYVel * Math.random();
        }
        agentVelocity.setLocation(newXVel, newYVel);
        updateCurrentAgentRect();
    }

    @Override
    double getReward() {
        double theReward = super.getReward();

        if (impactFromMovement) {
            theReward -= Math.abs(agentVelocity.getX()) + Math.abs(agentVelocity.getY());
        }
        return theReward;
    }

    @Override
    public Observation makeObservation() {
        Observation currentObs = new Observation(0, 4);
        Point2D theAgent = getAgentPosition();

        //Normalize each to 0,1
        currentObs.doubleArray[0] = theAgent.getX() / 100.0d;
        currentObs.doubleArray[1] = theAgent.getY() / 100.0d;

        currentObs.doubleArray[2] = (agentVelocity.getX() + 5.0d) / 10.0d;
        currentObs.doubleArray[3] = (agentVelocity.getY() + 5.0d) / 10.0d;
        return currentObs;
    }

    @Override
    Observation getObservationForState(Observation theQueryState) {
        //Value function only loops over dims 1 and 2 so we have to add the others.
        Observation theObs = new Observation(0, 4, 0);
        //States are in [0,100], observations are in [0,1]
        theObs.doubleArray[0] = theQueryState.doubleArray[0] / 100.0d;
        theObs.doubleArray[1] = theQueryState.doubleArray[1] / 100.0d;
        //States are in [-5,5], observations are in [0,1]
        //Use the current velocity to draw the VF
//        theObs.doubleArray[2] = (agentVelocity.getX() + 5.0d) / 10.0d;
//        theObs.doubleArray[3] = (agentVelocity.getY() + 5.0d) / 10.0d;

        //Use this code to draw velocity=0 value function.
        theObs.doubleArray[2]=.5d;
        theObs.doubleArray[3]=.5d;
        return theObs;
    }

    @Override
    int getNumVars() {
        return 4;
    }
}
