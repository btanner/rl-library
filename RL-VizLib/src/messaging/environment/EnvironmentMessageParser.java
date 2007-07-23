package messaging.environment;

import java.util.StringTokenizer;

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
				System.out.println("In EnvironmentMessageParser -- realized the request was for Ranges... passing it off to mountaincar");
				return new EnvRangeRequest(toU, fromU,EnvMessageType.kEnvQueryVarRanges);
			}
			
			if(cmdId==EnvMessageType.kEnvQueryObservationsForState.id()){
				System.out.println("In EnvironmentMessageParser -- realized the request was for kEnvQueryObservationsForState... passing it off to mountaincar");
				return new EnvObsForStateRequest(toU, fromU,EnvMessageType.kEnvQueryObservationsForState,theGenericMessage.getPayLoad());
			}

			if(cmdId==EnvMessageType.kEnvCustom.id()){
				System.out.println("In EnvironmentMessageParser -- realized the request was for kEnvCustom... passing it off to mountaincar");
				return new EnvCustomRequest(toU, fromU,EnvMessageType.kEnvCustom,theGenericMessage.getPayLoad());
			}

			System.out.println("EnvironmentMessageParser - unknown query type: "+theMessage);
			Thread.dumpStack();
			System.exit(1);
//			EnvMessageType theMessageType=new EnvMessageType(theCMDInt);
//			System.out.println("The Type token was: "+typeString+" and I think the message type is: "+theCMDInt);

			return null;
	}
}
