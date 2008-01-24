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
import java.util.Vector;

import rlVizLib.messaging.AbstractMessage;
import rlVizLib.messaging.AbstractResponse;
import rlVizLib.messaging.GenericMessage;
import rlVizLib.messaging.MessageUser;
import rlVizLib.messaging.MessageValueType;
import rlVizLib.messaging.NotAnRLVizMessageException;
import rlVizLib.messaging.environment.EnvMessageType;


public class MCHeightResponse extends AbstractResponse{
	Vector<Double> theHeights=null;


	public MCHeightResponse(Vector<Double> theHeights) {
		this.theHeights=theHeights;
	}

	public MCHeightResponse(String responseMessage) throws NotAnRLVizMessageException {
		theHeights=new Vector<Double>();
		GenericMessage theGenericResponse = new GenericMessage(responseMessage);

		String thePayLoadString=theGenericResponse.getPayLoad();

		StringTokenizer stateTokenizer = new StringTokenizer(thePayLoadString, ":");

		int numHeights=Integer.parseInt(stateTokenizer.nextToken());
		
		for(int i=0;i<numHeights;i++)
			theHeights.add(new Double(Double.parseDouble(stateTokenizer.nextToken())));
	}

	@Override
	public String makeStringResponse() {
		StringBuffer heightsBuffer=new StringBuffer();

		heightsBuffer.append(theHeights.size());
		heightsBuffer.append(":");

		for(int i=0;i<theHeights.size();i++){
			heightsBuffer.append(theHeights.get(i));
			heightsBuffer.append(":");
		}


			String theResponse=AbstractMessage.makeMessage(
					MessageUser.kBenchmark.id(),
					MessageUser.kEnv.id(),
					EnvMessageType.kEnvResponse.id(),
					MessageValueType.kStringList.id(),
			heightsBuffer.toString());


		return theResponse;
	}
	
	public Vector<Double> getHeights(){
		return theHeights;
	}
};