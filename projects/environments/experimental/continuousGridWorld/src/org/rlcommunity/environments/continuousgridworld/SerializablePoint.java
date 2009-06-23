/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rlcommunity.environments.continuousgridworld;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 *
 * @author btanner
 */
public class SerializablePoint extends Point2D.Double implements Serializable {

    /** Change this when you make new versions that are not compatible **/
    private static final long serialVersionUID = 1L;

    SerializablePoint(double x, double y) {
        super(x,y);
    }

    /**
     * Always treat de-serialization as a full-blown constructor, by
     * validating the final state of the de-serialized object.
     */
    private void readObject(ObjectInputStream aInputStream) throws ClassNotFoundException, IOException {
        //always perform the default de-serialization first
        x = aInputStream.readDouble();
        y = aInputStream.readDouble();
    }

    /**
     * This is the default implementation of writeObject.
     * Customise if necessary.
     */
    private void writeObject(
            ObjectOutputStream aOutputStream) throws IOException {
        aOutputStream.writeDouble(x);
        aOutputStream.writeDouble(y);
    }
}
