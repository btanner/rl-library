package messages;


import rlVizLib.messaging.AbstractMessage;
import rlVizLib.messaging.GenericMessage;
import rlVizLib.messaging.MessageUser;
import rlVizLib.messaging.MessageValueType;
import rlVizLib.messaging.NotAnRLVizMessageException;
import rlVizLib.messaging.environment.EnvMessageType;
import rlVizLib.messaging.environment.EnvironmentMessages;
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
