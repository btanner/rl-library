package org.rlcommunity.environments.continuousgridworld.messages;


import org.rlcommunity.rlglue.codec.RLGlue;
import rlVizLib.messaging.AbstractMessage;
import rlVizLib.messaging.GenericMessage;
import rlVizLib.messaging.MessageUser;
import rlVizLib.messaging.MessageValueType;
import rlVizLib.messaging.NotAnRLVizMessageException;
import rlVizLib.messaging.environment.EnvMessageType;
import rlVizLib.messaging.environment.EnvironmentMessages;

public class StateRequest extends EnvironmentMessages{

	public StateRequest(GenericMessage theMessageObject){
		super(theMessageObject);
	}

	public static StateResponse Execute(){
		String theRequest=AbstractMessage.makeMessage(
				MessageUser.kEnv.id(),
				MessageUser.kBenchmark.id(),
				EnvMessageType.kEnvCustom.id(),
				MessageValueType.kString.id(),
				"GETSTATE");

		String responseMessage=RLGlue.RL_env_message(theRequest);

		StateResponse theResponse;
		try {
			theResponse = new StateResponse(responseMessage);
		} catch (NotAnRLVizMessageException e) {
			System.err.println("In StateRequest, the response was not RL-Viz compatible");
			theResponse=null;
		}

		return theResponse;

	}
}
