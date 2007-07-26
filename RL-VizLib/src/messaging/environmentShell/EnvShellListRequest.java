package messaging.environmentShell;

import messaging.AbstractMessage;
import messaging.GenericMessage;
import messaging.MessageUser;
import messaging.MessageValueType;
import messaging.NotAnRLVizMessageException;
import rlglue.RLGlue;

public class EnvShellListRequest extends EnvironmentShellMessages{

	public EnvShellListRequest(GenericMessage theMessageObject) {
		super(theMessageObject);
	}
	
	

	public static EnvShellListResponse Execute(){
		String theRequest=AbstractMessage.makeMessage(
				MessageUser.kEnvShell.id(),
				MessageUser.kBenchmark.id(),
				EnvShellMessageType.kEnvShellListQuery.id(),
				MessageValueType.kNone.id(),
				"NULL");


		String responseMessage=RLGlue.RL_env_message(theRequest);
		
		EnvShellListResponse theResponse;
		try {
			theResponse = new EnvShellListResponse(responseMessage);
		} catch (NotAnRLVizMessageException e) {
			System.err.println("In EnvShellListRequest: response was not an RLViz Message");
			return null;
		}
		return theResponse;


	}
	
}
