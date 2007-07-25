package messaging.environmentShell;

import messaging.GenericMessage;
import messaging.MessageUser;
import messaging.MessageValueType;
import rlglue.RLGlue;

public class EnvShellListRequest extends EnvironmentShellMessages{

	public EnvShellListRequest(GenericMessage theMessageObject) {
		super(theMessageObject);
	}
	
	

	public static EnvShellListResponse Execute(){

		String theRequest="TO="+MessageUser.kEnvShell.id()+" FROM="+MessageUser.kBenchmark.id();
		theRequest+=" CMD="+EnvShellMessageType.kEnvShellListQuery.id()+" VALTYPE="+MessageValueType.kNone.id()+" VALS=NULL";

		String responseMessage=RLGlue.RL_env_message(theRequest);
		
		System.out.println("the response messge from the Shell was: "+responseMessage);
		EnvShellListResponse theResponse=new EnvShellListResponse(responseMessage);
		return theResponse;


	}
	
}
