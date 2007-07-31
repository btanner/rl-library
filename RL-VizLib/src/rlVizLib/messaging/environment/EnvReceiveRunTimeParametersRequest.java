package rlVizLib.messaging.environment;


import rlVizLib.general.ParameterHolder;
import rlVizLib.messaging.AbstractMessage;
import rlVizLib.messaging.GenericMessage;
import rlVizLib.messaging.MessageUser;
import rlVizLib.messaging.MessageValueType;
import rlVizLib.messaging.NotAnRLVizMessageException;
import rlVizLib.messaging.interfaces.ReceivesRunTimeParameterHolderInterface;
import rlglue.RLGlue;
import rlglue.environment.Environment;

public class EnvReceiveRunTimeParametersRequest extends EnvironmentMessages{
ParameterHolder theParams=null;


public EnvReceiveRunTimeParametersRequest(GenericMessage theMessageObject){
		super(theMessageObject);
		
		theParams=new ParameterHolder(super.getPayLoad());
	}

	public static EnvReceiveRunTimeParametersResponse Execute(ParameterHolder theParams){
		String serializedParameters=theParams.stringSerialize();
		
		String theRequest=AbstractMessage.makeMessage(
				MessageUser.kEnv.id(),
				MessageUser.kBenchmark.id(),
				EnvMessageType.kEnvReceiveRunTimeParameters.id(),
				MessageValueType.kNone.id(),
				serializedParameters);

		String responseMessage=RLGlue.RL_env_message(theRequest);

		EnvReceiveRunTimeParametersResponse theResponse;
		try {
			theResponse = new EnvReceiveRunTimeParametersResponse(responseMessage);
		} catch (NotAnRLVizMessageException e) {
			return new EnvReceiveRunTimeParametersResponse(false);			
		}
		return theResponse;
	}

	@Override
	public String handleAutomatically(Environment theEnvironment) {
		ReceivesRunTimeParameterHolderInterface castedEnv = (ReceivesRunTimeParameterHolderInterface)theEnvironment;
		EnvReceiveRunTimeParametersResponse theResponse=new EnvReceiveRunTimeParametersResponse(castedEnv.receiveRunTimeParameters(theParams));
		return theResponse.makeStringResponse();
	}

	@Override
	public boolean canHandleAutomatically(Object theEnvironment) {
		return (theEnvironment instanceof ReceivesRunTimeParameterHolderInterface);
	}
	
	
}
