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
import javax.swing.KeyStroke;
import org.rlcommunity.rlglue.codec.taskspec.TaskSpec;

/**
 *
 * @author Brian Tanner
 */
public class CartPoleControlSettings{
    public static void ensureTaskSpecMatchesExpectation(TaskSpec TSO) {
        assert (TSO.getNumDiscreteActionDims() == 1);
        assert (TSO.getNumContinuousActionDims() == 0);
        assert (TSO.getDiscreteActionRange(0).getMin() == 0);
        assert (TSO.getDiscreteActionRange(0).getMax() == 1);
    }

    public static void addActions(JComponent thePanel, IntActionReceiver theTarget) {
        javax.swing.Action pushLeft=new SimpleIntAction("Push Left",0, theTarget);
        javax.swing.Action pushRight=new SimpleIntAction("Push Right",1, theTarget);
        
        thePanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "pushLeft");
        thePanel.getActionMap().put("pushLeft", pushLeft);
        thePanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "pushRight");
        thePanel.getActionMap().put("pushRight", pushRight);


        thePanel.add(new JButton(pushLeft));
        thePanel.add(new JButton(pushRight));
    }
    
    
}

