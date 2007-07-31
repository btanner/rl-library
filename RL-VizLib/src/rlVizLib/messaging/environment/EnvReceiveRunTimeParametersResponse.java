package rlVizLib.messaging.environment;



import rlVizLib.messaging.AbstractMessage;
import rlVizLib.messaging.AbstractResponse;
import rlVizLib.messaging.GenericMessage;
import rlVizLib.messaging.MessageUser;
import rlVizLib.messaging.MessageValueType;
import rlVizLib.messaging.NotAnRLVizMessageException;

public class EnvReceiveRunTimeParametersResponse extends AbstractResponse{
	boolean accepted=false;
	

	public EnvReceiveRunTimeParametersResponse(boolean accepted){
		this.accepted=accepted;
	}

	public EnvReceiveRunTimeParametersResponse(String responseMessage) throws NotAnRLVizMessageException {
		GenericMessage theGenericResponse = new GenericMessage(responseMessage);

		String thePayLoadString=theGenericResponse.getPayLoad();
		
		this.accepted=Boolean.parseBoolean(thePayLoadString);
	}


	@Override
	public String makeStringResponse() {
		StringBuffer thePayLoadBuffer= new StringBuffer();


		thePayLoadBuffer.append(this.accepted);
		
		String theResponse=AbstractMessage.makeMessage(
				MessageUser.kBenchmark.id(),
				MessageUser.kEnv.id(),
				EnvMessageType.kEnvResponse.id(),
				MessageValueType.kStringList.id(),
				thePayLoadBuffer.toString());
		
		return theResponse;		
	
}

		public boolean getAccepted(){
			return accepted;
		}
};