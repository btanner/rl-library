package messaging.environment;
import rlglue.Environment;
import messaging.AbstractMessage;
import messaging.GenericMessage;
import visualization.interfaces.getEnvMaxMinsInterface;


	public class EnvironmentMessages extends AbstractMessage{
	
		public EnvironmentMessages(GenericMessage theMessageObject){
			super(theMessageObject);
		}

		
		public String handleAutomatically(Environment theEnvironment){
			return "no response";
		}

	};


