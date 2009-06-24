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

package org.rlcommunity.environments.continuousgridworld.map;

import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * This is a stupid hack class that is only important because
 * @author btanner
 */
public class SerializableRectangle extends Rectangle2D.Double implements Serializable {

    /** Change this when you make new versions that are not compatible **/
    private static final long serialVersionUID = 1L;

    public SerializableRectangle(double x, double y, double w, double h) {
        super(x, y, w, h);
    }

    /**
     * Always treat de-serialization as a full-blown constructor, by
     * validating the final state of the de-serialized object.
     */
    private void readObject(ObjectInputStream aInputStream) throws ClassNotFoundException, IOException {
        //always perform the default de-serialization first
        x = aInputStream.readDouble();
        y = aInputStream.readDouble();
        width = aInputStream.readDouble();
        height = aInputStream.readDouble();
    }

    /**
     * This is the default implementation of writeObject.
     * Customise if necessary.
     */
    private void writeObject(
            ObjectOutputStream aOutputStream) throws IOException {
        aOutputStream.writeDouble(x);
        aOutputStream.writeDouble(y);
        aOutputStream.writeDouble(width);
        aOutputStream.writeDouble(height);
    }
}
