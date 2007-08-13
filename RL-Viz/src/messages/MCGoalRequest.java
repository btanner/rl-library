package messages;

import rlVizLib.messaging.AbstractMessage;
import rlVizLib.messaging.GenericMessage;
import rlVizLib.messaging.MessageUser;
import rlVizLib.messaging.MessageValueType;
import rlVizLib.messaging.NotAnRLVizMessageException;
import rlVizLib.messaging.environment.EnvMessageType;
import rlVizLib.messaging.environment.EnvironmentMessages;
import rlglue.RLGlue;

public class MCGoalRequest extends EnvironmentMessages{

	public MCGoalRequest(GenericMessage theMessageObject) {
		super(theMessageObject);
	}

	public static MCGoalResponse Execute(){
		String theRequest=AbstractMessage.makeMessage(
				MessageUser.kEnv.id(),
				MessageUser.kBenchmark.id(),
				EnvMessageType.kEnvCustom.id(),
				MessageValueType.kString.id(),
				"GETMCGOAL");

		String responseMessage=RLGlue.RL_env_message(theRequest);

		MCGoalResponse theResponse;
		try {
			theResponse = new MCGoalResponse(responseMessage);
		} catch (NotAnRLVizMessageException e) {
			System.err.println("In MCGoalRequest, the response was not RL-Viz compatible");
			theResponse=null;
		}

		return theResponse;

	}

}
