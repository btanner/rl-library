package messages;

import java.util.StringTokenizer;
import java.util.Vector;

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
		goalPosition=Integer.parseInt(stateTokenizer.nextToken());
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
