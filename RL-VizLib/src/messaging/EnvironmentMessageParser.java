package messaging;

import java.util.StringTokenizer;

public class EnvironmentMessageParser extends GenericMessageParser{
		public static EnvironmentMessages parseMessage(String theMessage){
			StringTokenizer theTokenizer=new StringTokenizer(theMessage, " ");
			String toString=theTokenizer.nextToken();
			String fromString=theTokenizer.nextToken();
			String typeString=theTokenizer.nextToken();
			String valueString=theTokenizer.nextToken();
			String payLoadString=theTokenizer.nextToken();

			
			MessageUser toU=GenericMessageParser.parseUser(toString);
			MessageUser fromU=GenericMessageParser.parseUser(fromString);
			
			int cmdId=GenericMessageParser.parseInt(typeString);
			
			
			if(cmdId==EnvMessageType.kEnvQueryVarRanges.id()){
				System.out.println("In EnvironmentMessageParser -- realized the request was for Ranges... passing it off to mountaincar");
				return new EnvRangeRequest(toU, fromU,EnvMessageType.kEnvQueryVarRanges);
			}
			
			System.out.println("EnvironmentMessageParser - unknown query type: "+theMessage);
			Thread.dumpStack();
			System.exit(1);
//			EnvMessageType theMessageType=new EnvMessageType(theCMDInt);
//			System.out.println("The Type token was: "+typeString+" and I think the message type is: "+theCMDInt);

			return null;
	}
}
