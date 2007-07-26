package messaging.environment;


import java.util.StringTokenizer;

import messaging.AbstractMessage;
import messaging.GenericMessage;
import messaging.MessageUser;
import messaging.MessageValueType;
import messaging.NotAnRLVizMessageException;
import messaging.environment.EnvMessageType;
import messaging.environment.EnvironmentMessages;

import rlglue.RLGlue;

public class EnvVersionSupportedRequest extends EnvironmentMessages{

	public EnvVersionSupportedRequest(GenericMessage theMessageObject){
		super(theMessageObject);
	}

	public static EnvVersionSupportedResponse Execute(){
		String theRequest=AbstractMessage.makeMessage(
				MessageUser.kEnv.id(),
				MessageUser.kBenchmark.id(),
				EnvMessageType.kEnvQuerySupportedVersion.id(),
				MessageValueType.kNone.id(),
				"NULL");

		String responseMessage=RLGlue.RL_env_message(theRequest);

		EnvVersionSupportedResponse theResponse;
		try {
			theResponse = new EnvVersionSupportedResponse(responseMessage);
		} catch (NotAnRLVizMessageException e) {
			//if we didn't get back anything good from the environment, we'll assume its supporting version 0.0 of rlViz :P
			theResponse= new EnvVersionSupportedResponse(0,0);
		}
		return theResponse;

	}
}
