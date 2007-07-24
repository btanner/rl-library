package messaging.environment;

import messaging.GenericMessage;
import messaging.GenericMessageParser;
import messaging.MessageUser;

public class EnvironmentMessageParser extends GenericMessageParser{
	public static EnvironmentMessages parseMessage(String theMessage){

		GenericMessage theGenericMessage=new GenericMessage(theMessage);

		MessageUser toU=theGenericMessage.getTo();
		MessageUser fromU=theGenericMessage.getFrom();

		int cmdId=theGenericMessage.getTheMessageType();

		if(cmdId==EnvMessageType.kEnvQueryVarRanges.id()){
			return new EnvRangeRequest(toU, fromU,EnvMessageType.kEnvQueryVarRanges);
		}

		if(cmdId==EnvMessageType.kEnvQueryObservationsForState.id()){
			return new EnvObsForStateRequest(toU, fromU,EnvMessageType.kEnvQueryObservationsForState,theGenericMessage.getPayLoad());
		}

		if(cmdId==EnvMessageType.kEnvCustom.id()){
			return new EnvCustomRequest(toU, fromU,EnvMessageType.kEnvCustom,theGenericMessage.getPayLoad());
		}

		System.out.println("EnvironmentMessageParser - unknown query type: "+theMessage);
		Thread.dumpStack();
		System.exit(1);
		return null;
	}
}
