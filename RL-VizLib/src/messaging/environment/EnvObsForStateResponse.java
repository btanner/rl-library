package messaging.environment;

import java.util.StringTokenizer;
import java.util.Vector;

import messaging.AbstractMessage;
import messaging.AbstractResponse;
import messaging.GenericMessage;
import messaging.MessageUser;
import messaging.MessageValueType;
import messaging.NotAnRLVizMessageException;

import rlglue.Observation;
import utilities.UtilityShop;

public class EnvObsForStateResponse extends AbstractResponse{
	private Vector<Observation> theObservations=null;


	public EnvObsForStateResponse(Vector<Observation> theObservations) {
		this.theObservations=theObservations;
	}




	public EnvObsForStateResponse(String responseMessage) throws NotAnRLVizMessageException {
		GenericMessage theGenericResponse = new GenericMessage(responseMessage);

		String thePayLoadString=theGenericResponse.getPayLoad();

		StringTokenizer obsTokenizer = new StringTokenizer(thePayLoadString, ":");

		theObservations = new Vector<Observation>();
		String numValuesToken=obsTokenizer.nextToken();
		int numValues=Integer.parseInt(numValuesToken);

		for(int i=0;i<numValues;i++){
			String thisObsString=obsTokenizer.nextToken();
			theObservations.add(UtilityShop.buildObservationFromString(thisObsString));
		}
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

	//So, when you create on of these in an environment, this gives you the response to send
	public String makeStringResponse() {
		StringBuffer thePayLoadBuffer= new StringBuffer();

		//Tell them how many
		thePayLoadBuffer.append(theObservations.size());

		for(int i=0;i<theObservations.size();i++){
			thePayLoadBuffer.append(":");
			UtilityShop.serializeObservation(thePayLoadBuffer,theObservations.get(i));
		}

		String theResponse=AbstractMessage.makeMessage(
				MessageUser.kBenchmark.id(),
				MessageUser.kEnv.id(),
				EnvMessageType.kEnvResponse.id(),
				MessageValueType.kStringList.id(),
				thePayLoadBuffer.toString());

		return theResponse;
	}
};