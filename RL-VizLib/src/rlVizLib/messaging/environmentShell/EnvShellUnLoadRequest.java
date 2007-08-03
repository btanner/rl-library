package rlVizLib.messaging.environmentShell;


import rlVizLib.messaging.AbstractMessage;
import rlVizLib.messaging.GenericMessage;
import rlVizLib.messaging.MessageUser;
import rlVizLib.messaging.MessageValueType;
import rlVizLib.messaging.NotAnRLVizMessageException;
import rlglue.RLGlue;

public class EnvShellUnLoadRequest extends EnvironmentShellMessages{
	public EnvShellUnLoadRequest(GenericMessage theMessageObject) {
		super(theMessageObject);
	}




	public static EnvShellUnLoadResponse Execute(){
		String theRequestString=AbstractMessage.makeMessage(
				MessageUser.kEnvShell.id(),
				MessageUser.kBenchmark.id(),
				EnvShellMessageType.kEnvShellUnLoad.id(),
				MessageValueType.kNone.id(),
				"NULL");

		String responseMessage=RLGlue.RL_env_message(theRequestString);

		EnvShellUnLoadResponse theResponse;
		try {
			theResponse = new EnvShellUnLoadResponse(responseMessage);
		} catch (NotAnRLVizMessageException e) {
			System.err.println("In EnvShellUnLoadResponse: response was not an RLViz Message");
			return null;
		}	
		return theResponse;


	}
}
