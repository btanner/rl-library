package org.rlcommunity.environment.octopus.components;

import java.io.Serializable;
import org.rlcommunity.environment.octopus.config.*;

public class Target implements Serializable {
    
    private Vector2D position;
    private double value;
    private boolean highlighted;
    
    public Target(TargetSpec spec) {
        position = Vector2D.fromDuple(spec.getPosition());
        value = spec.getReward();
        highlighted = false;
    }
    
    public Vector2D getPosition() {
        return position;
    }
    
    public boolean isHighlighted() {
        return highlighted;
    }
    
    public double getValue() {
        return value;
    }
    
    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
    }
}
