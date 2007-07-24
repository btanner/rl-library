package messaging.environment;

import java.util.Vector;

import messaging.AbstractResponse;
import messaging.MessageUser;
import messaging.MessageValueType;

import rlglue.Observation;
import utilities.UtilityShop;

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
		StringBuffer bufferedResponse=new StringBuffer();
		
		bufferedResponse.append("EnvObsForStateResponse: " + theObservations.size()+" observations, they are: (serialized for fun)");
		
		for(int i=0;i<theObservations.size();i++){
			UtilityShop.serializeObservation(bufferedResponse,theObservations.get(i));
			bufferedResponse.append(" ");
		}

		return bufferedResponse.toString();
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
		StringBuffer theResponseBuffer= new StringBuffer();
		theResponseBuffer.append("TO=");
		theResponseBuffer.append(MessageUser.kBenchmark.id());
		theResponseBuffer.append(" FROM=");
		theResponseBuffer.append(MessageUser.kEnv.id());
		theResponseBuffer.append(" CMD=");
		theResponseBuffer.append(EnvMessageType.kEnvResponse.id());
		theResponseBuffer.append(" VALTYPE=");
		theResponseBuffer.append(MessageValueType.kStringList.id());
		theResponseBuffer.append(" VALS=");
		
		//Tell them how many
		theResponseBuffer.append(theObservations.size());

		for(int i=0;i<theObservations.size();i++){
			theResponseBuffer.append(":");
			UtilityShop.serializeObservation(theResponseBuffer,theObservations.get(i));
		}
		return theResponseBuffer.toString();
	}
};