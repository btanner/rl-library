package rlVizLib.messaging.environmentShell;
import rlVizLib.messaging.AbstractMessage;
import rlVizLib.messaging.GenericMessage;
import rlVizLib.visualization.interfaces.getEnvMaxMinsInterface;


	public class EnvironmentShellMessages extends AbstractMessage{
	
		public EnvironmentShellMessages(GenericMessage theMessageObject){
			super(theMessageObject);
		}

		
		public String handleAutomatically(getEnvMaxMinsInterface theEnvironment){
			return "no response";
		}

	};


