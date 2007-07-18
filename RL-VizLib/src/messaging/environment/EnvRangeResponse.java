package messaging.environment;

import java.util.Vector;

import messaging.AbstractResponse;
import messaging.MessageUser;
import messaging.MessageValueType;

public class EnvRangeResponse extends AbstractResponse{
	private Vector<Double> mins;
	private Vector<Double> maxs;
	
	public EnvRangeResponse(Vector<Double> mins, Vector<Double> maxs){
		this.mins=mins;
		this.maxs=maxs;
	}

	public String toString() {
		String theResponse="EnvRangeResponse: " + mins.size()+" variables, they are:";
		for(int i=0;i<mins.size();i++){
			theResponse+=" ("+mins.get(i)+","+maxs.get(i)+") ";
		}
		// TODO Auto-generated method stub
		return theResponse;
	}

	public Vector<Double> getMins() {
		return mins;
	}

	public Vector<Double> getMaxs() {
		return maxs;
	}

	@Override
	public String makeStringResponse() {
		String theResponseString="TO="+MessageUser.kBenchmark.id()+" FROM="+MessageUser.kEnv.id();
		theResponseString+=" CMD="+EnvMessageType.kEnvResponse.id()+" VALTYPE="+MessageValueType.kStringList.id()+" VALS=";
		
		theResponseString+=mins.size()+":";
		
		for(int i=0;i<mins.size();i++){
			theResponseString+=mins.get(i)+":"+maxs.get(i)+":";
		}
		
		return theResponseString;
	}
};