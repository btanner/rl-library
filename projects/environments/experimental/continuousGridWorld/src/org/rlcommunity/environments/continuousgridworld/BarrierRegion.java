/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.rlcommunity.environments.continuousgridworld;

import java.awt.Shape;


/**
 *
 * @author btanner
 */
public class BarrierRegion extends Region{
  /** Change this when you make new versions that are not compatible **/
    private static final long serialVersionUID = 1L;

    double movementPenalty=0.0d;

    public BarrierRegion(Shape theRegion, double movementPenalty){
        super(theRegion);
        assert(movementPenalty>=0.0d);
        assert(movementPenalty<=1.0d);
        this.movementPenalty=movementPenalty;
    }

    public double getPenalty(){
        return movementPenalty;
    }
}
