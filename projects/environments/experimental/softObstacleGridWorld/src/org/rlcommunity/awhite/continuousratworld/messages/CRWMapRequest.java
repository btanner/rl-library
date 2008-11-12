package org.rlcommunity.awhite.continuousratworld.messages;


import org.rlcommunity.rlglue.codec.RLGlue;
import rlVizLib.messaging.AbstractMessage;
import rlVizLib.messaging.GenericMessage;
import rlVizLib.messaging.MessageUser;
import rlVizLib.messaging.MessageValueType;
import rlVizLib.messaging.NotAnRLVizMessageException;
import rlVizLib.messaging.environment.EnvMessageType;
import rlVizLib.messaging.environment.EnvironmentMessages;

public class CRWMapRequest extends EnvironmentMessages{

	public CRWMapRequest(GenericMessage theMessageObject){
		super(theMessageObject);
	}

	public static CRWMapResponse Execute(){
		String theRequest=AbstractMessage.makeMessage(
				MessageUser.kEnv.id(),
				MessageUser.kBenchmark.id(),
				EnvMessageType.kEnvCustom.id(),
				MessageValueType.kString.id(),
				"GETCRWMAP");

		String responseMessage=RLGlue.RL_env_message(theRequest);

		CRWMapResponse theResponse;
		try {
			theResponse = new CRWMapResponse(responseMessage);
		} catch (NotAnRLVizMessageException e) {
			System.err.println("In CRWMapRequest, the response was not RL-Viz compatible");
			theResponse=null;
		}

		return theResponse;

	}
}
