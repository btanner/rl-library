package utilities;

import java.util.StringTokenizer;

import rlglue.Observation;


public class UtilityShop {
	
	static public double normalizeValue(double theValue, double minPossible, double maxPossible){
			return (theValue-minPossible)/(maxPossible-minPossible);
	}

	static public StringBuffer serializeObservation(StringBuffer theRequestBuffer,Observation theObs) {
		theRequestBuffer.append(theObs.intArray.length);
		theRequestBuffer.append("_");
		for(int i=0;i<theObs.intArray.length;i++){
			theRequestBuffer.append(theObs.intArray[i]);
			theRequestBuffer.append("_");
		}
		theRequestBuffer.append(theObs.doubleArray.length);
		theRequestBuffer.append("_");
		for(int i=0;i<theObs.doubleArray.length;i++){
			theRequestBuffer.append(theObs.doubleArray[i]);
			theRequestBuffer.append("_");
		}
		return theRequestBuffer;
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
