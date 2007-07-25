package messaging.environmentShell;

import java.util.StringTokenizer;
import messaging.AbstractResponse;
import messaging.GenericMessage;
import messaging.MessageUser;
import messaging.MessageValueType;

public class EnvShellLoadResponse extends AbstractResponse{
//	Constructor when the Shell is responding to the load request
	boolean theResult;

	public EnvShellLoadResponse(boolean theResult){
		this.theResult=theResult;
	}

//	Constructor when the benchmark is interpreting the returned response
	public EnvShellLoadResponse(String theMessage) {
		GenericMessage theGenericResponse=new GenericMessage(theMessage);

		String thePayLoadString=theGenericResponse.getPayLoad();

		StringTokenizer obsTokenizer = new StringTokenizer(thePayLoadString, ":");
		String loadResult=obsTokenizer.nextToken();
		String loadMessage=obsTokenizer.nextToken();

		if(!loadResult.equals("SUCCESS")){
			System.err.println("Didn't load remote environment for reason: "+loadMessage);
		}
	}




	@Override
	public String makeStringResponse() {
		String theResponseString="TO="+MessageUser.kBenchmark.id()+" FROM="+MessageUser.kEnvShell.id();
		theResponseString+=" CMD="+EnvShellMessageType.kEnvShellResponse.id()+" VALTYPE="+MessageValueType.kStringList.id()+" VALS=";
		
		if(theResult)
			theResponseString+="SUCCESS:No Message";
		else
			theResponseString+="FAILURE:No Message";

		//Right now we just always say it was a success

		return theResponseString;
	}


};