package battleship.events;

public class NotHitEvent {

	public final int ships;
	public final int player;

	private NotHitEvent(int ships, int player) {
		this.ships = ships;
		this.player = player;
	}
	
	public static NotHitEvent valueOf(int ships,int player){
		return new NotHitEvent(ships,player);
	}

	public int ships() {
		return ships;
	}

	public int player() {
		return player;
	}

	@Override
	public String toString() {
		return "NotHitEvent(ships=" + ships + ", player=" + player + ")";
	}
}
