package messaging.agent;

import java.util.StringTokenizer;

import messaging.GenericMessage;
import messaging.GenericMessageParser;
import messaging.MessageUser;

public class AgentMessageParser extends GenericMessageParser{
		public static AgentMessages parseMessage(String theMessage){
			
			GenericMessage theGenericMessage=new GenericMessage(theMessage);

			MessageUser toU=theGenericMessage.getTo();
			MessageUser fromU=theGenericMessage.getFrom();
			
			int cmdId=theGenericMessage.getTheMessageType();
			
			
			if(cmdId==AgentMessageType.kAgentQueryValuesForObs.id()){
				return new AgentValueForObsRequest(toU, fromU,AgentMessageType.kAgentQueryValuesForObs,theGenericMessage.getPayLoad());
			}
			

			System.out.println("AgentMessageParser - unknown query type: "+theMessage);
			Thread.dumpStack();
			System.exit(1);
//			EnvMessageType theMessageType=new EnvMessageType(theCMDInt);
//			System.out.println("The Type token was: "+typeString+" and I think the message type is: "+theCMDInt);

			return null;
	}
}
