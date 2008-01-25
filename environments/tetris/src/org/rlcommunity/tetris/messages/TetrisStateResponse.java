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

public class TetrisStateResponse extends AbstractResponse {
	private int tet_global_score= 0;
	private int world_width =0;
	private int world_height =0;
	private int [] world = null;
	private int currentPiece =0;
	
	public TetrisStateResponse(int score,int width, int height, int [] gs, int piece){
		this.tet_global_score =score;
		this.world_width = width;
		this.world_height = height;
		this.world = gs;	
		this.currentPiece = piece;
	}
	
	public TetrisStateResponse(String responseMessage)throws NotAnRLVizMessageException{
		
		GenericMessage theGenericResponse;
			theGenericResponse = new GenericMessage(responseMessage);


		String thePayLoadString=theGenericResponse.getPayLoad();

		StringTokenizer stateTokenizer = new StringTokenizer(thePayLoadString, ":");

		this.world_width=Integer.parseInt(stateTokenizer.nextToken());
		this.world_height=Integer.parseInt(stateTokenizer.nextToken());
		this.tet_global_score=Integer.parseInt(stateTokenizer.nextToken());
		int i =0;
		int worldSize = this.world_width*this.world_height;
		world = new int[worldSize];
		while((stateTokenizer.hasMoreTokens())&&(i<worldSize)){
			this.world[i] = Integer.parseInt(stateTokenizer.nextToken());
			++i;
		}
		this.currentPiece = Integer.parseInt(stateTokenizer.nextToken());
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
		theResponseBuffer.append(":");
		theResponseBuffer.append(this.tet_global_score);
		theResponseBuffer.append(":");
		for(int i = 0; i < this.world.length; i++){
		theResponseBuffer.append(":");
		theResponseBuffer.append(world[i]);
		}
		theResponseBuffer.append(":");
		theResponseBuffer.append(this.currentPiece);

		return theResponseBuffer.toString();
	}
	
	public int getScore(){
		return this.tet_global_score;
	}
	public int getWidth(){
		return this.world_width;
	}
	public int getHeight(){
		return this.world_height;
	}
	public int [] getWorld(){
		return this.world;
	}
	
	public int getCurrentPiece(){
		return currentPiece;
	}


}
