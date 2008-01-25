/* Tetris Domain
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
package org.rlcommunity.tetris.messages;

import java.util.StringTokenizer;

import rlVizLib.messaging.AbstractResponse;
import rlVizLib.messaging.GenericMessage;
import rlVizLib.messaging.MessageUser;
import rlVizLib.messaging.MessageValueType;
import rlVizLib.messaging.NotAnRLVizMessageException;
import rlVizLib.messaging.environment.EnvMessageType;

public class TetrisWorldResponse extends AbstractResponse {
	private int world_width =0;
	private int world_height =0;
	
	public TetrisWorldResponse(int width, int height){
		this.world_width = width;
		this.world_height = height;
	}
	
	public TetrisWorldResponse(String responseMessage)throws NotAnRLVizMessageException{
		
		GenericMessage theGenericResponse;
			theGenericResponse = new GenericMessage(responseMessage);


		String thePayLoadString=theGenericResponse.getPayLoad();

		StringTokenizer stateTokenizer = new StringTokenizer(thePayLoadString, ":");
		this.world_width=Integer.parseInt(stateTokenizer.nextToken());
		this.world_height=Integer.parseInt(stateTokenizer.nextToken());
	}
	
	@Override
	public String makeStringResponse() {
		
		StringBuffer theResponseBuffer= new StringBuffer();
		theResponseBuffer.append("TO=");
		theResponseBuffer.append(MessageUser.kBenchmark.id());
		theResponseBuffer.append(" FROM=");
		theResponseBuffer.append(MessageUser.kEnv.id());
		theResponseBuffer.append(" CMD=");
		theResponseBuffer.append(EnvMessageType.kEnvResponse.id());
		theResponseBuffer.append(" VALTYPE=");
		theResponseBuffer.append(MessageValueType.kStringList.id());
		theResponseBuffer.append(" VALS=");

		theResponseBuffer.append(this.world_width);
		theResponseBuffer.append(":");
		theResponseBuffer.append(this.world_height);


		return theResponseBuffer.toString();
	}
	public int getWidth(){
		return this.world_width;
	}
	public int getHeight(){
		return this.world_height;
	}
}
