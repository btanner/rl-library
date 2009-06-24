/*
 *  Copyright 2009 Brian Tanner.
 *
 *  brian@tannerpages.com
 *  http://research.tannerpages.com
 *
 *  This source file is from one of:
 *  {rl-coda,rl-glue,rl-library,bt-agentlib,rl-viz}.googlecode.com
 *  Check out http://rl-community.org for more information!
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
package org.rlcommunity.environments.continuousgridworld;

import org.rlcommunity.environments.continuousgridworld.map.SerializableRectangle;
import org.rlcommunity.environments.continuousgridworld.map.RewardRegion;
import org.rlcommunity.environments.continuousgridworld.map.BarrierRegion;
import org.rlcommunity.environments.continuousgridworld.map.TerminalRegion;
import java.awt.Shape;
import org.rlcommunity.rlglue.codec.taskspec.TaskSpec;
import org.rlcommunity.rlglue.codec.taskspec.TaskSpecVRLGLUE3;
import org.rlcommunity.rlglue.codec.taskspec.ranges.DoubleRange;
import org.rlcommunity.rlglue.codec.taskspec.ranges.IntRange;
import rlVizLib.general.ParameterHolder;



/*
 *  ContinuousGridWorld
 *
 *  Created by Brian Tanner on 02/03/07.
 *  Copyright 2007 Brian Tanner. All rights reserved.
 *
 */
import rlVizLib.general.hasVersionDetails;
import rlVizLib.messaging.environmentShell.TaskSpecPayload;

public class ContinuousGridWorld extends AbstractContinuousGridWorld {

    public ContinuousGridWorld() {
        this(getDefaultParameters());
    }

    public ContinuousGridWorld(ParameterHolder theParams) {
        super(theParams);
    }

    public static ParameterHolder getDefaultParameters() {
        ParameterHolder myParams = AbstractContinuousGridWorld.getDefaultParameters();
        return myParams;
    }

    protected String makeTaskSpec() {
        TaskSpecVRLGLUE3 theTaskSpecObject = new TaskSpecVRLGLUE3();
        theTaskSpecObject.setEpisodic();
        theTaskSpecObject.setDiscountFactor(1.0d);
        theTaskSpecObject.addContinuousObservation(new DoubleRange(0.0d, 1.0d, theState.getNumVars()));
        theTaskSpecObject.addDiscreteAction(new IntRange(0, 3));
        theTaskSpecObject.setRewardRange(new DoubleRange(-1, 1));
        theTaskSpecObject.setExtra("EnvName:ContinuousGridWorld");
        String taskSpecString = theTaskSpecObject.toTaskSpec();
        TaskSpec.checkTaskSpec(taskSpecString);
        return taskSpecString;

    }

    public static TaskSpecPayload getTaskSpecPayload(ParameterHolder P) {
        ContinuousGridWorld theGridWorld = new ContinuousGridWorld(P);
        String taskSpec = theGridWorld.makeTaskSpec();
        return new TaskSpecPayload(taskSpec, false, "");
    }

    @Override
    protected void addBarriersAndGoal(ParameterHolder theParams) {
        RewardRegion negativeEverywhere = new RewardRegion(new SerializableRectangle(0.0d, 0.0d, 100.0d, 100.0d), -1.0d);
        theState.addRewardState(negativeEverywhere);

        Shape goalRegion = new SerializableRectangle(35.0, 35.0, 12.0, 12.0);

        TerminalRegion goalState = new TerminalRegion(goalRegion);
        theState.addTerminalState(goalState);

        theState.addRewardState(new RewardRegion(goalRegion, 1.0));


        theState.addBarrier(new BarrierRegion(new SerializableRectangle(25.0, 25.0, 5.0, 50.0), 1.0));
        theState.addBarrier(new BarrierRegion(new SerializableRectangle(25.0, 25.0, 50.0, 5.0), 1.0));
        theState.addBarrier(new BarrierRegion(new SerializableRectangle(75.0, 25.0, 5.0, 50.0), 1.0));
    }
}

class DetailsProvider implements hasVersionDetails {

    public String getName() {
        return "Soft Obstacle Continuous Grid World 0.1";
    }

    public String getShortName() {
        return "Cont-Grid-World";
    }

    public String getAuthors() {
        return "Brian Tanner";
    }

    public String getInfoUrl() {
        return "http://library.rl-community.org/environments/continuousgridworld";
    }

    public String getDescription() {
        return "RL-Library Java Version of a continuous grid world with soft obstacles.";
    }
}