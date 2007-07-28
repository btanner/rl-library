package rlVizLib.messaging.agent;
import rlVizLib.messaging.AbstractMessage;
import rlVizLib.messaging.GenericMessage;
import rlVizLib.visualization.QueryableAgent;

		public class AgentMessages extends AbstractMessage{
			
			public AgentMessages(GenericMessage theMessageObject){
				super(theMessageObject);
			}

			
			public String handleAutomatically(QueryableAgent theAgent){
				return "no response";
			}

		};

