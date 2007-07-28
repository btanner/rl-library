package rlVizLib.messaging.environment;

import java.util.StringTokenizer;
import java.util.Vector;

import rlVizLib.messaging.AbstractMessage;
import rlVizLib.messaging.AbstractResponse;
import rlVizLib.messaging.GenericMessage;
import rlVizLib.messaging.MessageUser;
import rlVizLib.messaging.MessageValueType;
import rlVizLib.messaging.NotAnRLVizMessageException;


public class EnvRangeResponse extends AbstractResponse{
	private Vector<Double> mins=null;
	private Vector<Double> maxs=null;
	
	public EnvRangeResponse(Vector<Double> mins, Vector<Double> maxs){
		this.mins=mins;
		this.maxs=maxs;
	}

	public EnvRangeResponse(String responseMessage) throws NotAnRLVizMessageException {

		GenericMessage theGenericResponse = new GenericMessage(responseMessage);

		String thePayLoadString=theGenericResponse.getPayLoad();

		StringTokenizer obsTokenizer = new StringTokenizer(thePayLoadString, ":");

		String numValuesToken=obsTokenizer.nextToken();
		int numValues=Integer.parseInt(numValuesToken);


		mins=new Vector<Double>();
		maxs=new Vector<Double>();

		for(int i=0;i<numValues;i++){
			mins.add(Double.parseDouble(obsTokenizer.nextToken()));
			maxs.add(Double.parseDouble(obsTokenizer.nextToken()));
		}

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

		String thePayLoadString=mins.size()+":";
		
		for(int i=0;i<mins.size();i++){
			thePayLoadString+=mins.get(i)+":"+maxs.get(i)+":";
		}
		
		String theResponse=AbstractMessage.makeMessage(
				MessageUser.kBenchmark.id(),
				MessageUser.kEnv.id(),
				EnvMessageType.kEnvResponse.id(),
				MessageValueType.kStringList.id(),
				thePayLoadString);
		
		return theResponse;
	
		
	}
};