package messaging;
import java.util.StringTokenizer;
import java.util.Vector;







	public class EnvironmentMessages{
	
		private EnvMessageType theMessageType;
		private MessageUser from;
		private MessageUser to;
		
		public void EnvironmentMessages(MessageUser from, MessageUser to, EnvMessageType theMessageType){
			this.from=from;
			this.to=to;
			this.theMessageType=theMessageType;
		}
		
		EnvMessageType getMessageType(){return theMessageType;}
	};




	class EnvRangeResponse extends EnvironmentMessages{
		private Vector<Float> mins;
		private Vector<Float> maxs;
		
		public void EnvRangeResponse(Vector<Float> mins, Vector<Float> maxs){
			this.mins=mins;
			this.maxs=maxs;
		}

		public Vector<Float> getMins() {
			return mins;
		}

		public Vector<Float> getMaxs() {
			return maxs;
		}
	};
//
//
//	class Message_Env_Query_VarRanges : public EnvironmentMessage{
//	private:
//		
//	public:
//		Message_Env_Query_VarRanges(messageUser from, messageUser to);
//		virtual ~Message_Env_Query_VarRanges();
//		std::string makeResponse(std::vector<float> mins, std::vector<float> maxs);
//
//		static EnvRangeResponse* createAndQuery();
//		
//	};
//
//
//
//	class EnvQueryStateObsResponse{
//		public:
//			EnvQueryStateObsResponse(std::vector<DataHolder *> *theObservations);
//			~EnvQueryStateObsResponse();
//
//			std::vector<DataHolder *> *theObservations;
//	};
//
//
//	class Message_Env_Query_ObservationsForStates : public EnvironmentMessage{
//	private:
//		std::vector<DataHolder *> theStates;
//	public:
//		Message_Env_Query_ObservationsForStates(messageUser from, messageUser to,std::vector<DataHolder *> theStates);
//		virtual ~Message_Env_Query_ObservationsForStates();
//
//		std::string makeResponse(Environment *theEnv);
//
//		static EnvQueryStateObsResponse* createAndQuery(const std::vector<DataHolder *> &theQueryStates);
//
//	};
//
//
//	class Message_Env_Custom : public EnvironmentMessage{
//	private:
//		std::string theMessage;
//	public:
//		Message_Env_Custom(messageUser from, messageUser to,std::string theMessage);
//		virtual ~Message_Env_Custom();
//
//		std::string getTheMessage();
//	};
//
	


//		std::string AbstractMessage::parseMessagePrefix(const std::string theMessage, messageUser &toUser, messageUser &fromUser, int &msgType, messageValType &valueType){
//			int tempType;
//
//			std::istringstream iss(theMessage);
//
//			std::string spaceEater;
//			std::string messagePayload;
//
//			std::getline(iss, spaceEater, '=' );
//			iss >> tempType;
//			toUser=(messageUser)tempType;
//
//			std::getline(iss, spaceEater, '=' );
//			iss >> tempType;
//			fromUser=(messageUser)tempType;
//
//			std::getline(iss, spaceEater, '=' );
//			iss >> tempType;
//			msgType=tempType;
//
//			std::getline(iss, spaceEater, '=' );
//			iss >> tempType;
//			valueType=(messageValType)tempType;
//
//
//			std::getline(iss, spaceEater, '=' );
//			std::getline(iss, messagePayload);
//			
//			return messagePayload;
//		}

