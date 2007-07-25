package messaging.environmentShell;

import java.util.StringTokenizer;
import java.util.Vector;

import messaging.AbstractResponse;
import messaging.GenericMessage;
import messaging.MessageUser;
import messaging.MessageValueType;

public class EnvShellListResponse extends AbstractResponse{
	private Vector<String> theEnvList = new Vector<String>();
	
	public EnvShellListResponse(String responseMessage) {
		GenericMessage theGenericResponse=new GenericMessage(responseMessage);

		String thePayLoadString=theGenericResponse.getPayLoad();

		StringTokenizer obsTokenizer = new StringTokenizer(thePayLoadString, ":");

		String numEnvironmentsToken=obsTokenizer.nextToken();

		int numEnvironments=Integer.parseInt(numEnvironmentsToken);

		for(int i=0;i<numEnvironments;i++){
			theEnvList.add(obsTokenizer.nextToken());
		}

	}


	public EnvShellListResponse(Vector<String> envNameVector) {
		this.theEnvList=envNameVector;
	}


	@Override
	public String makeStringResponse() {
		String theResponseString="TO="+MessageUser.kBenchmark.id()+" FROM="+MessageUser.kEnvShell.id();
		theResponseString+=" CMD="+EnvShellMessageType.kEnvShellResponse.id()+" VALTYPE="+MessageValueType.kStringList.id()+" VALS=";
		
		theResponseString+=theEnvList.size()+":";
		
		for(int i=0;i<theEnvList.size();i++){
			theResponseString+=theEnvList.get(i)+":";
		}
		
		return theResponseString;
	}
	
	public String toString() {
		String theString= "EnvShellList Response: "+theEnvList.toString();
		return theString;
	}


	public Vector<String> getTheEnvList() {
		return theEnvList;
	}

};