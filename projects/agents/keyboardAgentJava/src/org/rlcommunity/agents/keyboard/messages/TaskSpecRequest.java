/* Helicopter Domain Visualizer Resources for RL - Competition 
* Copyright (C) 2007, Brian Tanner
* 
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 2
* of the License, or (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA. */
package org.rlcommunity.agents.keyboard.messages;

import org.rlcommunity.rlglue.codec.RLGlue;
import rlVizLib.messaging.AbstractMessage;
import rlVizLib.messaging.GenericMessage;
import rlVizLib.messaging.MessageUser;
import rlVizLib.messaging.MessageValueType;
import rlVizLib.messaging.NotAnRLVizMessageException;
import rlVizLib.messaging.agent.AgentMessageType;
import rlVizLib.messaging.agent.AgentMessages;

public class TaskSpecRequest extends AgentMessages{

	public TaskSpecRequest(GenericMessage theMessageObject) {
		super(theMessageObject);
	}

        public static TaskSpecResponse Execute() {
		String theRequest=AbstractMessage.makeMessage(
				MessageUser.kAgent.id(),
				MessageUser.kBenchmark.id(),
				AgentMessageType.kAgentCustom.id(),
				MessageValueType.kString.id(),
				"GETTASKSPEC");

		String responseMessage=RLGlue.RL_agent_message(theRequest);
		TaskSpecResponse theResponse;
		try{
		theResponse = new TaskSpecResponse(responseMessage);
		}catch(NotAnRLVizMessageException ex){
			System.out.println("Not a valid RL Viz Message in TellAgentWhatToDo response" + ex);
			return null;
		}
		return theResponse;
	}


}
