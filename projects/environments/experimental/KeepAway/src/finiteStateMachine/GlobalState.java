package finiteStateMachine;

import messages.Telegram;
import players.PlayerInterface;
import KeepAway.Prm;
import generalGameCode.Utilities;
import generalGameCode.Vector2D;

public class GlobalState extends State<PlayerInterface> {
	boolean debugThis=false;
	
	static GlobalState instance=null;
	static{
		instance=new GlobalState();
	}
	
	public static GlobalState Instance(){return instance;}
	
	@Override
	public
	void enter(PlayerInterface player) {

	}

//	@Override
	public void execute(PlayerInterface player) {

	}
	@Override
	public	void exit(PlayerInterface player) {
	}

	@Override
	public void receiveMessage(PlayerInterface theEntity, Telegram theMessage) {
		//Receive ball
		if(theMessage.type()==TelegramTypes.receivePass)theEntity.GetFSM().changeState(ReceivePass.Instance());
	}

}
