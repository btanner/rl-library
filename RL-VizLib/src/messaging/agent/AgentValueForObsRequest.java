package messaging.agent;


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
import visualization.QueryableAgent;

public class AgentValueForObsRequest extends AgentMessages{
	Vector<Observation> theRequestObservations=new Vector<Observation>();

	public AgentValueForObsRequest(GenericMessage theMessageObject) {
		super(theMessageObject);
		
		String thePayLoad=super.getPayLoad();

		StringTokenizer obsTokenizer = new StringTokenizer(thePayLoad, ":");

		String numValuesToken=obsTokenizer.nextToken();
		int numValues=Integer.parseInt(numValuesToken);
		assert(numValues>=0);
		for(int i=0;i<numValues;i++){
			String thisObsString=obsTokenizer.nextToken();
			theRequestObservations.add(UtilityShop.buildObservationFromString(thisObsString));
		}

	}

	public static AgentValueForObsResponse Execute(Vector<Observation> theRequestObservations){
		StringBuffer thePayLoadBuffer= new StringBuffer();

		//Tell them how many
		thePayLoadBuffer.append(theRequestObservations.size());

		for(int i=0;i<theRequestObservations.size();i++){
			thePayLoadBuffer.append(":");
			UtilityShop.serializeObservation(thePayLoadBuffer,theRequestObservations.get(i));
		}

		String theRequest=AbstractMessage.makeMessage(
				MessageUser.kAgent.id(),
				MessageUser.kBenchmark.id(),
				AgentMessageType.kAgentQueryValuesForObs.id(),
				MessageValueType.kStringList.id(),
				thePayLoadBuffer.toString());

		String responseMessage=RLGlue.RL_agent_message(theRequest);

			AgentValueForObsResponse theResponse;
			try {
				theResponse = new AgentValueForObsResponse(responseMessage);
			} catch (NotAnRLVizMessageException e) {
				System.err.println("AgentValueForObsResponse received a non RLViz response");
				theResponse=null;
			}
			
			return theResponse;

	}

	/**
	 * @return the theRequestStates
	 */
	public Vector<Observation> getTheRequestObservations() {
		return theRequestObservations;
	}

	@Override
	public boolean canHandleAutomatically() {
		return true;
	}

	@Override
	public String handleAutomatically(QueryableAgent theAgent) {
		Vector<Double> theValues = new Vector<Double>();

		for(int i=0;i<theRequestObservations.size();i++){
			theValues.add(theAgent.getValueForState(theRequestObservations.get(i)));
		}

		AgentValueForObsResponse theResponse = new AgentValueForObsResponse(theValues);
		String stringResponse=theResponse.makeStringResponse();

		return stringResponse;
	}


}
