package messaging.environment;


import java.util.StringTokenizer;
import java.util.Vector;

import messaging.GenericMessage;
import messaging.MessageUser;
import messaging.MessageValueType;
import messaging.UtilityShop;

import rlglue.RLGlue;
import rlglue.Observation;

public class EnvObsForStateRequest extends EnvironmentMessages{
Vector<Observation> theRequestStates=new Vector<Observation>();

	public EnvObsForStateRequest(MessageUser from, MessageUser to, EnvMessageType theMessageType, String thePayLoad) {
		super(from, to, theMessageType);
		
		StringTokenizer obsTokenizer = new StringTokenizer(thePayLoad, ":");

		String numValuesToken=obsTokenizer.nextToken();
		int numValues=Integer.parseInt(numValuesToken);
		assert(numValues>=0);
		for(int i=0;i<numValues;i++){
			String thisObsString=obsTokenizer.nextToken();
			theRequestStates.add(UtilityShop.buildObservationFromString(thisObsString));
		}

	}

	public static EnvObsForStateResponse Execute(Vector<Observation> theQueryStates){

		String theRequest="TO="+MessageUser.kEnv.id()+" FROM="+MessageUser.kBenchmark.id();
		theRequest+=" CMD="+EnvMessageType.kEnvQueryObservationsForState.id()+" VALTYPE="+MessageValueType.kStringList.id()+" VALS=";

		//First send how many
		theRequest+=theQueryStates.size();

		for(int i=0;i<theQueryStates.size();i++)
			theRequest+=":"+UtilityShop.serializeObservation(theQueryStates.get(i));


		String responseMessage=RLGlue.RL_env_message(theRequest);
		GenericMessage theGenericResponse=new GenericMessage(responseMessage);

		String thePayLoadString=theGenericResponse.getPayLoad();

		StringTokenizer obsTokenizer = new StringTokenizer(thePayLoadString, ":");
		Vector<Observation> theObservations = new Vector<Observation>();
		String numValuesToken=obsTokenizer.nextToken();
		int numValues=Integer.parseInt(numValuesToken);
		assert(numValues>=0);
		for(int i=0;i<numValues;i++){
			String thisObsString=obsTokenizer.nextToken();
			theObservations.add(UtilityShop.buildObservationFromString(thisObsString));
		}

		EnvObsForStateResponse theResponse=new EnvObsForStateResponse(theObservations);
		return theResponse;

	}

	/**
	 * @return the theRequestStates
	 */
	public Vector<Observation> getTheRequestStates() {
		return theRequestStates;
	}
}
