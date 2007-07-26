package messaging.environmentShell;
import messaging.AbstractMessage;
import messaging.GenericMessage;
import visualization.interfaces.getEnvMaxMinsInterface;


	public class EnvironmentShellMessages extends AbstractMessage{
	
		public EnvironmentShellMessages(GenericMessage theMessageObject){
			super(theMessageObject);
		}

		
		public String handleAutomatically(getEnvMaxMinsInterface theEnvironment){
			return "no response";
		}

	};


