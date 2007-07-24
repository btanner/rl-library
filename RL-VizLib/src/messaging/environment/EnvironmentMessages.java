package messaging.environment;
import visualization.QueryableEnvironment;
import messaging.MessageUser;







	public class EnvironmentMessages{
	
		private EnvMessageType theMessageType;
		private MessageUser from;
		private MessageUser to;
		
		public EnvironmentMessages(MessageUser from, MessageUser to, EnvMessageType theMessageType){
			this.from=from;
			this.to=to;
			this.theMessageType=theMessageType;
		}
		
		EnvMessageType getMessageType(){return theMessageType;}

		public EnvMessageType getTheMessageType() {
			return theMessageType;
		}

		public MessageUser getFrom() {
			return from;
		}

		public MessageUser getTo() {
			return to;
		}
		
		/*
		 * Override this if you can handle automatically given a queryable environment
		 */
		public boolean canHandleAutomatically() {
			return false;
		}
		
		public String handleAutomatically(QueryableEnvironment theEnvironment){
			return "no response";
		}

	};


