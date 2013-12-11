package battleship.events;

public class HitEvent {

	public final int ships;
	public final int player;

	private HitEvent(int ships, int player) {
		this.ships = ships;
		this.player = player;
	}
	
	public static HitEvent valueOf(int ships,int player){
		return new HitEvent(ships,player);
	}

	public int ships() {
		return ships;
	}

	public int player() {
		return player;
	}

	@Override
	public String toString() {
		return "HitEvent(ships=" + ships + ", player=" + player + ")";
	}
}
