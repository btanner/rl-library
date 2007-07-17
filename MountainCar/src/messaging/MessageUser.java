package messaging;


public enum MessageUser{
	kBenchmark(0),
	kEnvShell(1),
	kAgentShell(2),
	kEnv(3),
	kAgent(4);
	
	private final int id;
	
	MessageUser(int id){
        this.id = id;
    }
    public int id()   {return id;}
}
