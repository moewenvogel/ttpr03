package battleship.events;

public class InitEvent {

	public final int ships;
	public final int player;

	private InitEvent(int ships, int player) {
		this.ships = ships;
		this.player = player;
	}
	
	public static InitEvent valueOf(int ships,int player){
		return new InitEvent(ships,player);
	}

	public int ships() {
		return ships;
	}

	public int player() {
		return player;
	}

	@Override
	public String toString() {
		return "InitEvent(ships=" + ships + ", player=" + player + ")";
	}
}
