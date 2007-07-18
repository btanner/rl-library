package messaging.environment;

import java.util.Vector;

import messaging.AbstractResponse;
import messaging.MessageUser;
import messaging.MessageValueType;
import messaging.UtilityShop;

import rlglue.Observation;

public class EnvObsForStateResponse extends AbstractResponse{
	private Vector<Observation> theObservations;
	

	public EnvObsForStateResponse(Vector<Observation> theObservations) {
		this.theObservations=theObservations;
	}




	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String theResponse="EnvObsForStateResponse: " + theObservations.size()+" observations, they are: (serialized for fun)";
		for(int i=0;i<theObservations.size();i++){
			theResponse+=UtilityShop.serializeObservation(theObservations.get(i))+" ";
		}
		// TODO Auto-generated method stub
		return theResponse;
	}




	/**
	 * @return the theObservations
	 */
	public Vector<Observation> getTheObservations() {
		return theObservations;
	}

//	@Override
	
	//So, when you create on of these in an environment, this gives you the response to send
	public String makeStringResponse() {
		String theResponseString="TO="+MessageUser.kBenchmark.id()+" FROM="+MessageUser.kEnv.id();
		theResponseString+=" CMD="+EnvMessageType.kEnvResponse.id()+" VALTYPE="+MessageValueType.kStringList.id()+" VALS=";
		
		theResponseString+=theObservations.size()+":";
		
		for(int i=0;i<theObservations.size();i++){
			theResponseString+=UtilityShop.serializeObservation(theObservations.get(i))+":";
		}
		
		return theResponseString;
	}
};