package org.rlcommunity.awhite.visualizers.tools.humanAgentInterface.messages;

import rlVizLib.messaging.AbstractMessage;
import rlVizLib.messaging.AbstractResponse;
import rlVizLib.messaging.MessageUser;
import rlVizLib.messaging.MessageValueType;
import rlVizLib.messaging.NotAnRLVizMessageException;
import rlVizLib.messaging.agent.AgentMessageType;

public class agentRewardResponse extends AbstractResponse{
    
    //this one is called on the benchmark side to change a string message to an object
    public agentRewardResponse(String responseMessage) throws NotAnRLVizMessageException {
        //There is no response
    }
    
    //this one is called on the agent side to just build the object
    public agentRewardResponse() {
        super();
    }

    public String makeStringResponse(){
        	String theResponseString=AbstractMessage.makeMessage(
				MessageUser.kBenchmark.id(),
				MessageUser.kAgent.id(),
				AgentMessageType.kAgentResponse.id(),
				MessageValueType.kNone.id(),
				"NULL");
                
                return theResponseString;
        

    }
    
}