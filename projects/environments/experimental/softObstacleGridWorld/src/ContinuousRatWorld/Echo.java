/** Echo outputs to the screen all the messages it receives. 
 * 
 * Developers: Masa
 *
 * Copyright: UCSD, Machine Perception Laboratory,  Javier R. Movellan
 *
 * License:  GPL
 *
 * La Jolla, California, April 5, 2007

 Prints on Screeen Any messages it gets. Helpful for debugging

*/
package ContinuousRatWorld;



import edu.mplab.rubios.node.*;


import java.io.*;
import java.net.*;
import java.util.*;

public class Echo extends RUBIOSNode{

	double [] features = new double[getNumFeatures()];

    public  Echo(String[] args, double mydt){
	super(args, mydt);
    }

    /** parseMessage ignores all incoming messages 
     */
    public boolean parseMessage(String msg){
	return false;
    }


    /** 
     */
    public void nodeDynamics(){
	}

	public double[]  getFeatures()
	{
//parse and return array of doubles
	
	int i=0;
	
	if(nMessagesReceived >0)
	{
	StringTokenizer st = new StringTokenizer(messagesReceived[nMessagesReceived-1],";");
		while(st.hasMoreTokens()) {

			StringTokenizer st2 = new StringTokenizer(st.nextToken(),"=");
			
			while(st2.hasMoreTokens())
			{ 

				StringTokenizer st3 = new StringTokenizer(st2.nextToken(),", { } :");
						
				while(st3.hasMoreTokens()) {			

					try{
					features[i] = Double.parseDouble(st3.nextToken());
					i++;
					}
					catch (NumberFormatException e)
					{}			
				}
			}
		}
	}
	else {
		for( i=0;i<getNumFeatures();i++)
			features[i] = 0.0;
	}
	
	return features;
	}

	public int getNumFeatures()
	{
		return 49;
	}

    public static  void main(String args[]){
	//double myDt = 1; //  Sampling rate in seconds
	
	//Echo rn = new Echo(args, myDt );
	//rn.run();
    }
}
