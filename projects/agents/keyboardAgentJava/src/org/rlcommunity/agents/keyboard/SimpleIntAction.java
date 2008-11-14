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

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

/**
 *
 * @author Brian Tanner
 */
public class SimpleIntAction extends AbstractAction {

    IntActionReceiver theReceiver;
    private int[] theValues;

    public SimpleIntAction(String name,int theValue, IntActionReceiver theReceiver) {
        super(name);
        this.theReceiver = theReceiver;
        theValues=new int[1];
        this.theValues[0] = theValue;
    }

    public SimpleIntAction(int value, IntActionReceiver theReceiver) {
        this("no name",value, theReceiver);
    }
    
    public SimpleIntAction(String name,int[] values, IntActionReceiver theReceiver) {
        super(name);
        this.theReceiver=theReceiver;
        this.theValues=values;
    }

    public void actionPerformed(ActionEvent e) {
        theReceiver.receiveIntAction(theValues);
        theReceiver.actionFinished();
    }
}
