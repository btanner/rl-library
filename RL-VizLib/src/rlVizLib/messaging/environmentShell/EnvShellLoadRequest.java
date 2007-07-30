package rlVizLib.messaging.environmentShell;

import java.util.StringTokenizer;

import rlVizLib.general.ParameterHolder;
import rlVizLib.messaging.AbstractMessage;
import rlVizLib.messaging.GenericMessage;
import rlVizLib.messaging.MessageUser;
import rlVizLib.messaging.MessageValueType;
import rlVizLib.messaging.NotAnRLVizMessageException;
import rlglue.RLGlue;

public class EnvShellLoadRequest extends EnvironmentShellMessages{
	private String envName;
	private ParameterHolder theParams;

	public EnvShellLoadRequest(GenericMessage theMessageObject) {
		super(theMessageObject);

		StringTokenizer st=new StringTokenizer(super.getPayLoad(),":");
		this.envName=st.nextToken();
		theParams=new ParameterHolder(st.nextToken());
	}



	public ParameterHolder getTheParams() {
		return theParams;
	}



	//This is intended for debugging but works well to be just called to save code duplication
	public static String getRequestMessage(String envName, ParameterHolder theParams){

		String paramString="NULL";
		if(theParams!=null)paramString=theParams.stringSerialize();

		String payLoadString=envName+":"+paramString;

		return AbstractMessage.makeMessage(
				MessageUser.kEnvShell.id(),
				MessageUser.kBenchmark.id(),
				EnvShellMessageType.kEnvShellLoad.id(),
				MessageValueType.kString.id(),
				payLoadString);

	}
	public static EnvShellLoadResponse Execute(String envName, ParameterHolder theParams){
		String theRequestString=getRequestMessage(envName,theParams);

		String responseMessage=RLGlue.RL_env_message(theRequestString);

		EnvShellLoadResponse theResponse;
		try {
			theResponse = new EnvShellLoadResponse(responseMessage);
		} catch (NotAnRLVizMessageException e) {
			System.err.println("In EnvShellLoadRequest: response was not an RLViz Message");
			return null;
		}		return theResponse;


	}

	public String getEnvName() {
		return envName;
	}
	
	public ParameterHolder getParameterHolder(){
		return theParams;
	}

}
