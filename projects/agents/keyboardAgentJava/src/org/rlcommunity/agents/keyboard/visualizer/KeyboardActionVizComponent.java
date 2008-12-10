/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rlcommunity.agents.keyboard.visualizer;

import java.util.Observable;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;

import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import org.rlcommunity.agents.keyboard.IntActionReceiver;
import org.rlcommunity.agents.keyboard.SimpleIntAction;
import org.rlcommunity.agents.keyboard.mappings.AcrobotControlSettings;
import org.rlcommunity.agents.keyboard.mappings.CartPoleControlSettings;
import org.rlcommunity.agents.keyboard.mappings.ExpandedCritterControlSettings;
import org.rlcommunity.agents.keyboard.mappings.GridWorldControlSettings;
import org.rlcommunity.agents.keyboard.mappings.MountainCarControlSettings;
import org.rlcommunity.agents.keyboard.mappings.TetrisControlSettings;
import org.rlcommunity.agents.keyboard.messages.TaskSpecRequest;
import org.rlcommunity.agents.keyboard.messages.TellAgentWhatToDoRequest;
import org.rlcommunity.rlglue.codec.taskspec.TaskSpec;
import rlVizLib.general.TinyGlue;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.types.Reward_observation_terminal;
import rlVizLib.visualization.SelfUpdatingVizComponent;
import rlVizLib.visualization.VizComponentChangeListener;
import rlVizLib.visualization.interfaces.DynamicControlTarget;

/**
 *
 * @author btanner
 */
public class KeyboardActionVizComponent implements SelfUpdatingVizComponent, Observer, IntActionReceiver {

    TinyGlue theGlueState = null;
    DynamicControlTarget theControlTarget;
    JPanel theKeyListenerPanel = new JPanel();
    boolean sendNullActions = false;
    int nullActionMSInterval = 200;
    Action nullAction = null;

    public KeyboardActionVizComponent(TinyGlue theGlueState, DynamicControlTarget theControlTarget) {
        this.theControlTarget = theControlTarget;
        this.theGlueState = theGlueState;
        theGlueState.addLastObserver(this);
        javax.swing.Action pushCarLeft = new SimpleIntAction(0, this);
        javax.swing.Action pushCarRight = new SimpleIntAction(2, this);
        javax.swing.Action pushCarNeutral = new SimpleIntAction(1, this);
        theKeyListenerPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "pushCarLeft");
        theKeyListenerPanel.getActionMap().put("pushCarLeft", pushCarLeft);
        theKeyListenerPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "pushCarRight");
        theKeyListenerPanel.getActionMap().put("pushCarRight", pushCarRight);
        theKeyListenerPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "pushCarNeutral");
        theKeyListenerPanel.getActionMap().put("pushCarNeutral", pushCarNeutral);

        theKeyListenerPanel.setBackground(Color.black);

        Vector<Component> theComponents = new Vector<Component>();
        theComponents.add(theKeyListenerPanel);
        theControlTarget.addControls(theComponents);
    }

    public void render(Graphics2D g) {
        //This is some hacky stuff, someone better than me should clean it up
        Font f = new Font("Verdana", 0, 8);
        g.setFont(f);
        //SET COLOR
        g.setColor(Color.BLACK);
        //DRAW STRING
        AffineTransform saveAT = g.getTransform();
        g.scale(.005, .005);

        float currentHeight = 10.0f;
        float heightIncrement = 10.0f;

        currentHeight += heightIncrement;

        //Do action int variables	    
        StringBuffer actStringBuffer = new StringBuffer("Action Ints: ");
        Action lastAction = theGlueState.getLastAction();
        if (lastAction != null) {
            for (int i = 0; i < lastAction.intArray.length; i++) {
                actStringBuffer.append(lastAction.intArray[i]);
                actStringBuffer.append('\t');
            }
            g.drawString(actStringBuffer.toString(), 0.0f, currentHeight);
            currentHeight += heightIncrement;

            //Do double variables
            g.drawString("Action Doubles", 0.0f, currentHeight);
            currentHeight += heightIncrement;
            for (int i = 0; i < lastAction.doubleArray.length; i++) {
                g.drawString("" + lastAction.doubleArray[i], 0.0f, currentHeight);
                currentHeight += heightIncrement;
            }
        }
        g.setTransform(saveAT);
    }
    /**
     * This is the object (a renderObject) that should be told when this component needs to be drawn again.
     */
    private VizComponentChangeListener theChangeListener;

    public void setVizComponentChangeListener(VizComponentChangeListener theChangeListener) {
        this.theChangeListener = theChangeListener;
    }
    /**
     * This will be called when TinyGlue steps.
     * @param o
     * @param arg
     */
    boolean firstStep = true;

    public void update(Observable o, Object arg) {
        if (firstStep) {
            String taskSpec = TaskSpecRequest.Execute().getTaskSpec();
            assert (taskSpec != null);
            setupPanels(taskSpec);
            firstStep = false;
        }
        if (arg instanceof Observation) {
            //RL_env_start just got called
            int[] theAction = waitForAction();
            TellAgentWhatToDoRequest.Execute(theAction);
        }
        if (arg instanceof Reward_observation_terminal) {
            //RL_env_step just got called
            final int[] theAction = waitForAction();
            TellAgentWhatToDoRequest.Execute(theAction);
        }

        //Have this listen for right events from tinyglue and make sure a new action comes
        if (theChangeListener != null) {
            theChangeListener.vizComponentChanged(this);
        }
    }
    private boolean haveNextAction = false;
    private int[] nextAction = new int[0];
    private Object syncLock = new Object();

    public void receiveIntAction(int[] values) {
        synchronized (syncLock) {
            nextAction = values;
            haveNextAction = true;
        }

    }

    public void actionFinished() {
    }
    static String supportedEnvStrings[] = {"EnvName:Mountain-Car", "EnvName:Acrobot",
        "EnvName:ContinuousGridWorld", "EnvName:Tetris", "EnvName:Tetris", "EnvName:ExpandedCritter",
        "EnvName:CartPole"
    };

    public static boolean supportsEnvironment(String taskSpec) {
        for (String thisString : supportedEnvStrings) {
            if (taskSpec.contains(thisString)) {
                return true;
            }
        }
        return false;
    }

    private void setupPanels(String taskSpec) {
        this.sendNullActions = false;

        theControlTarget.removeControl(theKeyListenerPanel);
        theKeyListenerPanel = new JPanel();

        TaskSpec TSO = new TaskSpec(taskSpec);
        String extraString = TSO.getExtraString();
        if (extraString.contains("EnvName:Mountain-Car")) {
            MountainCarControlSettings.addActions(theKeyListenerPanel, this);
        }
        if (extraString.contains("EnvName:Acrobot")) {
            AcrobotControlSettings.addActions(theKeyListenerPanel, this);
        }
        if (extraString.contains("EnvName:ContinuousGridWorld")) {
            GridWorldControlSettings.addActions(theKeyListenerPanel, this);
        }
        if (extraString.contains("EnvName:Tetris")) {
            TetrisControlSettings.addActions(theKeyListenerPanel, this);
        }
        if (extraString.contains("EnvName:CartPole")) {
            CartPoleControlSettings.addActions(theKeyListenerPanel, this);
        }
        if (extraString.contains("EnvName:ExpandedCritter")) {
            ExpandedCritterControlSettings.addActions(theKeyListenerPanel, this);
        }
        if (extraString.contains("EnvName:ExpandedPhysicalCritter")) {
            ExpandedCritterControlSettings.addActions(theKeyListenerPanel, this);
            this.sendNullActions = true;
            this.nullActionMSInterval = 200;
            nullAction = new Action(4, 0, 0);
            nullAction.intArray = new int[]{1, 0, 0, 0};
        } //        if (theKeyBoardMapper != null) {
        //            theKeyBoardMapper.ensureTaskSpecMatchesExpectation(TSO);
        else {
            System.err.println("Didn't know how to make a keyboard agent from string: " + extraString);
        }

        Vector<Component> v = new Vector<Component>();

        v.add(theKeyListenerPanel);
        theControlTarget.addControls(v);

    }
    long lastActionTime = System.currentTimeMillis();

    private int[] waitForAction() {
        int[] theAction = null;
        while (true) {
            synchronized (syncLock) {
                boolean nullTimeOut = (lastActionTime + nullActionMSInterval) < System.currentTimeMillis();
                if (haveNextAction || (sendNullActions && nullTimeOut)) {
                    if (haveNextAction) {
                        theAction = nextAction;
                    } else {
                        if (nullTimeOut && sendNullActions) {
                            theAction = nullAction.intArray;
                        }
                    }
                    haveNextAction = false;
                    lastActionTime = System.currentTimeMillis();
//                    theButtonPanel.add(new JButton("Test"));
//                    theButtonPanel.getParent().invalidate();
                    break;
                }
            }
            try {
                Thread.yield();
            } catch (Exception e) {
                System.err.println("Bad sleep: " + e);
            }
        }
        return theAction;

    }
}