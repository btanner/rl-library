package messaging.agent;

import messaging.GenericMessage;
import messaging.GenericMessageParser;

public class AgentMessageParser extends GenericMessageParser{
		public static AgentMessages parseMessage(String theMessage){
			GenericMessage theGenericMessage=new GenericMessage(theMessage);

			int cmdId=theGenericMessage.getTheMessageType();
			
			if(cmdId==AgentMessageType.kAgentQueryValuesForObs.id())	return new AgentValueForObsRequest(theGenericMessage);

			System.out.println("AgentMessageParser - unknown query type: "+theMessage);
			Thread.dumpStack();
			System.exit(1);
			return null;
	}
}
