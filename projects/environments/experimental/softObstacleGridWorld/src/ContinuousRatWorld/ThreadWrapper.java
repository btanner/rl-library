//
//  ThreadWrapper.java
//  
//
//  Created by Adam White on 02/09/08.
//  Copyright 2008 __MyCompanyName__. All rights reserved.
//
package ContinuousRatWorld;


import java.io.*;
import java.net.*;
import java.util.*;

public class ThreadWrapper extends Thread{

	Echo rn;
	
	public  ThreadWrapper()
	{
		super("rubious feature thread");
		String args[] = new String[0];
		 rn = new Echo(args, 1 );
	}


    public void run (){
		rn.run();
	}
	

	public double[] getFeatures()
	{
		return rn.getFeatures();
	}
	public int getNumFeatures()
	{
		return rn.getNumFeatures();
	}
	
}