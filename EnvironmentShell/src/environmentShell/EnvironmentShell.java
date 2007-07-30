package environmentShell;
import java.util.Vector;

import rlVizLib.general.ParameterHolder;
import rlVizLib.messaging.GenericMessage;
import rlVizLib.messaging.MessageUser;
import rlVizLib.messaging.NotAnRLVizMessageException;
import rlVizLib.messaging.environmentShell.EnvShellListResponse;
import rlVizLib.messaging.environmentShell.EnvShellLoadRequest;
import rlVizLib.messaging.environmentShell.EnvShellLoadResponse;
import rlVizLib.messaging.environmentShell.EnvShellMessageType;
import rlVizLib.messaging.environmentShell.EnvironmentShellMessageParser;
import rlVizLib.messaging.environmentShell.EnvironmentShellMessages;
import rlglue.Action;
import rlglue.Environment;
import rlglue.Observation;
import rlglue.Random_seed_key;
import rlglue.Reward_observation;
import rlglue.State_key;


public class EnvironmentShell implements Environment{
	private Environment theEnvironment = null;

	EnvLoadingHelper loadHelper=new EnvLoadingHelper();



	public void env_cleanup() {
		theEnvironment.env_cleanup();
	}

	public Random_seed_key env_get_random_seed() {
		return theEnvironment.env_get_random_seed();
	}

	public State_key env_get_state() {
		return theEnvironment.env_get_state();
	}

	public String env_init() {
		return theEnvironment.env_init();
	}

	public String env_message(String theMessage) {

		GenericMessage theGenericMessage;
		try {
			theGenericMessage = new GenericMessage(theMessage);
		} catch (NotAnRLVizMessageException e) {
			System.err.println("Someone sent EnvironmentShell a message that wasn't RL-Viz compatible");
			return "I only respond to RL-Viz messages!";
		}
		if(theGenericMessage.getTo().id()==MessageUser.kEnvShell.id()){

			//Its for me
			EnvironmentShellMessages theMessageObject=EnvironmentShellMessageParser.makeMessage(theGenericMessage);

			//Handle a request for the list of environments
			if(theMessageObject.getTheMessageType()==EnvShellMessageType.kEnvShellListQuery.id()){
				loadHelper.loadEnvFiles();
				Vector<String> envNameVector=loadHelper.getEnvNames();
				Vector<ParameterHolder> envParamVector=loadHelper.getParamHolders();
				
				EnvShellListResponse theResponse=new EnvShellListResponse(envNameVector,envParamVector);

				return theResponse.makeStringResponse();
			}

			//Handle a request to actually load the environment
			if(theMessageObject.getTheMessageType()==EnvShellMessageType.kEnvShellLoad.id()){
				
				EnvShellLoadRequest theCastedRequest=(EnvShellLoadRequest)theMessageObject;
				
				String envName=theCastedRequest.getEnvName();
				ParameterHolder theParams=theCastedRequest.getParameterHolder();

				//Actually "load" the environment
				theEnvironment=loadHelper.loadEnvironment(envName,theParams);

				EnvShellLoadResponse theResponse=new EnvShellLoadResponse(theEnvironment!=null);

				return theResponse.makeStringResponse();
			}


			System.err.println("Env shell doesn't know how to handle message: "+theMessage);
		}
		//IF it wasn't for me, pass it on
		return theEnvironment.env_message(theMessage);


	}


	public void env_set_random_seed(Random_seed_key arg0) {
		theEnvironment.env_set_random_seed(arg0);
	}

	public void env_set_state(State_key arg0) {
		theEnvironment.env_set_state(arg0);
	}

	public Observation env_start() {
		return theEnvironment.env_start();
	}

	public Reward_observation env_step(Action arg0) {
		return theEnvironment.env_step(arg0);
	}

}
