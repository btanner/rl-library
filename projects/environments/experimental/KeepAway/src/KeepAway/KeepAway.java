package KeepAway;

import rlVizLib.Environments.EnvironmentBase;
import rlVizLib.general.RLVizVersion;
import rlVizLib.messaging.interfaces.HasAVisualizerInterface;

import rlglue.environment.Environment;
import rlglue.types.Action;
import rlglue.types.Observation;
import rlglue.types.Random_seed_key;
import rlglue.types.Reward_observation;
import rlglue.types.State_key;





public class KeepAway implements HasAVisualizerInterface, Environment{
	
    protected Reward_observation makeRewardObservation(double reward, boolean isTerminal){
		Reward_observation RO=new Reward_observation();
		RO.r=reward;
		
		RO.terminal=1;
		if(!isTerminal){
			RO.terminal=0;
                        RO.o=m_kGlueSupport.makeObservation();
                }else{
                    RO.o=new Observation(0,0);
                }

		return RO;
	}
    SoccerPitch P=null;

        
	boolean rallyGame=false;
//	private boolean visualizerNameRequested=false;
	
	KeepAwayGlueSupport m_kGlueSupport=null;
	
	public KeepAway(){
		m_kGlueSupport=new KeepAwayGlueSupport(this);
	}
	


	public String env_init() {
		P=new SoccerPitch(100,50);
		m_kGlueSupport.init(P);
		P.shouldLog=m_kGlueSupport.visualizerNameRequested();

//Garbage for now
		String taskSpec = "2:e:3_[f,f, i]_[-1.2,0.6]_[-0.07,0.07]_[0,10]:1_[i]_[0,2]:[0,1]";
		return taskSpec;
		
	}

	public String env_message(String theMessage) {
		return m_kGlueSupport.env_message(theMessage);
	}

	public void env_set_random_seed(Random_seed_key key) {
		// TODO Auto-generated method stub
		
	}

	public void env_set_state(State_key key) {
		// TODO Auto-generated method stub
		
	}

	public Observation env_start() {
                KeepAwayGlueSupport.getInstance().popAction();
                KeepAwayGlueSupport.getInstance().popPlayerToAct();
                
                P.reset();

		P.Update();
		while(!KeepAwayGlueSupport.getInstance().playerReadyToAct()&&P.gameActive&&!rallyGame){
			System.out.println("in env_start loop");
			P.Update();
		}
                
                System.out.println("After env_start loop: "+KeepAwayGlueSupport.getInstance().playerReadyToAct()+" : "+P.gameActive+" : "+rallyGame);

		return m_kGlueSupport.makeObservation();
		
	}

	//Need to find a place to build the message vector and update it...

	public Reward_observation env_step(Action action) {
            System.out.println("\tenv_step starting");
		//Do something with the agent's action
                KeepAwayGlueSupport.getInstance().setAction(action);
		P.Update();
                
                double immediateReward=1.0d;

            System.out.println("\tenv_step after first action");
		
//		System.out.println("Keeperact: "+P.keeperShouldAct+" \t gameActive: "+P.gameActive);
		while(!KeepAwayGlueSupport.getInstance().playerReadyToAct()&&P.gameActive&&!rallyGame){
//			System.out.println("in loop");
			P.Update();
                        immediateReward+=1.0d;
		}
            System.out.println("\tenv_step after loop, immediate reward is: "+immediateReward+" terminal is: "+!P.gameActive);
//		
//		
//		System.out.println(" --- Time Step --- ");
		
//		if(P.keeperShouldAct)
//			System.out.println("Ok, it's time to ask the agent for an action!");
		//maybe do a while loop here, either while episode not over or while the keepers can't kick the ball, or a timeout
//		int whoToKickTo=
		
//		if(Math.random()<.1){
//			//Do a kick
//			double xDir=Math.random()-.5;
//			double yDir=Math.random()-.5;
//			
//			P.getBall().Kick(new Vector2D(xDir,yDir), 1.0);
//		}
		boolean terminal=!P.gameActive;
		return 	makeRewardObservation(immediateReward,terminal);
		
	}

	public RLVizVersion getTheVersionISupport() {
		return new RLVizVersion(1,1);
	}

	public String getVisualizerClassName() {
		return "visualizers.KeepAway.KeepAwayVisualizer";
	}
	public void env_cleanup() {
		// TODO Auto-generated method stub
		
	}

	public Random_seed_key env_get_random_seed() {
		// TODO Auto-generated method stub
		return null;
	}

	public State_key env_get_state() {
		// TODO Auto-generated method stub
		return null;
	}

}
