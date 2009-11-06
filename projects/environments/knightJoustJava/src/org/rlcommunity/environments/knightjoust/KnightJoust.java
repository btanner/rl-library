/*
Copyright 2009 Ioannis Partalas
http://rl-library.googlecode.com/
http://users.auth.gr/~partalas/

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
package org.rlcommunity.environments.knightjoust;

/**
 *
 * @author Ioannis Partalas
 */
import java.net.URL;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.taskspec.*;
import org.rlcommunity.rlglue.codec.types.Reward_observation_terminal;

import rlVizLib.Environments.EnvironmentBase;
import org.rlcommunity.rlglue.codec.taskspec.ranges.DoubleRange;
import org.rlcommunity.rlglue.codec.taskspec.ranges.IntRange;

import java.util.Random;
import rlVizLib.general.ParameterHolder;
import rlVizLib.messaging.environmentShell.TaskSpecPayload;
import rlVizLib.messaging.interfaces.HasImageInterface;

public class KnightJoust extends EnvironmentBase implements HasImageInterface {

    final protected KnightJoustState theState;
    private Random rand = new Random();

    public KnightJoust(ParameterHolder P) {
        theState=new KnightJoustState();
    }

    public KnightJoust() {
        this(getDefaultParameters());
    }

    //The following code is just some technical stuff so that we can check agent/env
    //compatibility in RL-Viz, and so we can later easily add parameters if we like.
    public static ParameterHolder getDefaultParameters() {
        return new ParameterHolder();
    }

    public static TaskSpecPayload getTaskSpecPayload(ParameterHolder P) {
        KnightJoust theEnv = new KnightJoust(P);
        String taskSpecString = theEnv.makeTaskSpec().getStringRepresentation();
        return new TaskSpecPayload(taskSpecString, false, "");
    }

    private TaskSpec makeTaskSpec() {
        TaskSpecVRLGLUE3 theTaskSpecObject = new TaskSpecVRLGLUE3();
        theTaskSpecObject.setEpisodic();
        theTaskSpecObject.setDiscountFactor(1.0d);

        theTaskSpecObject.addContinuousObservation(new DoubleRange(0.0, 34.0));
        theTaskSpecObject.addContinuousObservation(new DoubleRange(0.0, 180.0));
        theTaskSpecObject.addContinuousObservation(new DoubleRange(0.0, 180.0));


        theTaskSpecObject.addDiscreteAction(new IntRange(0, 2));
        theTaskSpecObject.setRewardRange(new DoubleRange(0, 50));
        theTaskSpecObject.setExtra("EnvName:KnightJoust:" + this.getClass().getPackage().getImplementationVersion());

        String taskSpecString = theTaskSpecObject.toTaskSpec();
        TaskSpec.checkTaskSpec(taskSpecString);

        return new TaskSpec(theTaskSpecObject);

    }

    public String env_init() {
        return makeTaskSpec().getStringRepresentation();
    }

    public Observation env_start() {
        theState.reset();

        return makeObservation();
    }

    public Reward_observation_terminal env_step(Action theAction) {

        int a = theAction.intArray[0];

        if (a > 2 || a < 0) {
            System.err.println("Invalid action selected in KnightJoust: " + a);
            a = rand.nextInt(2);
        }

        theState.updateState(a);

        return makeRewardObservation(theState.getReward(), theState.inGoalRegion());
    }

    @Override
    protected Observation makeObservation() {
        Observation currentObs = new Observation(0, 3);

        currentObs.doubleArray[0] = theState.calcDistToOpponent();
        currentObs.doubleArray[1] = theState.calcAngleEast();
        currentObs.doubleArray[2] = theState.calcAngleWest();


        return currentObs;
    }

    public String env_message(String theMessage) {
        return "Knight Joust does not respond to messages.";

    }

    public void env_cleanup() {
    }

    public URL getImageURL() {
        return this.getClass().getResource("/images/splash.png");
    }
}
