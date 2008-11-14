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

import rlVizLib.messaging.AbstractResponse;
import rlVizLib.messaging.GenericMessage;
import rlVizLib.messaging.MessageUser;
import rlVizLib.messaging.MessageValueType;
import rlVizLib.messaging.NotAnRLVizMessageException;
import rlVizLib.messaging.agent.AgentMessageType;

public class TellAgentWhatToDoResponse extends AbstractResponse{
    

	public TellAgentWhatToDoResponse(){
	}
	
	public TellAgentWhatToDoResponse(String responseMessage)throws NotAnRLVizMessageException{
		GenericMessage theGenericResponse;
        	theGenericResponse = new GenericMessage(responseMessage);
	}


	@Override
	public String makeStringResponse() {
		StringBuffer theResponseBuffer= new StringBuffer();
		theResponseBuffer.append("TO=");
		theResponseBuffer.append(MessageUser.kBenchmark.id());
		theResponseBuffer.append(" FROM=");
		theResponseBuffer.append(MessageUser.kAgent.id());
		theResponseBuffer.append(" CMD=");
		theResponseBuffer.append(AgentMessageType.kAgentResponse.id());
		theResponseBuffer.append(" VALTYPE=");
		theResponseBuffer.append(MessageValueType.kNone.id());
		theResponseBuffer.append(" VALS=");

		return theResponseBuffer.toString();
	}
	
}
