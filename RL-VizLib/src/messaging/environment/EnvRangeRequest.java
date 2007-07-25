package messaging.environment;

import java.util.StringTokenizer;
import java.util.Vector;

import messaging.GenericMessage;
import messaging.MessageUser;
import messaging.MessageValueType;
import rlglue.RLGlue;
import visualization.QueryableEnvironment;

public class EnvRangeRequest extends EnvironmentMessages{

	public EnvRangeRequest(GenericMessage theMessageObject) {
		super(theMessageObject);
	}

	public static EnvRangeResponse Execute(){

		String theRequest="TO="+MessageUser.kEnv.id()+" FROM="+MessageUser.kBenchmark.id();
		theRequest+=" CMD="+EnvMessageType.kEnvQueryVarRanges.id()+" VALTYPE="+MessageValueType.kNone.id()+" VALS=NULL";

		String responseMessage=RLGlue.RL_env_message(theRequest);

		GenericMessage theGenericResponse=new GenericMessage(responseMessage);

		String thePayLoadString=theGenericResponse.getPayLoad();

		StringTokenizer obsTokenizer = new StringTokenizer(thePayLoadString, ":");

		String numValuesToken=obsTokenizer.nextToken();
		int numValues=Integer.parseInt(numValuesToken);
		assert(numValues>=0);

		Vector<Double> mins=new Vector<Double>();
		Vector<Double> maxs=new Vector<Double>();

		for(int i=0;i<numValues;i++){
			mins.add(Double.parseDouble(obsTokenizer.nextToken()));
			maxs.add(Double.parseDouble(obsTokenizer.nextToken()));
		}

		EnvRangeResponse theResponse=new EnvRangeResponse(mins, maxs);
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
