package messaging;

import java.util.StringTokenizer;

public class GenericMessageParser {

		public static MessageUser parseUser(String userChunk){
			System.out.println("GenericMessageParser got asked to parse "+userChunk);
			StringTokenizer tok=new StringTokenizer(userChunk,"=");
			tok.nextToken();
			String theUserString=tok.nextToken();
			
			int theIntValue=Integer.parseInt(theUserString);
			
			if(theIntValue==MessageUser.kAgent.id())
				return MessageUser.kAgent;
			if(theIntValue==MessageUser.kEnv.id())
				return MessageUser.kEnv;
			if(theIntValue==MessageUser.kAgentShell.id())
				return MessageUser.kAgentShell;
			if(theIntValue==MessageUser.kBenchmark.id())
				return MessageUser.kBenchmark;
			if(theIntValue==MessageUser.kEnvShell.id())
				return MessageUser.kEnvShell;

			Thread.dumpStack();
			return null;
			
		}

		public static int parseInt(String typeString) {
			StringTokenizer typeTokenizer=new StringTokenizer(typeString,"=");
			typeTokenizer.nextToken();
			String theCMD=typeTokenizer.nextToken();
			int theCMDInt=Integer.parseInt(theCMD);
			return theCMDInt;
		}
}
