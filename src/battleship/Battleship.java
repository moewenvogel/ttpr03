package battleship;

import battleship.events.HitEvent;
import battleship.events.InitEvent;
import battleship.events.NotHitEvent;
import battleship.gui.BattleshipGui;
import battleship.logic.Game;

import com.google.common.eventbus.EventBus;

public class Battleship {

	public static final EventBus bus = new EventBus();
	private final BattleshipGui gui = new BattleshipGui();
	
	private Battleship(){
		bus.register(gui);
		Game.testLocal();
//		test();
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
	
	private void test(){
		s();
		bus.post(InitEvent.valueOf(10,10));
		s();
		bus.post(NotHitEvent.valueOf(5,4));
		s();
		bus.post(NotHitEvent.valueOf(2,8));
		s();
		bus.post(NotHitEvent.valueOf(5,8));
		s();
		bus.post(NotHitEvent.valueOf(2,1));
		s();
		bus.post(HitEvent.valueOf(5,3));
	}
	
	private void s(){
		try {
			Thread.sleep(800);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
