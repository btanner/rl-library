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



import java.util.Vector;

import rlVizLib.glueProxy.RLGlueProxy;
import rlVizLib.messaging.AbstractMessage;
import rlVizLib.messaging.GenericMessage;
import rlVizLib.messaging.MessageUser;
import rlVizLib.messaging.MessageValueType;
import rlVizLib.messaging.NotAnRLVizMessageException;
import rlVizLib.messaging.environment.EnvMessageType;
import rlVizLib.messaging.environment.EnvironmentMessages;

public class MCHeightRequest extends EnvironmentMessages{
	Vector<Double> queryPositions=null;

	public MCHeightRequest(GenericMessage theMessageObject){
		super(theMessageObject);
	}

	public static MCHeightResponse Execute(Vector<Double> queryPositions){
		StringBuffer queryPosBuffer=new StringBuffer();

		queryPosBuffer.append(queryPositions.size());
		queryPosBuffer.append(":");

		for(int i=0;i<queryPositions.size();i++){
			queryPosBuffer.append(queryPositions.get(i));
			queryPosBuffer.append(":");
		}


			String theRequest=AbstractMessage.makeMessage(
					MessageUser.kEnv.id(),
					MessageUser.kBenchmark.id(),
					EnvMessageType.kEnvCustom.id(),
					MessageValueType.kStringList.id(),
			"GETHEIGHTS:"+queryPosBuffer.toString());

		String responseMessage=RLGlueProxy.RL_env_message(theRequest);

		MCHeightResponse theResponse;
		try {
			theResponse = new MCHeightResponse(responseMessage);
		} catch (NotAnRLVizMessageException e) {
			System.err.println("In MCStateRequest, the response was not RL-Viz compatible");
			theResponse=null;
		}

		return theResponse;

	}

	public Vector<Double> getQueryPositions() {
		return queryPositions;
	}
}
