/*
 
This code is based on the source code that Matthew Taylor
provided me. The original source can be obtained from the following
URL: http://teamcore.usc.edu/taylorm/dissertation/index.htm.
A full description of the environment can be found in Matthew's PhD dissertation:

Matthew E. Taylor. Autonomous Inter-Task Transfer in Reinforcement Learning Domains. 
Ph.D. Thesis, Department of Computer Sciences, The University of Texas at Austin, 2008.

This package implements the Knight Joust environment under the
framework of RL-Glue 3.0.

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

import java.util.Random;

/**
 *
 * @author Ioannis Partalas
 */
public class KnightJoustState {

//    private double dist_to_opp;
//    private double angle_west;
//    private double angle_east;
    private int YMAX = 25;
    private int XMAX = 25;
    private int OPPRANDBACK = 5;
    private int OPPRANDSIDE = 10;
    private double rewardAtGoal = 50; // reward when reaching the goal line
    private double rewardForward = 5; // reward when the forward action is selected
    private double rewardOthers = 0; // otherwise get this reward
    private double rewardWhenCaught = 0;
//    private double goalposition = 25;
    private int playerX;
    private int playerY;
    private int oppX;
    private int oppY;
    private double temp_reward;
    private Random rand = new Random();

    public boolean inGoalRegion() {
        if (playerY == YMAX) {
            return true;
        }
        return false;
    }

    double calcAngleEast() {
        if (playerY >= oppY) {
            if (playerX <= oppX) {
                return 180.0;
            } else {
                return 0;
            }
        }

        //else player is below opponent

        //player to Left Edge
        double dCXDelta = 0 - playerX;
        double dCYDelta = 0;
        double dCLen = Math.sqrt(dCXDelta * dCXDelta + dCYDelta * dCYDelta);

        //player to Opp
        double dOXDelta = oppX - playerX;
        double dOYDelta = oppY - playerY;
        double dOLen = Math.sqrt(dOXDelta * dOXDelta + dOYDelta * dOYDelta);

        //get ang(Opponent, Player, Side)
        double dotProd = (dCXDelta * dOXDelta + dCYDelta * dOYDelta) / (dCLen * dOLen);
        double dAngOPS = Math.toDegrees(Math.acos(dotProd));

        return dAngOPS;
    }

    double calcAngleWest() {
        if (playerY >= oppY) {
            if (playerX >= oppX) {
                return 180.0;
            } else {
                return 0;
            }
        }

        //else player is below opponent

        //player to Right Edge
        double dCXDelta = XMAX - playerX;
        double dCYDelta = 0;
        double dCLen = Math.sqrt(dCXDelta * dCXDelta + dCYDelta * dCYDelta);

        //player to Opp
        double dOXDelta = oppX - playerX;
        double dOYDelta = oppY - playerY;
        double dOLen = Math.sqrt(dOXDelta * dOXDelta + dOYDelta * dOYDelta);

        //get ang(Opponent, Player, Side)
        double dotProd = (dCXDelta * dOXDelta + dCYDelta * dOYDelta) / (dCLen * dOLen);
        double dAngOPS = Math.toDegrees(Math.acos(dotProd));

        return dAngOPS;
    }

    //Y dist from player to opponent
    //YMAX if player is behind opponent
    double calcDistToOpponent() {

        double dXD = playerX - oppX;
        double dYD = playerY - oppY;
        return Math.sqrt(dXD * dXD + dYD * dYD);
    }

    void updateState(int action) {
        movePlayer(action);
        moveOpponent();
    }

    public void movePlayer(int iMove) {

        if (iMove == 1) {
            assert (playerX > 1);
            playerX -= 2;
            playerY++;
            temp_reward = rewardOthers;
        } else if (iMove == 2) {
            assert (playerX < XMAX - 1);
            playerX += 2;
            playerY++;
            temp_reward = rewardOthers;
        } else {
            playerY++;
            temp_reward = rewardForward;
        }

        if (playerY == YMAX) {
            temp_reward = rewardAtGoal;
        }

    }

    public void moveOpponent() {
        if (oppY >= playerY) {

            if (oppY > playerY) //oppent above player
            {
                oppY--;
            }

            if (rand.nextInt(OPPRANDSIDE) != 1) {
                if (oppX > playerX) {
                    oppX--;
                } else if (oppX < playerX) {
                    oppX++;
                }
            }
        } else {
            //openet below player
            if (oppX == playerX) {
                //oponent only needs to move up
                if (rand.nextInt(OPPRANDBACK) != 1) {
                    oppY++;
                }
            } else {
                //opoent needs to move up and in X. 4 cases:
                //don't move
                //move X
                //move Y
                //move X & Y
                if (rand.nextInt(2) == 1) {
                    //move X first
                    if (rand.nextInt(OPPRANDSIDE) != 1) {
                        if (oppX > playerX) {
                            oppX--;
                        } else if (oppX < playerX) {
                            oppX++;
                        }
                        //now try to move Y
                        if (rand.nextInt(OPPRANDBACK) != 1) {
                            oppY++;
                        }
                    }
                } else {
                    //move Y first
                    if (rand.nextInt(OPPRANDBACK) != 1) {
                        oppY++;
                        if (rand.nextInt(OPPRANDSIDE) != 1) {
                            if (oppX > playerX) {
                                oppX--;
                            } else if (oppX < playerX) {
                                oppX++;
                            }
                        }
                    }
                }
            }
        }

        if (playerX == oppX && playerY == oppY) {
            temp_reward = rewardWhenCaught;
        }
    }

    void reset() {
        playerX = XMAX / 2;
        playerY = 0;
        oppX = (rand.nextInt(10) - 5) + XMAX / 2;
        oppY = YMAX - (rand.nextInt(1));
    }

    double getReward() {
        return temp_reward;
    }
}
