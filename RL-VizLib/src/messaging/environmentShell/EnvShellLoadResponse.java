package messaging.environmentShell;

import java.util.StringTokenizer;

import messaging.AbstractMessage;
import messaging.AbstractResponse;
import messaging.GenericMessage;
import messaging.MessageUser;
import messaging.MessageValueType;
import messaging.NotAnRLVizMessageException;

public class EnvShellLoadResponse extends AbstractResponse{
//	Constructor when the Shell is responding to the load request
	boolean theResult;

	public EnvShellLoadResponse(boolean theResult){
		this.theResult=theResult;
	}

//	Constructor when the benchmark is interpreting the returned response
	public EnvShellLoadResponse(String theMessage) throws NotAnRLVizMessageException {
		GenericMessage theGenericResponse=new GenericMessage(theMessage);

		String thePayLoadString=theGenericResponse.getPayLoad();
		
		System.out.println("Env Shell Load response is: "+theMessage);

		StringTokenizer obsTokenizer = new StringTokenizer(thePayLoadString, ":");
		String loadResult=obsTokenizer.nextToken();
		String loadMessage=obsTokenizer.nextToken();

		if(!loadResult.equals("SUCCESS")){
			System.err.println("Didn't load remote environment for reason: "+loadMessage);
		}
	}




	@Override
	public String makeStringResponse() {
		String thePayLoadString="";

		if(theResult)
			thePayLoadString+="SUCCESS:No Message";
		else
			thePayLoadString+="FAILURE:No Message";

		String theResponse=AbstractMessage.makeMessage(
				MessageUser.kBenchmark.id(),
				MessageUser.kEnvShell.id(),
				EnvShellMessageType.kEnvShellResponse.id(),
				MessageValueType.kStringList.id(),
				thePayLoadString);


		return theResponse;
		}


};