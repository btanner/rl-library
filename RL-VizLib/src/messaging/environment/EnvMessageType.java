package messaging.environment;

public enum EnvMessageType{
	kEnvResponse(0),
	kEnvQueryVarRanges(1),
	kEnvQueryObservationsForState(2),
	kEnvCustom(3),
	kEnvQuerySupportedVersion(4);
	
	private final int id;
	
	EnvMessageType(int id){
        this.id = id;
    }
    public int id()   {return id;}
}