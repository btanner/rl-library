package messaging.agent;


import java.util.StringTokenizer;
import java.util.Vector;

import messaging.GenericMessage;
import messaging.MessageUser;
import messaging.MessageValueType;

import rlglue.RLGlue;
import rlglue.Observation;
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
		long time0=System.currentTimeMillis();

		StringBuffer theRequestBuffer= new StringBuffer();
		theRequestBuffer.append("TO=");
		theRequestBuffer.append(MessageUser.kAgent.id());
		theRequestBuffer.append(" FROM=");
		theRequestBuffer.append(MessageUser.kBenchmark.id());
		theRequestBuffer.append(" CMD=");
		theRequestBuffer.append(AgentMessageType.kAgentQueryValuesForObs.id());
		theRequestBuffer.append(" VALTYPE=");
		theRequestBuffer.append(MessageValueType.kStringList.id());
		theRequestBuffer.append(" VALS=");

		//Tell them how many
		theRequestBuffer.append(theRequestObservations.size());

		long time1=System.currentTimeMillis();

		for(int i=0;i<theRequestObservations.size();i++){
			theRequestBuffer.append(":");
			UtilityShop.serializeObservation(theRequestBuffer,theRequestObservations.get(i));
		}

		String theRequest=theRequestBuffer.toString();

		long time2=System.currentTimeMillis();
		String responseMessage=RLGlue.RL_agent_message(theRequest);
		long time3=System.currentTimeMillis();
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

		long time4=System.currentTimeMillis();

		boolean printTiming=false;
		if(printTiming){
			System.out.println("===================================");

			System.out.println("timing summary for Getting values:");
			System.out.println("===================================");

			System.out.println("Preamble before serialization:  "+(time1-time0));
			System.out.println("Serialization:  "+(time2-time1));
			System.out.println("Nework query + response:  "+(time3-time2));
			System.out.println("Parsing returned values:  "+(time4-time3));

			System.out.println("Time to actually send and receive: "+(time2-time1)+" and time to parse response was: "+(time3-time2));
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
		return theResponse.makeStringResponse();
	}


}
