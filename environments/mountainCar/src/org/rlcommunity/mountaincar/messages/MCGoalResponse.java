/* Mountain Car Domain
* Copyright (C) 2007, Brian Tanner brian@tannerpages.com (http://brian.tannerpages.com/)
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
package org.rlcommunity.mountaincar.messages;

import java.util.StringTokenizer;

import rlVizLib.messaging.AbstractMessage;
import rlVizLib.messaging.AbstractResponse;
import rlVizLib.messaging.GenericMessage;
import rlVizLib.messaging.MessageUser;
import rlVizLib.messaging.MessageValueType;
import rlVizLib.messaging.NotAnRLVizMessageException;
import rlVizLib.messaging.environment.EnvMessageType;

public class MCGoalResponse extends AbstractResponse {
	double goalPosition;
	
	public MCGoalResponse(double goal){
		this.goalPosition = goal;
	}

	public MCGoalResponse(String responseMessage) throws NotAnRLVizMessageException {
		GenericMessage theGenericResponse = new GenericMessage(responseMessage);
		String thePayLoadString=theGenericResponse.getPayLoad();
		StringTokenizer stateTokenizer = new StringTokenizer(thePayLoadString, ":");
		goalPosition=Double.parseDouble(stateTokenizer.nextToken());
	}
	
	public double getGoalPosition() {
		return this.goalPosition;
	}

	@Override
	public String makeStringResponse() {
		StringBuffer goalBuffer=new StringBuffer();

		goalBuffer.append(goalPosition);
		goalBuffer.append(":");


			String theResponse=AbstractMessage.makeMessage(
					MessageUser.kBenchmark.id(),
					MessageUser.kEnv.id(),
					EnvMessageType.kEnvResponse.id(),
					MessageValueType.kStringList.id(),
			goalBuffer.toString());


		return theResponse;
	}
	

}
