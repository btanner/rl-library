package rlVizLib.messaging.environmentShell;

import java.util.StringTokenizer;

import rlVizLib.messaging.AbstractMessage;
import rlVizLib.messaging.AbstractResponse;
import rlVizLib.messaging.GenericMessage;
import rlVizLib.messaging.MessageUser;
import rlVizLib.messaging.MessageValueType;
import rlVizLib.messaging.NotAnRLVizMessageException;


public class EnvShellUnLoadResponse extends AbstractResponse{
	public EnvShellUnLoadResponse(){}

//	Constructor when the benchmark is interpreting the returned response
	public EnvShellUnLoadResponse(String theMessage) throws NotAnRLVizMessageException {
		GenericMessage theGenericResponse=new GenericMessage(theMessage);
	}




	@Override
	public String makeStringResponse() {
		String theResponse=AbstractMessage.makeMessage(
				MessageUser.kBenchmark.id(),
				MessageUser.kEnvShell.id(),
				EnvShellMessageType.kEnvShellResponse.id(),
				MessageValueType.kNone.id(),
				"NULL");

		return theResponse;
		}


};