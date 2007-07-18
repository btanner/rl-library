package messaging;

import java.util.Vector;

public class EnvRangeResponse extends AbstractResponse{
	private Vector<Double> mins;
	private Vector<Double> maxs;
	
	public EnvRangeResponse(Vector<Double> mins, Vector<Double> maxs){
		this.mins=mins;
		this.maxs=maxs;
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