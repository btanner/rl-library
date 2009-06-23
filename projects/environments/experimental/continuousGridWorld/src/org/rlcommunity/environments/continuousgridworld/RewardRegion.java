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
public class RewardRegion extends Region {
/** Change this when you make new versions that are not compatible **/
    private static final long serialVersionUID = 1L;

    private double rewardValue=0.0d;

    public RewardRegion(Shape theRegion, double rewardValue){
        super(theRegion);
        assert(rewardValue>=-1.0d);
        assert(rewardValue<=1.0d);
        this.rewardValue=rewardValue;
    }

    public double getRewardValue(){
        return rewardValue;
    }
}
