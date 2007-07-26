package messaging.environment;


import java.util.StringTokenizer;

import messaging.AbstractMessage;
import messaging.AbstractResponse;
import messaging.GenericMessage;
import messaging.MessageUser;
import messaging.MessageValueType;
import messaging.NotAnRLVizMessageException;
import messaging.environment.EnvMessageType;

public class EnvVersionSupportedResponse extends AbstractResponse{
	int majorRevision;
	int minorRevision;
	

	public EnvVersionSupportedResponse(int majorRevision, int minorRevision){
		this.majorRevision=majorRevision;
		this.minorRevision=minorRevision;
	}

	public EnvVersionSupportedResponse(String responseMessage) throws NotAnRLVizMessageException {
		GenericMessage theGenericResponse = new GenericMessage(responseMessage);

		
		String thePayLoadString=theGenericResponse.getPayLoad();

		StringTokenizer versionTokenizer = new StringTokenizer(thePayLoadString, ":");

		majorRevision=Integer.parseInt(versionTokenizer.nextToken());
		minorRevision=Integer.parseInt(versionTokenizer.nextToken());
	}


	@Override
	public String makeStringResponse() {
		StringBuffer thePayLoadBuffer= new StringBuffer();


		thePayLoadBuffer.append(majorRevision);
		thePayLoadBuffer.append(":");
		thePayLoadBuffer.append(minorRevision);
		
		String theResponse=AbstractMessage.makeMessage(
				MessageUser.kBenchmark.id(),
				MessageUser.kEnv.id(),
				EnvMessageType.kEnvResponse.id(),
				MessageValueType.kStringList.id(),
				thePayLoadBuffer.toString());
		
		return theResponse;		
	
}

	public int getMajorRevision() {
		return majorRevision;
	}

	public int getMinorRevision() {
		return minorRevision;
	}

};