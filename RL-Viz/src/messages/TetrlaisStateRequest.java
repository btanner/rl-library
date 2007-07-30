package messages;

import rlglue.RLGlue;
import rlVizLib.messaging.AbstractMessage;
import rlVizLib.messaging.GenericMessage;
import rlVizLib.messaging.MessageUser;
import rlVizLib.messaging.MessageValueType;
import rlVizLib.messaging.NotAnRLVizMessageException;
import rlVizLib.messaging.environment.EnvMessageType;
import rlVizLib.messaging.environment.EnvironmentMessages;

public class TetrlaisStateRequest extends EnvironmentMessages{

	public TetrlaisStateRequest(GenericMessage theMessageObject) {
		super(theMessageObject);
		// TODO Auto-generated constructor stub
	}

	public static TetrlaisStateResponse Execute(){
		String theRequest=AbstractMessage.makeMessage(
				MessageUser.kEnv.id(),
				MessageUser.kBenchmark.id(),
				EnvMessageType.kEnvCustom.id(),
				MessageValueType.kString.id(),
				"GETTETRLAISSTATE");

		String responseMessage=RLGlue.RL_env_message(theRequest);
		TetrlaisStateResponse theResponse;
		try{
		theResponse = new TetrlaisStateResponse(responseMessage);
		}catch(NotAnRLVizMessageException ex){
			System.out.println("Not a valid RL Viz Message in Tetrlais State Response" + ex);
			return null;
		}
		return theResponse;
	}

}
