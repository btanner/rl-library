package messaging.agent;

import java.util.Vector;

import messaging.AbstractResponse;
import messaging.MessageUser;
import messaging.MessageValueType;
import messaging.UtilityShop;

import rlglue.Observation;

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
		// TODO Auto-generated method stub
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
		String theResponseString="TO="+MessageUser.kBenchmark.id()+" FROM="+MessageUser.kAgent.id();
		theResponseString+=" CMD="+AgentMessageType.kAgentResponse.id()+" VALTYPE="+MessageValueType.kStringList.id()+" VALS=";
		
		theResponseString+=theValues.size()+":";
		
		for(int i=0;i<theValues.size();i++){
			theResponseString+=theValues.get(i)+":";
		}
		
		return theResponseString;
	}
};