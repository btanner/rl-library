package rlVizLib.messaging.environment;


import java.util.Vector;


import rlVizLib.general.RLVizVersion;
import rlVizLib.messaging.AbstractMessage;
import rlVizLib.messaging.GenericMessage;
import rlVizLib.messaging.MessageUser;
import rlVizLib.messaging.MessageValueType;
import rlVizLib.messaging.NotAnRLVizMessageException;
import rlVizLib.messaging.interfaces.RLVizEnvInterface;
import rlVizLib.messaging.interfaces.getEnvMaxMinsInterface;
import rlglue.Environment;
import rlglue.RLGlue;

public class EnvVersionSupportedRequest extends EnvironmentMessages{

	public EnvVersionSupportedRequest(GenericMessage theMessageObject){
		super(theMessageObject);
	}

	public static EnvVersionSupportedResponse Execute(){
		String theRequest=AbstractMessage.makeMessage(
				MessageUser.kEnv.id(),
				MessageUser.kBenchmark.id(),
				EnvMessageType.kEnvQuerySupportedVersion.id(),
				MessageValueType.kNone.id(),
				"NULL");

		String responseMessage=RLGlue.RL_env_message(theRequest);

		EnvVersionSupportedResponse theResponse;
		try {
			theResponse = new EnvVersionSupportedResponse(responseMessage);
		} catch (NotAnRLVizMessageException e) {
			//if we didn't get back anything good from the environment, we'll assume its supporting version 0.0 of rlViz :P
			theResponse= new EnvVersionSupportedResponse(RLVizVersion.NOVERSION);
		}
		return theResponse;
	}

	@Override
	public String handleAutomatically(Environment theEnvironment) {
		RLVizEnvInterface castedEnv = (RLVizEnvInterface)theEnvironment;
		EnvVersionSupportedResponse theResponse=new EnvVersionSupportedResponse(castedEnv.getTheVersionISupport());
		return theResponse.makeStringResponse();
	}

	@Override
	public boolean canHandleAutomatically(Object theEnvironment) {
		return (theEnvironment instanceof RLVizEnvInterface);
	}
	
	
}
