package messaging.environment;

import java.util.Vector;

import messaging.AbstractMessage;
import messaging.GenericMessage;
import messaging.MessageUser;
import messaging.MessageValueType;
import messaging.NotAnRLVizMessageException;
import rlglue.RLGlue;
import visualization.QueryableEnvironment;

public class EnvRangeRequest extends EnvironmentMessages{

	public EnvRangeRequest(GenericMessage theMessageObject) {
		super(theMessageObject);
	}

	public static EnvRangeResponse Execute(){
		String theRequest=AbstractMessage.makeMessage(
				MessageUser.kEnv.id(),
				MessageUser.kBenchmark.id(),
				EnvMessageType.kEnvQueryVarRanges.id(),
				MessageValueType.kNone.id(),
		"NULL");

		String responseMessage=RLGlue.RL_env_message(theRequest);

		EnvRangeResponse theResponse;
		try {
			theResponse = new EnvRangeResponse(responseMessage);
		} catch (NotAnRLVizMessageException e) {
			System.err.println("In EnvRangeRequest, the response was not RL-Viz Compatible");
			theResponse = null;
		}

		return theResponse;

	}

	@Override
	public boolean canHandleAutomatically() {
		return true;
	}

	@Override
	public String handleAutomatically(QueryableEnvironment theEnvironment) {
		//			//Handle a request for the ranges
		Vector<Double> mins = new Vector<Double>();
		Vector<Double> maxs = new Vector<Double>();

		int numVars=theEnvironment.getNumVars();

		for(int i=0;i<numVars;i++){
			mins.add(theEnvironment.getMinValueForQuerableVariable(i));
			maxs.add(theEnvironment.getMaxValueForQuerableVariable(i));
		}

		EnvRangeResponse theResponse=new EnvRangeResponse(mins, maxs);

		return theResponse.makeStringResponse();

	}

}
