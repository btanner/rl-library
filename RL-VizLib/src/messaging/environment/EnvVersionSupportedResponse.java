package messaging.environment;


import general.RLVizVersion;

import java.util.StringTokenizer;

import messaging.AbstractMessage;
import messaging.AbstractResponse;
import messaging.GenericMessage;
import messaging.MessageUser;
import messaging.MessageValueType;
import messaging.NotAnRLVizMessageException;
import messaging.environment.EnvMessageType;

public class EnvVersionSupportedResponse extends AbstractResponse{
	RLVizVersion theVersion=null;
	

	public EnvVersionSupportedResponse(RLVizVersion theVersion){
		this.theVersion=theVersion;
	}

	public EnvVersionSupportedResponse(String responseMessage) throws NotAnRLVizMessageException {
		GenericMessage theGenericResponse = new GenericMessage(responseMessage);

		
		String thePayLoadString=theGenericResponse.getPayLoad();

		StringTokenizer versionTokenizer = new StringTokenizer(thePayLoadString, ":");

		theVersion=new RLVizVersion(versionTokenizer.nextToken());
	}


	@Override
	public String makeStringResponse() {
		StringBuffer thePayLoadBuffer= new StringBuffer();


		thePayLoadBuffer.append(theVersion.serialize());
		
		String theResponse=AbstractMessage.makeMessage(
				MessageUser.kBenchmark.id(),
				MessageUser.kEnv.id(),
				EnvMessageType.kEnvResponse.id(),
				MessageValueType.kStringList.id(),
				thePayLoadBuffer.toString());
		
		return theResponse;		
	
}

	public RLVizVersion getTheVersion() {
		return theVersion;
	}

};