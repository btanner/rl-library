package messaging;

import rlglue.RLGlue;

public class EnvRangeRequest extends EnvironmentMessages{
	
	public EnvRangeRequest(MessageUser from, MessageUser to, EnvMessageType theMessageType) {
		super(from, to, theMessageType);
	}

	public static EnvRangeResponse Execute(){

		String theRequest="TO="+MessageUser.kEnv.id()+" FROM="+MessageUser.kBenchmark.id();
		theRequest+=" CMD="+EnvMessageType.kEnvQueryVarRanges.id()+" VALTYPE="+MessageValueType.kNone.id()+" VALS=NULL";
		
		try {
			System.out.println("Benchmark: Sending request over network");
			String theResponse=RLGlue.RL_env_message(theRequest);
			System.out.println("Benchmark: Received request from network");
			
			System.out.println("Asked for variable ranges and got: "+theResponse+" in response");
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Exception when reading the response");
			e.printStackTrace();
		}
		return null;
		
	

//		std::string thePayLoad=GenericMessageResponse(responseMessage).getPayload();
//
//		std::istringstream iss(thePayLoad);
//
//		std::vector<float> mins;
//		std::vector<float> maxs;
//		
//	 	unsigned int numValues;
//		iss >>numValues;
//		iss.ignore();
//		
//		assert(numValues>=0);
//		
//		for(size_t i=0;i<numValues;i++){
//			float thisMin;
//			float thisMax;
//			
//			iss >>thisMin;
//			iss.ignore();
//			iss >> thisMax;
//			iss.ignore();
//			
//			mins.push_back(thisMin);
//			maxs.push_back(thisMax);
//		}
//
//		
//		EnvRangeResponse *theResponse=new EnvRangeResponse(mins, maxs);
//		return theResponse;
//	}
//	
}
}
