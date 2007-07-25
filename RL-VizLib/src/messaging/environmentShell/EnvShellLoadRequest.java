package messaging.environmentShell;

import messaging.GenericMessage;
import messaging.MessageUser;
import messaging.MessageValueType;
import rlglue.RLGlue;

public class EnvShellLoadRequest extends EnvironmentShellMessages{
String envName;

	public EnvShellLoadRequest(GenericMessage theMessageObject) {
		super(theMessageObject);
		
		this.envName=super.getPayLoad();
	}
	
	

	public static EnvShellLoadResponse Execute(String envName){

		String theRequest="TO="+MessageUser.kEnvShell.id()+" FROM="+MessageUser.kBenchmark.id();
		theRequest+=" CMD="+EnvShellMessageType.kEnvShellLoad.id()+" VALTYPE="+MessageValueType.kString.id()+" VALS="+envName;

		String responseMessage=RLGlue.RL_env_message(theRequest);
		
		EnvShellLoadResponse theResponse=new EnvShellLoadResponse(responseMessage);
		return theResponse;


	}



	public String getEnvName() {
		return envName;
	}
	
}
