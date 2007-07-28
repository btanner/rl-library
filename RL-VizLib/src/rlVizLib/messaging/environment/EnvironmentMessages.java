package rlVizLib.messaging.environment;
import rlVizLib.messaging.AbstractMessage;
import rlVizLib.messaging.GenericMessage;
import rlVizLib.visualization.interfaces.getEnvMaxMinsInterface;
import rlglue.Environment;


	public class EnvironmentMessages extends AbstractMessage{
	
		public EnvironmentMessages(GenericMessage theMessageObject){
			super(theMessageObject);
		}

		
		public String handleAutomatically(Environment theEnvironment){
			return "no response";
		}

	};


