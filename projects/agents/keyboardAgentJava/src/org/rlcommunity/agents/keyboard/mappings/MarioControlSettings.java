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
import org.rlcommunity.rlglue.codec.types.Action;

/**
 *
 * @author Brian Tanner
 */
public class MarioControlSettings{
    public static void ensureTaskSpecMatchesExpectation(TaskSpec TSO) {
        assert (TSO.getNumDiscreteActionDims() == 3);
        assert (TSO.getNumContinuousActionDims() == 0);
        assert (TSO.getDiscreteActionRange(0).getMin() == 0);
        assert (TSO.getDiscreteActionRange(0).getMax() == 2);
    }

    public static void addActions(JComponent thePanel, IntActionReceiver theTarget) {
        javax.swing.Action walkLeft=new SimpleIntAction("Walk Left",new int[]{-1,0,0}, theTarget);
        javax.swing.Action walkRight=new SimpleIntAction("Walk Right",new int[]{1,0,0}, theTarget);
        javax.swing.Action neutral=new SimpleIntAction("Nothing",new int[]{0,0,0}, theTarget);
        javax.swing.Action jump=new SimpleIntAction("Jump",new int[]{0,1,0}, theTarget);
        
        thePanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "walkLeft");
        thePanel.getActionMap().put("walkLeft", walkLeft);
        thePanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "walkRight");
        thePanel.getActionMap().put("walkRight", walkRight);
        thePanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "nothing");
        thePanel.getActionMap().put("nothing", neutral);
        thePanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "jump");
        thePanel.getActionMap().put("jump", jump);
        
        thePanel.add(new JButton(walkLeft));
        thePanel.add(new JButton(neutral));
        thePanel.add(new JButton(walkRight));
        thePanel.add(new JButton(jump));
    }

    public static Action getActionStructure() {
        return new Action(3,0,0);
    }
}

