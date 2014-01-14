package battleship;

import battleship.gui.BattleshipGui;
import battleship.logic.Game;

import com.google.common.eventbus.EventBus;

public class Battleship {

	public static final EventBus bus = new EventBus();
	private final BattleshipGui gui = new BattleshipGui();
	
	private Battleship(){
		bus.register(gui);
		Game.testNetwork();
		//Game.testLocal();
	}
	
	public static Battleship start(){
		return new Battleship();
	}
	
	public static EventBus bus(){
		return bus;
	}
	
	public static void main(String[] args) {
		new Battleship();
	}
}
