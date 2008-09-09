package org.rlcommunity.awhite.visualizers.tools.humanAgentInterface.messages;

import rlVizLib.glueProxy.RLGlueProxy;
import rlVizLib.messaging.AbstractMessage;
import rlVizLib.messaging.GenericMessage;
import rlVizLib.messaging.MessageUser;
import rlVizLib.messaging.MessageValueType;
import rlVizLib.messaging.NotAnRLVizMessageException;
import rlVizLib.messaging.agent.AgentMessageType;
import rlVizLib.messaging.agent.AgentMessages;





public class agentRewardRequest extends AgentMessages{

	public agentRewardRequest(GenericMessage theMessageObject) {
		super(theMessageObject);
	}

	public static agentRewardResponse Execute(double theRewardToSend){
		String theRequest=AbstractMessage.makeMessage(
				MessageUser.kAgent.id(),
				MessageUser.kBenchmark.id(),
				AgentMessageType.kAgentCustom.id(),
				MessageValueType.kString.id(),
				"SENDAGENTREWARD:"+theRewardToSend);
                

		String responseMessage=RLGlueProxy.RL_agent_message(theRequest);

		agentRewardResponse theResponse;
		try {
			theResponse = new agentRewardResponse(responseMessage);
		} catch (NotAnRLVizMessageException e) {
			System.err.println("In "+agentRewardRequest.class+", the response was not RL-Viz compatible");
			theResponse=null;
		}
		return theResponse;
	}

}
