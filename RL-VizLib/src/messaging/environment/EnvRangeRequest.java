package messaging.environment;

import java.util.StringTokenizer;
import java.util.Vector;

import messaging.GenericMessage;
import messaging.MessageUser;
import messaging.MessageValueType;
import rlglue.RLGlue;

public class EnvRangeRequest extends EnvironmentMessages{

	public EnvRangeRequest(MessageUser from, MessageUser to, EnvMessageType theMessageType) {
		super(from, to, theMessageType);
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
}
