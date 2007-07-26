package messaging.environmentShell;

import messaging.GenericMessage;
import messaging.GenericMessageParser;
import messaging.NotAnRLVizMessageException;

public class EnvironmentShellMessageParser extends GenericMessageParser{

	public static EnvironmentShellMessages parseMessage(String theMessage) throws NotAnRLVizMessageException{
		GenericMessage theGenericMessage=new GenericMessage(theMessage);
		return EnvironmentShellMessageParser.makeMessage(theGenericMessage);
	}

	public static EnvironmentShellMessages makeMessage(GenericMessage theGenericMessage) {
		int cmdId=theGenericMessage.getTheMessageType();
		if(cmdId==EnvShellMessageType.kEnvShellListQuery.id()) 				return new EnvShellListRequest(theGenericMessage);
		if(cmdId==EnvShellMessageType.kEnvShellLoad.id()) 				return new EnvShellLoadRequest(theGenericMessage);


		System.out.println("EnvironmentShellMessageParser - unknown query type: "+cmdId);
		Thread.dumpStack();
		System.exit(1);
		return null;
	}
}
