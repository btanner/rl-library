package messaging;

import java.util.StringTokenizer;

public class EnvironmentMessageParser{
		public static EnvironmentMessages parseMessage(String theMessage){
			StringTokenizer theTokenizer=new StringTokenizer(theMessage, " ");
			String toString=theTokenizer.nextToken();
			String fromString=theTokenizer.nextToken();
			String typeString=theTokenizer.nextToken();
			String valueString=theTokenizer.nextToken();
			String payLoadString=theTokenizer.nextToken();

			StringTokenizer typeTokenizer=new StringTokenizer(typeString,"=");
			String cmdLabel=typeTokenizer.nextToken();
			String theCMD=typeTokenizer.nextToken();
			Integer theCMDInt=Integer.parseInt(theCMD);
			
			if(theCMDInt==EnvMessageType.kEnvQueryVarRanges.id())
				System.out.println("It is a query for ranges");
//			EnvMessageType theMessageType=new EnvMessageType(theCMDInt);
//			System.out.println("The Type token was: "+typeString+" and I think the message type is: "+theCMDInt);

			return null;
	}
}
