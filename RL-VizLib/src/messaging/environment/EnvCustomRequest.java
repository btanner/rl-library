package messaging.environment;

import messaging.MessageUser;

public class EnvCustomRequest extends EnvironmentMessages{
	String Payload=null;
	
	public EnvCustomRequest(MessageUser from, MessageUser to, EnvMessageType theMessageType, String Payload) {
		super(from, to, theMessageType);
		this.Payload=Payload;
	}

	public String getPayload() {
		return Payload;
	}

}
