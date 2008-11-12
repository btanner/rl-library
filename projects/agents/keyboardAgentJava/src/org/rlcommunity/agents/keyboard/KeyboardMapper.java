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

package org.rlcommunity.agents.keyboard;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.rlcommunity.rlglue.codec.taskspec.TaskSpec;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;

/**
 *
 * @author Brian Tanner
 */
public abstract class KeyboardMapper {

    public abstract void ensureTaskSpecMatchesExpectation(TaskSpec TSO);

    public abstract Action getAction(Observation theObservation);
    public abstract Action getAction(double theReward,Observation theObservation);

    private JPanel thePanel;
    
        public KeyboardMapper() {
        JFrame theFrame = new JFrame();
        theFrame.setSize(100, 100);
        theFrame.setVisible(true);

        thePanel=new JPanel();
        theFrame.add(thePanel);
        
        addActions(thePanel);
    }
        protected abstract void addActions(JComponent theComponent);

}
