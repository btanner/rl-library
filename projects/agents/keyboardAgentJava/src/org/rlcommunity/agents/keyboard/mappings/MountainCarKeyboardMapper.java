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
package org.rlcommunity.agents.keyboard.mappings;

import org.rlcommunity.agents.keyboard.*;
import java.awt.event.KeyEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import org.rlcommunity.rlglue.codec.taskspec.TaskSpec;
import org.rlcommunity.rlglue.codec.types.Observation;

/**
 *
 * @author Brian Tanner
 */
public class MountainCarKeyboardMapper extends KeyboardMapper implements IntActionReceiver {

    int nextAction = 0;
    boolean nextActionSet = false;

    public MountainCarKeyboardMapper() {
        super();
    }

    public void ensureTaskSpecMatchesExpectation(TaskSpec TSO) {
        assert (TSO.getNumDiscreteActionDims() == 1);
        assert (TSO.getNumContinuousActionDims() == 0);
        assert (TSO.getDiscreteActionRange(0).getMin() == 0);
        assert (TSO.getDiscreteActionRange(0).getMax() == 2);
    }

    private org.rlcommunity.rlglue.codec.types.Action getAction() {
        while (!nextActionSet) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException ex) {
                Logger.getLogger(MountainCarKeyboardMapper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        org.rlcommunity.rlglue.codec.types.Action theAction = new org.rlcommunity.rlglue.codec.types.Action(1, 0);
        theAction.intArray[0] = nextAction;
        nextActionSet = false;
        return theAction;
    }

    public org.rlcommunity.rlglue.codec.types.Action getAction(Observation theObservation) {
        return getAction();
    }

    public org.rlcommunity.rlglue.codec.types.Action getAction(double theReward, Observation theObservation) {
        return getAction();
    }

    public void receiveIntAction(int dim, int val) {
        assert(dim==0);
        nextAction=val;
        nextActionSet=true;
    }

    public void actionFinished() {
    }

    @Override
    protected void addActions(JComponent theComponent) {
        javax.swing.Action pushCarLeft=new SimpleIntAction(0, this);
        javax.swing.Action pushCarRight=new SimpleIntAction(2, this);
        javax.swing.Action pushCarNeutral=new SimpleIntAction(1, this);
        
        theComponent.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "pushCarLeft");
        theComponent.getActionMap().put("pushCarLeft", pushCarLeft);
        theComponent.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "pushCarRight");
        theComponent.getActionMap().put("pushCarRight", pushCarRight);
        theComponent.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "pushCarNeutral");
        theComponent.getActionMap().put("pushCarNeutral", pushCarNeutral);
    }
}

