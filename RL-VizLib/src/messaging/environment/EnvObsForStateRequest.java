package messaging.environment;


import java.util.StringTokenizer;
import java.util.Vector;

import messaging.AbstractMessage;
import messaging.GenericMessage;
import messaging.MessageUser;
import messaging.MessageValueType;
import messaging.NotAnRLVizMessageException;
import rlglue.Observation;
import rlglue.RLGlue;
import utilities.UtilityShop;
import visualization.QueryableEnvironment;

public class EnvObsForStateRequest extends EnvironmentMessages{
	Vector<Observation> theRequestStates=new Vector<Observation>();

	public EnvObsForStateRequest(GenericMessage theMessageObject) {
		super(theMessageObject);

		String thePayLoad=super.getPayLoad();

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
		StringBuffer thePayLoadBuffer= new StringBuffer();

		//Tell them how many
		thePayLoadBuffer.append(theQueryStates.size());

		for(int i=0;i<theQueryStates.size();i++){
			thePayLoadBuffer.append(":");
			UtilityShop.serializeObservation(thePayLoadBuffer,theQueryStates.get(i));
		}

		String theRequest=AbstractMessage.makeMessage(
				MessageUser.kEnv.id(),
				MessageUser.kBenchmark.id(),
				EnvMessageType.kEnvQueryObservationsForState.id(),
				MessageValueType.kStringList.id(),
				thePayLoadBuffer.toString());




		String responseMessage=RLGlue.RL_env_message(theRequest);

		EnvObsForStateResponse theResponse;
		try {
			theResponse = new EnvObsForStateResponse(responseMessage);
		} catch (NotAnRLVizMessageException e) {
			System.err.println("In EnvObsForStateResponse the response was not RL-Viz compatible");
			theResponse=null;
		}

		return theResponse;

	}

	/**
	 * @return the theRequestStates
	 */
	public Vector<Observation> getTheRequestStates() {
		return theRequestStates;
	}

	@Override
	public boolean canHandleAutomatically() {
		return true;
	}

	@Override
	public String handleAutomatically(QueryableEnvironment theEnvironment) {
		Vector<Observation> theObservations= new Vector<Observation>();

		for(int i=0;i<theRequestStates.size();i++){
			Observation thisObs=theEnvironment.getObservationForState(theRequestStates.get(i));
			theObservations.add(thisObs);
		}

		EnvObsForStateResponse theResponse = new EnvObsForStateResponse(theObservations);
		return theResponse.makeStringResponse();

	}

}
