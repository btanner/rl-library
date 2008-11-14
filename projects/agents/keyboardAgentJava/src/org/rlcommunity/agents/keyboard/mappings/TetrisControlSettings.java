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
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import org.rlcommunity.rlglue.codec.taskspec.TaskSpec;

/**
 *
 * @author Brian Tanner
 */
public class TetrisControlSettings{
    public static void ensureTaskSpecMatchesExpectation(TaskSpec TSO) {
        assert (TSO.getNumDiscreteActionDims() == 1);
        assert (TSO.getNumContinuousActionDims() == 0);
        assert (TSO.getDiscreteActionRange(0).getMin() == 0);
        assert (TSO.getDiscreteActionRange(0).getMax() == 5);
    }

    public static void addActions(JComponent thePanel, IntActionReceiver theTarget) {

        javax.swing.Action LEFT=new SimpleIntAction("Move Left",0, theTarget);
        javax.swing.Action RIGHT=new SimpleIntAction("Move Right",1, theTarget);
        javax.swing.Action CCW=new SimpleIntAction("Rotate Left",2, theTarget);
        javax.swing.Action CW=new SimpleIntAction("Rotate Right",3, theTarget);
        javax.swing.Action NONE=new SimpleIntAction("Do nothing",4, theTarget);
        javax.swing.Action FALL=new SimpleIntAction("Drop",5, theTarget);
        
        thePanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "LEFT");
        thePanel.getActionMap().put("LEFT", LEFT);
        thePanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "RIGHT");
        thePanel.getActionMap().put("RIGHT", RIGHT);
        thePanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('x'), "CW");
        thePanel.getActionMap().put("CW", CW);
        thePanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('z'), "CCW");
        thePanel.getActionMap().put("CCW", CCW);
        thePanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"), "NONE");
        thePanel.getActionMap().put("NONE", NONE);
        thePanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "FALL");
        thePanel.getActionMap().put("FALL", FALL);
        
        thePanel.add(new JButton(LEFT));
        thePanel.add(new JButton(RIGHT));
        thePanel.add(new JButton(CCW));
        thePanel.add(new JButton(CW));
        thePanel.add(new JButton(NONE));
        thePanel.add(new JButton(FALL));
    }
}

