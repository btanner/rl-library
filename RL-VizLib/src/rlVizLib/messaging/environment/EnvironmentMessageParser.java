package rlVizLib.messaging.environment;

import rlVizLib.messaging.GenericMessage;
import rlVizLib.messaging.GenericMessageParser;
import rlVizLib.messaging.NotAnRLVizMessageException;

public class EnvironmentMessageParser extends GenericMessageParser{
	public static EnvironmentMessages parseMessage(String theMessage) throws NotAnRLVizMessageException{
		GenericMessage theGenericMessage=new GenericMessage(theMessage);

		int cmdId=theGenericMessage.getTheMessageType();

		if(cmdId==EnvMessageType.kEnvQueryVarRanges.id()) 				return new EnvRangeRequest(theGenericMessage);
		if(cmdId==EnvMessageType.kEnvQueryObservationsForState.id()) 	return new EnvObsForStateRequest(theGenericMessage);
		if(cmdId==EnvMessageType.kEnvQuerySupportedVersion.id()) 	return new EnvVersionSupportedRequest(theGenericMessage);
		if(cmdId==EnvMessageType.kEnvCustom.id())						return new EnvCustomRequest(theGenericMessage);


		System.out.println("EnvironmentMessageParser - unknown query type: "+theMessage);
		Thread.dumpStack();
		System.exit(1);
		return null;
	}
}
