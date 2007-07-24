package messages;


import java.util.StringTokenizer;
import messaging.GenericMessage;
import messaging.MessageUser;
import messaging.MessageValueType;
import messaging.environment.EnvMessageType;
import messaging.environment.EnvironmentMessages;

import rlglue.RLGlue;

public class MCStateRequest extends EnvironmentMessages{

	public MCStateRequest(MessageUser from, MessageUser to, EnvMessageType theMessageType) {
		super(from, to, theMessageType);
	}

	public static MCStateResponse Execute(){
		String theRequest="TO="+MessageUser.kEnv.id()+" FROM="+MessageUser.kBenchmark.id();
		theRequest+=" CMD="+EnvMessageType.kEnvCustom.id()+" VALTYPE="+MessageValueType.kString.id()+" VALS=GETMCSTATE";

		String responseMessage=RLGlue.RL_env_message(theRequest);

//should implement this soon.
//		MCStateResponse theResponse=new MCStateResponse(responseMessage);
			
		GenericMessage theGenericResponse=new GenericMessage(responseMessage);

		String thePayLoadString=theGenericResponse.getPayLoad();

		StringTokenizer stateTokenizer = new StringTokenizer(thePayLoadString, ":");
		
		double position=Double.parseDouble(stateTokenizer.nextToken());
		double velocity=Double.parseDouble(stateTokenizer.nextToken());
		double height=Double.parseDouble(stateTokenizer.nextToken());
		double deltaheight=Double.parseDouble(stateTokenizer.nextToken());
		
		MCStateResponse theResponse=new MCStateResponse(position,velocity,height,deltaheight);
		return theResponse;

	}
}
