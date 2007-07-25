package messaging.agent;

import java.util.Vector;

import messaging.AbstractResponse;
import messaging.MessageUser;
import messaging.MessageValueType;

public class AgentValueForObsResponse extends AbstractResponse{
	private Vector<Double> theValues;
	

	public AgentValueForObsResponse(Vector<Double> theValues) {
		this.theValues=theValues;
	}




	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String theResponse="AgentValuesForOBs: " + theValues.size()+" values, they are: ";
		for(int i=0;i<theValues.size();i++){
			theResponse+=theValues.get(i)+" ";
		}
		return theResponse;
	}




	/**
	 * @return the theValues
	 */
	public Vector<Double> getTheValues() {
		return theValues;
	}

//	@Override
	
	//So, when you create on of these in an environment, this gives you the response to send
	public String makeStringResponse() {
		
		StringBuffer theResponseBuffer= new StringBuffer();
		theResponseBuffer.append("TO=");
		theResponseBuffer.append(MessageUser.kBenchmark.id());
		theResponseBuffer.append(" FROM=");
		theResponseBuffer.append(MessageUser.kAgent.id());
		theResponseBuffer.append(" CMD=");
		theResponseBuffer.append(AgentMessageType.kAgentResponse.id());
		theResponseBuffer.append(" VALTYPE=");
		theResponseBuffer.append(MessageValueType.kStringList.id());
		theResponseBuffer.append(" VALS=");

		theResponseBuffer.append(theValues.size());
		theResponseBuffer.append(":");

		
		for(int i=0;i<theValues.size();i++){
			theResponseBuffer.append(theValues.get(i));
			theResponseBuffer.append(':');
		}
		return theResponseBuffer.toString();
	}
};