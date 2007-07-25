package messaging.environmentShell;

public enum EnvShellMessageType{
	kEnvShellResponse(0),
	kEnvShellListQuery(1),
	kEnvShellLoad(2),
	kEnvShellUnLoad(3);
	
	private final int id;
	
	EnvShellMessageType(int id){
        this.id = id;
    }
    public int id()   {return id;}
}