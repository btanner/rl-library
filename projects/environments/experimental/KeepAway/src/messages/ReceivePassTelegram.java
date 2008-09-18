package messages;

import finiteStateMachine.TelegramTypes;

public class ReceivePassTelegram extends Telegram {
	
	public ReceivePassTelegram(){
		super(TelegramTypes.receivePass);
	}
}
