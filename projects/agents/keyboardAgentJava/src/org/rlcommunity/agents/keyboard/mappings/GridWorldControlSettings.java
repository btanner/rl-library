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
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import org.rlcommunity.rlglue.codec.taskspec.TaskSpec;
import org.rlcommunity.rlglue.codec.types.Observation;

/**
 *
 * @author Brian Tanner
 */
public class GridWorldControlSettings {
    public static void ensureTaskSpecMatchesExpectation(TaskSpec TSO) {
        assert (TSO.getNumDiscreteActionDims() == 1);
        assert (TSO.getNumContinuousActionDims() == 0);
        assert (TSO.getDiscreteActionRange(0).getMin() == 0);
        assert (TSO.getDiscreteActionRange(0).getMax() == 3);
    }

    public static void addActions(JComponent thePanel, IntActionReceiver theTarget) {
        javax.swing.Action goLeft=new SimpleIntAction("Left",1, theTarget);
        javax.swing.Action goRight=new SimpleIntAction("Right",0, theTarget);
        javax.swing.Action goDown=new SimpleIntAction("Down",2, theTarget);
        javax.swing.Action goUp=new SimpleIntAction("Up",3, theTarget);
        
        thePanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "goLeft");
        thePanel.getActionMap().put("goLeft", goLeft);
        thePanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "goRight");
        thePanel.getActionMap().put("goRight", goRight);
        thePanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "goDown");
        thePanel.getActionMap().put("goDown", goDown);
        thePanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "goUp");
        thePanel.getActionMap().put("goUp", goUp);
        
        thePanel.add(new JButton(goLeft));
        thePanel.add(new JButton(goRight));
        thePanel.add(new JButton(goUp));
        thePanel.add(new JButton(goDown));
    }
}

