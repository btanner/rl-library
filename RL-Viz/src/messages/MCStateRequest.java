package messages;


import java.util.StringTokenizer;

import messaging.AbstractMessage;
import messaging.GenericMessage;
import messaging.MessageUser;
import messaging.MessageValueType;
import messaging.NotAnRLVizMessageException;
import messaging.environment.EnvMessageType;
import messaging.environment.EnvironmentMessages;

import rlglue.RLGlue;

public class MCStateRequest extends EnvironmentMessages{

	public MCStateRequest(GenericMessage theMessageObject){
		super(theMessageObject);
	}

	public static MCStateResponse Execute(){
		String theRequest=AbstractMessage.makeMessage(
				MessageUser.kEnv.id(),
				MessageUser.kBenchmark.id(),
				EnvMessageType.kEnvCustom.id(),
				MessageValueType.kString.id(),
				"GETMCSTATE");

		String responseMessage=RLGlue.RL_env_message(theRequest);

		MCStateResponse theResponse;
		try {
			theResponse = new MCStateResponse(responseMessage);
		} catch (NotAnRLVizMessageException e) {
			System.err.println("In MCStateRequest, the response was not RL-Viz compatible");
			theResponse=null;
		}

		return theResponse;

	}
}
