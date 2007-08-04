package messages;


import java.util.Vector;

import rlVizLib.messaging.AbstractMessage;
import rlVizLib.messaging.GenericMessage;
import rlVizLib.messaging.MessageUser;
import rlVizLib.messaging.MessageValueType;
import rlVizLib.messaging.NotAnRLVizMessageException;
import rlVizLib.messaging.environment.EnvMessageType;
import rlVizLib.messaging.environment.EnvironmentMessages;
import rlglue.RLGlue;

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

		String responseMessage=RLGlue.RL_env_message(theRequest);

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
