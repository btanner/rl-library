package ContinuousGridWorld.messages;


import org.rlcommunity.rlglue.codec.RLGlue;
import rlVizLib.messaging.AbstractMessage;
import rlVizLib.messaging.GenericMessage;
import rlVizLib.messaging.MessageUser;
import rlVizLib.messaging.MessageValueType;
import rlVizLib.messaging.NotAnRLVizMessageException;
import rlVizLib.messaging.environment.EnvMessageType;
import rlVizLib.messaging.environment.EnvironmentMessages;

public class MapRequest extends EnvironmentMessages{

	public MapRequest(GenericMessage theMessageObject){
		super(theMessageObject);
	}

	public static MapResponse Execute(){
		String theRequest=AbstractMessage.makeMessage(
				MessageUser.kEnv.id(),
				MessageUser.kBenchmark.id(),
				EnvMessageType.kEnvCustom.id(),
				MessageValueType.kString.id(),
				"GETCGWMAP");

		String responseMessage=RLGlue.RL_env_message(theRequest);

		MapResponse theResponse;
		try {
			theResponse = new MapResponse(responseMessage);
		} catch (NotAnRLVizMessageException e) {
			System.err.println("In CGWMapRequest, the response was not RL-Viz compatible");
			theResponse=null;
		}

		return theResponse;

	}
}
