package messaging.environment;


import java.util.StringTokenizer;
import java.util.Vector;

import messaging.GenericMessage;
import messaging.MessageUser;
import messaging.MessageValueType;
import messaging.UtilityShop;
import messaging.agent.AgentMessageType;

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
		StringBuffer theRequestBuffer= new StringBuffer();
		theRequestBuffer.append("TO=");
		theRequestBuffer.append(MessageUser.kEnv.id());
		theRequestBuffer.append(" FROM=");
		theRequestBuffer.append(MessageUser.kBenchmark.id());
		theRequestBuffer.append(" CMD=");
		theRequestBuffer.append(EnvMessageType.kEnvQueryObservationsForState.id());
		theRequestBuffer.append(" VALTYPE=");
		theRequestBuffer.append(MessageValueType.kStringList.id());
		theRequestBuffer.append(" VALS=");
		
		//Tell them how many
		theRequestBuffer.append(theQueryStates.size());

		for(int i=0;i<theQueryStates.size();i++){
			theRequestBuffer.append(":");
			UtilityShop.serializeObservation(theRequestBuffer,theQueryStates.get(i));
		}
		

		String theRequest=theRequestBuffer.toString();
		
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
