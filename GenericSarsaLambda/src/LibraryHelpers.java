import rlglue.Observation;


public class LibraryHelpers {

	static public  Observation cloneObservation(Observation theObs){
		
		Observation newObs=new Observation(theObs.intArray.length, theObs.doubleArray.length);
		for(int i=0;i<theObs.intArray.length;i++) newObs.intArray[i]=theObs.intArray[i];
		for(int i=0;i<theObs.doubleArray.length;i++) newObs.doubleArray[i]=theObs.doubleArray[i];

		return newObs;
	}
}
