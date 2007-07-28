package rlVizLib.messaging.agent;

public enum AgentMessageType{
	kAgentResponse(0),
	kAgentQueryValuesForObs(1),
	kAgentCustom(2);
	
	private final int id;
	
	AgentMessageType(int id){
        this.id = id;
    }
    public int id()   {return id;}
}