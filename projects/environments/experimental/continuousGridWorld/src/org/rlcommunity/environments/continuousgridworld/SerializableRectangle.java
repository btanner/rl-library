/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rlcommunity.environments.continuousgridworld;

import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 *
 * @author btanner
 */
public class SerializableRectangle extends Rectangle2D.Double implements Serializable {

    /** Change this when you make new versions that are not compatible **/
    private static final long serialVersionUID = 1L;

    SerializableRectangle(double x, double y, double w, double h) {
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
