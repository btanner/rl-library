import rlVizLib.general.ParameterHolder;
import rlVizLib.messaging.environmentShell.EnvShellListRequest;
import rlVizLib.messaging.environmentShell.EnvShellListResponse;
import rlVizLib.messaging.environmentShell.EnvShellLoadRequest;

public class consoleTrainerHelper{
	private static void load(String envNameString, ParameterHolder theParams){
		EnvShellLoadRequest.Execute(envNameString,theParams);
	}
	
	private static ParameterHolder preload(String envNameString){
		EnvShellListResponse ListResponse = EnvShellListRequest.Execute();

		int thisEnvIndex=ListResponse.getTheEnvList().indexOf(envNameString);
		
		ParameterHolder p = ListResponse.getTheParamList().get(thisEnvIndex);
		return p;
		
	}
	
	private static void preloadAndLoad(String envNameString){
		ParameterHolder p=preload(envNameString);
		load(envNameString,p);
	}

	/*
	* Tetris has an integer parameter called pnum that takes values in [0,9]
	* Setting this parameter changes the exact tetris problem you are solving
	*/
	public static void loadTetris(int whichParamSet){
		String theEnvString="GeneralizedTetris - Java";
		ParameterHolder theParams=preload(theEnvString);
		theParams.setIntegerParam("pnum",whichParamSet);
		
		load(theEnvString, theParams);
	}

	/*
	* MountainCar has an integer parameter called pnum that takes values in [0,9]
	* Setting this parameter changes the exact tetris problem you are solving
	*/
	public static void loadMountainCar(int whichParamSet){
		String theEnvString="GeneralizedMountainCar - Java";
		ParameterHolder theParams=preload(theEnvString);
		theParams.setIntegerParam("pnum",whichParamSet);
		
		load(theEnvString, theParams);
	}
	
	/*
	* Helicopter has no user controllable parameters
	*/
	public static void loadHelicopter(){
		String theEnvString="AlteredHelicopter - Java";
		preloadAndLoad(theEnvString);
	}
}