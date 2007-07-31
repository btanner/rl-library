package rlVizLib.messaging.environment;
import rlVizLib.messaging.AbstractMessage;
import rlVizLib.messaging.GenericMessage;
import rlglue.environment.Environment;


	public class EnvironmentMessages extends AbstractMessage{
	
		public EnvironmentMessages(GenericMessage theMessageObject){
			super(theMessageObject);
		}

		
		public String handleAutomatically(Environment theEnvironment){
			return "no response";
		}

	};


