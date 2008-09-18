package kaMessages;

public interface SoccerTeamFacadeInterface {

	public int getPlayerCount();
	public PlayerFacadeInterface getPlayer(int which);
	public String stringSerialize();
}
