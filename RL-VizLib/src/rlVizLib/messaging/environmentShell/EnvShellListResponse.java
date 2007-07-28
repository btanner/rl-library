package rlVizLib.messaging.environmentShell;

import java.util.StringTokenizer;
import java.util.Vector;

import rlVizLib.messaging.AbstractMessage;
import rlVizLib.messaging.AbstractResponse;
import rlVizLib.messaging.GenericMessage;
import rlVizLib.messaging.MessageUser;
import rlVizLib.messaging.MessageValueType;
import rlVizLib.messaging.NotAnRLVizMessageException;


public class EnvShellListResponse extends AbstractResponse{
	private Vector<String> theEnvList = new Vector<String>();

	public EnvShellListResponse(String responseMessage) throws NotAnRLVizMessageException {
		GenericMessage theGenericResponse=new GenericMessage(responseMessage);

		String thePayLoadString=theGenericResponse.getPayLoad();

		StringTokenizer envListTokenizer = new StringTokenizer(thePayLoadString, ":");

		String numEnvironmentsToken=envListTokenizer.nextToken();

		int numEnvironments=Integer.parseInt(numEnvironmentsToken);

		for(int i=0;i<numEnvironments;i++){
			theEnvList.add(envListTokenizer.nextToken());
		}

	}


	public EnvShellListResponse(Vector<String> envNameVector) {
		this.theEnvList=envNameVector;
	}


	@Override
	public String makeStringResponse() {

		String thePayLoadString=theEnvList.size()+":";

		for(int i=0;i<theEnvList.size();i++){
			thePayLoadString+=theEnvList.get(i)+":";
		}

		String theResponse=AbstractMessage.makeMessage(
				MessageUser.kBenchmark.id(),
				MessageUser.kEnvShell.id(),
				EnvShellMessageType.kEnvShellResponse.id(),
				MessageValueType.kStringList.id(),
				thePayLoadString);


		return theResponse;
	}

	public String toString() {
		String theString= "EnvShellList Response: "+theEnvList.toString();
		return theString;
	}


	public Vector<String> getTheEnvList() {
		return theEnvList;
	}

};