package messaging;

import java.util.StringTokenizer;

import rlglue.Observation;


public class UtilityShop {
	static public String serializeObservation(Observation theObs){
		String theString="";
		
		theString+=theObs.intArray.length+"_";
		for(int i=0;i<theObs.intArray.length;i++)theString+=theObs.intArray[i]+"_";
		theString+=theObs.doubleArray.length+"_";
		for(int i=0;i<theObs.doubleArray.length;i++)theString+=theObs.doubleArray[i]+"_";
		
		return theString;
	}

	public static Observation buildObservationFromString(String thisObsString) {
		StringTokenizer obsTokenizer = new StringTokenizer(thisObsString, "_");
		
		String intCountToken=obsTokenizer.nextToken();
		int theIntCount=Integer.parseInt(intCountToken);
		int theInts[]=new int[theIntCount];
		
		for(int i=0;i<theInts.length;i++){
			theInts[i]=Integer.parseInt(obsTokenizer.nextToken());
		}

		String doubleCountToken=obsTokenizer.nextToken();
		int theDoubleCount=Integer.parseInt(doubleCountToken);
		double theDoubles[]=new double[theDoubleCount];
		
		for(int i=0;i<theDoubles.length;i++){
			theDoubles[i]=Double.parseDouble(obsTokenizer.nextToken());
		}

		Observation theObs= new Observation(theIntCount, theDoubleCount);
		theObs.intArray=theInts;
		theObs.doubleArray=theDoubles;

		return theObs;
	}
}
