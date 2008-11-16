/*
 * Copyright 2008 Brian Tanner
 * http://bt-recordbook.googlecode.com/
 * brian@tannerpages.com
 * http://brian.tannerpages.com
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.rlcommunity.environments.mountaincar.visualizer;

import java.util.LinkedList;
import java.util.ListIterator;
import org.rlcommunity.rlglue.codec.types.Observation_action;
import org.rlcommunity.rlglue.codec.types.Reward_observation_action_terminal;

public class PerformanceTracker {

    /** The maximum number of values in the rewardSteps. */
    public static final int MAX_PERF_VALS = 250;
    /** The factor that the lists are measured at. */
    public static final int LIST_FACTOR = 10;
    /** The number of lists in the rewardSteps. */
    public static final int NUM_REWARD_LISTS = 6;
    /** The random seed. */
    private Long storedSeed_ = null;
    /** The reward gained at each step. */
    private LinkedList<Double>[] rewardSteps_;
    /** The cumulative steps. */
    private LinkedList<Double> cumulativeSteps_;
    /** The amount of reward received per episode. */
    private LinkedList<Double> rewardPerEpisode_;
    /** A value counting the number fo steps passed. */
    private int stepsPassed;
    private int totalScore;
    private double rewardThisEpisode = 0;
    /** The episode counter */
    private int episodeCount;

    public void init() {
        rewardSteps_ = new LinkedList[NUM_REWARD_LISTS];
        for (int i = 0; i < NUM_REWARD_LISTS; i++) {
            rewardSteps_[i] = new LinkedList<Double>();
            rewardSteps_[i].add(0.0);
        }
        cumulativeSteps_ = new LinkedList<Double>();
        cumulativeSteps_.add(1d);
        cumulativeSteps_.add(0d);
        rewardPerEpisode_ = new LinkedList<Double>();
        rewardPerEpisode_.add(0d);
        stepsPassed = 0;
        totalScore = 0;
        episodeCount = 1;
    }

    void start(Observation_action ao) {
    }

    private synchronized void update(double reward, boolean terminal) {
        stepsPassed++;
        rewardSteps_[0].add(reward);
        // If the smallest array isn't bursting, use that as the cumulative
        // values.
        if (cumulativeSteps_.getFirst() == 1) {
            if (rewardSteps_[0].size() < MAX_PERF_VALS) {
                cumulativeSteps_.add(reward + cumulativeSteps_.getLast());
            } else {
                cumulativeSteps_.clear();
                cumulativeSteps_.add(LIST_FACTOR * 1d);
            }
        }

        totalScore += reward;
        rewardThisEpisode += reward;
        if (rewardPerEpisode_.size() <= episodeCount) {
            rewardPerEpisode_.add(new Double(0));
        }
        rewardPerEpisode_.set(episodeCount, new Double(rewardThisEpisode));

        if (rewardSteps_[0].size() > MAX_PERF_VALS) {
            rewardSteps_[0].remove();
        }
        if (terminal) {
            episodeCount++;
            rewardThisEpisode = 0;
        }

        propagatePerformances(1);
    }

    public void step(Reward_observation_action_terminal Roat) {
        update(Roat.r,Roat.terminal==1);
    }

    /**
     * Propagates the performances of the arrays upward every N steps where N is
     * the size of the performance array.
     * 
     * @param index
     *            The index of the current list (> 0).
     */
    private synchronized void propagatePerformances(int index) {
        if (index == rewardSteps_.length) {
            return;
        }
        LinkedList<Double> lowerRewards = rewardSteps_[index - 1];
        LinkedList<Double> thisRewards = rewardSteps_[index];
        int moduloNum = (int) Math.pow(LIST_FACTOR, index);

        // Check the cumulative array for matching scale
        if (cumulativeSteps_.getFirst() == moduloNum) {
            if (cumulativeSteps_.size() == 1) {
                double sum = 0;
                for (Double val : thisRewards) {
                    sum += val * moduloNum;
                    cumulativeSteps_.add(sum);
                }
            }

            // Check that this array hasn't overfilled
            if (thisRewards.size() >= MAX_PERF_VALS) {
                cumulativeSteps_.clear();
                cumulativeSteps_.add(new Double(moduloNum * LIST_FACTOR));
            }
        }

        if (stepsPassed % moduloNum == 0) {
            double sum = 0;
            int listIndex = Math.max(0, lowerRewards.size() - LIST_FACTOR);
            ListIterator<Double> iter = lowerRewards.listIterator(listIndex);
            while (iter.hasNext()) {
                sum += iter.next();
            }
            thisRewards.add(sum / LIST_FACTOR);
            if (thisRewards.size() > MAX_PERF_VALS) {
                thisRewards.remove();
            }
            if (cumulativeSteps_.getFirst() == moduloNum) {
                cumulativeSteps_.add((sum * moduloNum / LIST_FACTOR) + cumulativeSteps_.getLast());
            }
            propagatePerformances(index + 1);
        }
    }

    /**
     * Gets the performance values of the agent at a specific resolution.
     * 
     * @param steps
     *            The number of steps the resolution is at.
     * @return A double[] of performance values.
     */
    public synchronized Double[]  getPerformanceValues(int steps) {
        if (steps == -1) {
            return cumulativeSteps_.toArray(new Double[cumulativeSteps_.size()]);
        }
        if (steps == -2) {
            return rewardPerEpisode_.toArray(new Double[rewardPerEpisode_.size()]);
        }
        LinkedList<Double> l = rewardSteps_[(int) Math.log10(steps)];
        return l.toArray(new Double[l.size()]);
    }

    public int getStepsPassed() {
        return stepsPassed;
    }

    public int getTotalScore() {
        return (int) totalScore;
    }

    public int getEpisodeCount() {
        return episodeCount;
    }
}
