package org.rlcommunity.environments.continuousgridworld;

import java.awt.Shape;
import org.rlcommunity.environments.continuousgridworld.map.BarrierRegion;
import org.rlcommunity.environments.continuousgridworld.map.RewardRegion;
import org.rlcommunity.environments.continuousgridworld.map.SerializableRectangle;
import org.rlcommunity.environments.continuousgridworld.map.TerminalRegion;

/**
 *
 * @author btanner
 */
public class MapGenerator {

    static final int MAP_EMPTY = 1;
    static final int MAP_CUP = 0;
    static final int MAP_MAZE = 2;

    static void makeMap(int mapNumber, State theState) {
        RewardRegion negativeEverywhere = new RewardRegion(new SerializableRectangle(0.0d, 0.0d, 100.0d, 100.0d), -1.0d);
        theState.addRewardState(negativeEverywhere);
        Shape goalRegion = new SerializableRectangle(35.0, 35.0, 12.0, 12.0);
        TerminalRegion goalState = new TerminalRegion(goalRegion);
        theState.addTerminalState(goalState);

        theState.addRewardState(new RewardRegion(goalRegion, 1.0));

        switch (mapNumber) {
            case MAP_EMPTY: // Empty map
                break;
            case MAP_CUP: // Cup map
                theState.addBarrier(new BarrierRegion(new SerializableRectangle(25.0, 25.0, 5.0, 50.0), 1.0));
                theState.addBarrier(new BarrierRegion(new SerializableRectangle(25.0, 25.0, 50.0, 5.0), 1.0));
                theState.addBarrier(new BarrierRegion(new SerializableRectangle(75.0, 25.0, 5.0, 50.0), 1.0));

                break;
            case MAP_MAZE:
                //Left wall with break
                theState.addBarrier(new BarrierRegion(new SerializableRectangle(10.0d, 10.0d, 5.0d, 40.0d), 1.0d));
                theState.addBarrier(new BarrierRegion(new SerializableRectangle(10.0d, 57.5d, 5.0d, 35.0d), 1.0d));
//
//                //Second left wall with opening at the top
                theState.addBarrier(new BarrierRegion(new SerializableRectangle(25.0d, 20.0d, 5.0d, 70.0d), 1.0d));
//
//                //cross wall under the goal
                theState.addBarrier(new BarrierRegion(new SerializableRectangle(25.0d, 55.0d, 35.0d, 5.0d), 1.0d));
//
//
//                //Going down from the cross wall
                theState.addBarrier(new BarrierRegion(new SerializableRectangle(55.0d, 55.0d, 5.0d, 30.0d), 1.0d));
//
//                //second cross wall under the goal
                theState.addBarrier(new BarrierRegion(new SerializableRectangle(40.0d, 70.0d, 15.0d, 5.0d), 1.0d));
//
//                //Going down from the second cross wall
                theState.addBarrier(new BarrierRegion(new SerializableRectangle(40.0d, 70.0d, 5.0d, 15.0d), 1.0d));
//
//                //Correct path goal protector wall
                theState.addBarrier(new BarrierRegion(new SerializableRectangle(55.0d, 32.5d, 5.0d, 30.0d), 1.0d));
//
//                //Top wall
                theState.addBarrier(new BarrierRegion(new SerializableRectangle(10.0d, 10.0d, 80.0d, 5.0d), 1.0d));
//
//                //Second to top wall
                theState.addBarrier(new BarrierRegion(new SerializableRectangle(25.0d, 20.0d, 50.0d, 5.0d), 1.0d));
//
//                //Far right wall
                theState.addBarrier(new BarrierRegion(new SerializableRectangle(85.0d, 10.0d, 5.0d, 80.0d), 1.0d));
//
//                //Second from Far right wall
                theState.addBarrier(new BarrierRegion(new SerializableRectangle(70.0d, 20.0d, 5.0d, 70.0d), 1.0d));
//
//                //Bottom wall with break
               theState.addBarrier(new BarrierRegion(new SerializableRectangle(10.0d, 90.0d, 35.0d, 5.0d), 1.0d));
                theState.addBarrier(new BarrierRegion(new SerializableRectangle(52.5d, 90.0d, 37.5d, 5.0d), 1.0d));
                break;

            default:
                throw new IllegalArgumentException("Map number " + mapNumber);
        }

    }
}
