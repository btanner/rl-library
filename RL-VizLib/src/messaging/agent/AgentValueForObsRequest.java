package messaging.agent;


import java.util.StringTokenizer;
import java.util.Vector;

import messaging.GenericMessage;
import messaging.MessageUser;
import messaging.MessageValueType;
import messaging.UtilityShop;

import rlglue.RLGlue;
import rlglue.Observation;

public class AgentValueForObsRequest extends AgentMessages{
Vector<Observation> theRequestObservations=new Vector<Observation>();

	public AgentValueForObsRequest(MessageUser from, MessageUser to, AgentMessageType theMessageType, String thePayLoad) {
		super(from, to, theMessageType);
		
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

		String theRequest="TO="+MessageUser.kAgent.id()+" FROM="+MessageUser.kBenchmark.id();
		theRequest+=" CMD="+AgentMessageType.kAgentQueryValuesForObs.id()+" VALTYPE="+MessageValueType.kStringList.id()+" VALS=";

		//First send how many
		theRequest+=theRequestObservations.size();

		for(int i=0;i<theRequestObservations.size();i++)
			theRequest+=":"+UtilityShop.serializeObservation(theRequestObservations.get(i));


		long time1=System.currentTimeMillis();
		String responseMessage=RLGlue.RL_agent_message(theRequest);
		long time2=System.currentTimeMillis();
		GenericMessage theGenericResponse=new GenericMessage(responseMessage);

		String thePayLoadString=theGenericResponse.getPayLoad();

		StringTokenizer valueTokenizer = new StringTokenizer(thePayLoadString, ":");
		Vector<Double> theValues = new Vector<Double>();
		String numValuesToken=valueTokenizer.nextToken();
		int numValues=Integer.parseInt(numValuesToken);
		assert(numValues>=0);
		for(int i=0;i<numValues;i++){
			theValues.add(Double.parseDouble(valueTokenizer.nextToken()));
		}

		AgentValueForObsResponse theResponse=new AgentValueForObsResponse(theValues);
		
		long time3=System.currentTimeMillis();
		
		System.out.println("Time to actually send and receive: "+(time2-time1)+" and time to parse response was: "+(time3-time2));

		return theResponse;

	}

	/**
	 * @return the theRequestStates
	 */
	public Vector<Observation> getTheRequestObservations() {
		return theRequestObservations;
	}
}
