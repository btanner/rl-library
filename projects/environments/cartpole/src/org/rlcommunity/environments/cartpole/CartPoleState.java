/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rlcommunity.environments.cartpole;

import java.util.Random;

/**
 *
 * @author btanner
 */
public class CartPoleState {

    final static double GRAVITY = 9.8;
    final static double MASSCART = 1.0;
    final static double MASSPOLE = 0.1;
    final static double TOTAL_MASS = (MASSPOLE + MASSCART);
    final static double LENGTH = 0.5;	  /* actually half the pole's length */

    final static double POLEMASS_LENGTH = (MASSPOLE * LENGTH);
    final static double FORCE_MAG = 10.0;
    final static double TAU = 0.02;	  /* seconds between state updates */

    final static double FOURTHIRDS = 4.0d / 3.0d;
    final static double DEFAULTLEFTCARTBOUND = -2.4;
    final static double DEFAULTRIGHTCARTBOUND = 2.4;
    final static double DEFAULTLEFTANGLEBOUND = -Math.toRadians(12.0d);
    final static double DEFAULTRIGHTANGLEBOUND = Math.toRadians(12.0d);
    double leftCartBound;
    double rightCartBound;
    double leftAngleBound;
    double rightAngleBound;    //State variables

    /*Current Values */
    double x;			/* cart position, meters */

    double x_dot;			/* cart velocity */

    double theta;			/* pole angle, radians */

    double theta_dot;		/* pole angular velocity */

    private int lastAction=0;


    private double noise=0.0d;
    private boolean randomStarts=false;
    private Random theRandom;
    
    CartPoleState(boolean randomStartStates, double transitionNoise, long randomSeed) {
        this.randomStarts=randomStartStates;
        this.noise=transitionNoise;
        if(randomSeed==0){
            theRandom=new Random();
        }else{
            theRandom=new Random(randomSeed);
        }
    }

    void reset() {
        lastAction=0;
        x = 0.0f;
        x_dot = 0.0f;
        theta = 0.0f;
        theta_dot = 0.0f;

        if(randomStarts){
            //Going to have the random start states be near to equilibrium
            x=theRandom.nextDouble()-.5d;
            x_dot=theRandom.nextDouble()-.5d;
            theta=(theRandom.nextDouble()-.5d)/8.0d;
            theta_dot=(theRandom.nextDouble()-.5d)/8.0d;
        }

    }

    void update(int theAction) {
        lastAction=theAction;
        double xacc;
        double thetaacc;
        double force;
        double costheta;
        double sintheta;
        double temp;

        if (theAction > 0) {
            force = FORCE_MAG;
        } else {
            force = -FORCE_MAG;
        }

        //Noise of 1.0 means possibly full opposite action
        double thisNoise=2.0d*noise*FORCE_MAG*(theRandom.nextDouble()-.5d);

        force+=thisNoise;

        costheta = Math.cos(theta);
        sintheta = Math.sin(theta);

        temp = (force + POLEMASS_LENGTH * theta_dot * theta_dot * sintheta) / TOTAL_MASS;

        thetaacc = (GRAVITY * sintheta - costheta * temp) / (LENGTH * (FOURTHIRDS - MASSPOLE * costheta * costheta / TOTAL_MASS));

        xacc = temp - POLEMASS_LENGTH * thetaacc * costheta / TOTAL_MASS;

        /*** Update the four state variables, using Euler's method. ***/
        x += TAU * x_dot;
        x_dot += TAU * xacc;
        theta += TAU * theta_dot;
        theta_dot += TAU * thetaacc;

        /**These probably never happen because the pole would crash **/
        while (theta >= Math.PI) {
            theta -= 2.0d * Math.PI;
        }
        while (theta < -Math.PI) {
            theta += 2.0d * Math.PI;
        }

    }


    public double getX() {
        return x;
    }

    public double getXDot() {
        return x_dot;
    }

    public double getTheta() {
        return theta;
    }

    public double getThetaDot() {
        return theta_dot;
    }

    public int getLastAction(){
        return lastAction;
    }
    /*CART POLE SPECIFIC FUNCTIONS*/

    public boolean inFailure() {
        if (x < leftCartBound || x > rightCartBound || theta < leftAngleBound || theta > rightAngleBound) {
            return true;
        } /* to signal failure */
        return false;
    }

    public double getLeftCartBound() {
        return this.leftCartBound;
    }

    public double getRightCartBound() {
        return this.rightCartBound;
    }

    public double getRightAngleBound() {
        return this.rightAngleBound;
    }

    public double getLeftAngleBound() {
        return this.leftAngleBound;
    }
}
