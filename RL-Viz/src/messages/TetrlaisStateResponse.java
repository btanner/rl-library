package messages;

import java.util.StringTokenizer;

import rlVizLib.messaging.AbstractResponse;
import rlVizLib.messaging.GenericMessage;
import rlVizLib.messaging.MessageUser;
import rlVizLib.messaging.MessageValueType;
import rlVizLib.messaging.NotAnRLVizMessageException;
import rlVizLib.messaging.environment.EnvMessageType;

public class TetrlaisStateResponse extends AbstractResponse {
	private int tet_global_score= 0;
	private int world_width =0;
	private int world_height =0;
	private int [] world = null;
	private int timeStep=0;
	private int episodeNumber=0;
	
	public TetrlaisStateResponse(int episodeNumber, int timeStep,int score,int width, int height, int [] gs){
		this.tet_global_score =score;
		this.world_width = width;
		this.world_height = height;
		this.world = gs;	
		this.timeStep=timeStep;
		this.episodeNumber=episodeNumber;
	}
	
	public TetrlaisStateResponse(String responseMessage)throws NotAnRLVizMessageException{
		
		GenericMessage theGenericResponse;
			theGenericResponse = new GenericMessage(responseMessage);


		String thePayLoadString=theGenericResponse.getPayLoad();

		StringTokenizer stateTokenizer = new StringTokenizer(thePayLoadString, ":");
		this.timeStep=Integer.parseInt(stateTokenizer.nextToken());
		this.episodeNumber=Integer.parseInt(stateTokenizer.nextToken());

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

		theResponseBuffer.append(this.timeStep);
		theResponseBuffer.append(":");
		theResponseBuffer.append(this.episodeNumber);
		theResponseBuffer.append(":");
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
	
	public int getTimeStep(){
		return timeStep;
	}
	public int getEpisodeNumber(){
		return episodeNumber;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj==null)
			return false;
		TetrlaisStateResponse compareObject=(TetrlaisStateResponse)obj;
		return (compareObject.timeStep==timeStep&&compareObject.episodeNumber==episodeNumber);
		
	}

}
