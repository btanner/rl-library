package messaging.environmentShell;

import messaging.AbstractMessage;
import messaging.GenericMessage;
import messaging.MessageUser;
import messaging.MessageValueType;
import messaging.NotAnRLVizMessageException;
import rlglue.RLGlue;

public class EnvShellLoadRequest extends EnvironmentShellMessages{
String envName;

	public EnvShellLoadRequest(GenericMessage theMessageObject) {
		super(theMessageObject);
		
		this.envName=super.getPayLoad();
	}
	
	

	public static EnvShellLoadResponse Execute(String envName){
		String theRequest=AbstractMessage.makeMessage(
				MessageUser.kEnvShell.id(),
				MessageUser.kBenchmark.id(),
				EnvShellMessageType.kEnvShellLoad.id(),
				MessageValueType.kString.id(),
				envName);

		String responseMessage=RLGlue.RL_env_message(theRequest);
		
		EnvShellLoadResponse theResponse;
		try {
			theResponse = new EnvShellLoadResponse(responseMessage);
		} catch (NotAnRLVizMessageException e) {
			System.err.println("In EnvShellLoadRequest: response was not an RLViz Message");
			return null;
		}		return theResponse;


	}



	public String getEnvName() {
		return envName;
	}
	
}
