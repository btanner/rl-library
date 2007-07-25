package messaging.agent;
import messaging.AbstractMessage;
import messaging.GenericMessage;
import visualization.QueryableAgent;

		public class AgentMessages extends AbstractMessage{
			
			public AgentMessages(GenericMessage theMessageObject){
				super(theMessageObject);
			}

			
			public String handleAutomatically(QueryableAgent theAgent){
				return "no response";
			}

		};

