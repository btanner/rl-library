package messaging.environmentShell;
import messaging.AbstractMessage;
import messaging.GenericMessage;
import visualization.QueryableEnvironment;


	public class EnvironmentShellMessages extends AbstractMessage{
	
		public EnvironmentShellMessages(GenericMessage theMessageObject){
			super(theMessageObject);
		}

		
		public String handleAutomatically(QueryableEnvironment theEnvironment){
			return "no response";
		}

	};


