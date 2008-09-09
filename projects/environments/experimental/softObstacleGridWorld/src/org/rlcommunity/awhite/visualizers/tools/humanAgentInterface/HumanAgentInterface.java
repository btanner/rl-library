/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.rlcommunity.awhite.visualizers.tools.humanAgentInterface;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JSlider;
import org.rlcommunity.awhite.visualizers.tools.humanAgentInterface.messages.agentRewardRequest;
import rlVizLib.visualization.interfaces.DynamicControlTarget;

/**
 *
 * @author awhite
 */
public class HumanAgentInterface implements ActionListener
{
    //Will have to find a way to easily generalize this and move it to vizlib

    //This is a little interface that will let us dump controls to a panel somewhere.

    javax.swing.JButton rewardButton = null;
    javax.swing.JSlider rewardMagnitude = null;

    javax.swing.JButton punishButton = null;
    javax.swing.JSlider punishMagnitude = null;
    
    public HumanAgentInterface()
    {
    }
            
    
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == rewardButton) {
            double rewardToSend= (double)(rewardMagnitude.getValue());
            agentRewardRequest.Execute(rewardToSend);
            //System.out.println("Sent a reward to the agent");
        }
        if (event.getSource() == punishButton) {
            double rewardToSend= (double)(punishMagnitude.getValue());
            agentRewardRequest.Execute(rewardToSend);
            //System.out.println("Sent a punishment to the agent");
        }      }


    public void addComponents(DynamicControlTarget theControlTarget){
        rewardButton = new JButton();
        rewardButton.setText("Reward");
        rewardButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        rewardButton.addActionListener(this);

        punishButton = new JButton();
        punishButton.setText("Punish");
        punishButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        punishButton.addActionListener(this);        
        
        rewardMagnitude = new JSlider(0, 100, 0);
        rewardMagnitude.setMajorTickSpacing(15);
        rewardMagnitude.setMinorTickSpacing(1);
        rewardMagnitude.setPaintTicks(true);
        rewardMagnitude.setPaintLabels(true);
        rewardMagnitude.setToolTipText("Adjust Magnitude of Reward");
        
        punishMagnitude = new JSlider(-100, 0, 0);
        punishMagnitude.setMajorTickSpacing(15);
        punishMagnitude.setMinorTickSpacing(1);
        punishMagnitude.setPaintTicks(true);
        punishMagnitude.setPaintLabels(true);
        punishMagnitude.setToolTipText("Adjust Magnitude of Punishment");        

        
        
        if (theControlTarget != null) {
            Vector<Component> newComponents = new Vector<Component>();
            JLabel MCControlsLabel = new JLabel("MountainCar Visualizer Controls: ");
            MCControlsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            newComponents.add(MCControlsLabel);
            newComponents.add(rewardMagnitude);
            newComponents.add(rewardButton);
            newComponents.add(punishMagnitude);
            newComponents.add(punishButton);
            
            theControlTarget.addControls(newComponents);
        }        
                
    }


}
