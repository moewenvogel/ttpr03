package battleship.gui;

import java.awt.event.KeyListener;

import battleship.events.HitEvent;
import battleship.events.InitEvent;
import battleship.events.NotHitEvent;
import battleship.logic.Game;

import com.google.common.eventbus.Subscribe;

import ch.aplu.jgamegrid.Actor;
import ch.aplu.jgamegrid.GGButton;
import ch.aplu.jgamegrid.GGButtonListener;
import ch.aplu.jgamegrid.GameGrid;
import ch.aplu.jgamegrid.Location;

public class BattleshipGui extends GameGrid {

	int player;
	// imagesize of cells in px
	public final static int imagesize = 25;

	private static final long serialVersionUID = 7378323006789259694L;

	public void addActor(Actor a, Location l) {
		// only one actor in one grid
		removeActorsAt(l);
		super.addActor(a, l);
	}

	public BattleshipGui() {
		super(50, 18, imagesize, java.awt.Color.black,
				"images/backgroundbig.gif");
		setTitle("Battleship");
	}
	
	@Subscribe
	public void initListener(InitEvent e) {
		System.out.println(e);
		player = e.player();
		setNbHorzCells(e.ships());
		setNbVertCells(e.player()*2);
		for (Location l : getEmptyLocations()) {
			addActor(new CouldBe(), l);
		}
		show();
		setVisible(true);
	}

	@Subscribe
	public void hitListener(HitEvent e) {
		System.out.println(e);
		if (e.ships() >= 50){
			addActor(new Hit(), new Location(e.ships()-50, player+e.player()));
		}
		else
			addActor(new Hit(), new Location(e.ships(), e.player()));
	}

	@Subscribe
	public void nothitListener(NotHitEvent e) {
		System.out.println(e);
		if (e.ships() >= 50){
			addActor(new NotHit(), new Location(e.ships()-50, player+e.player()));
		}
		else
			addActor(new NotHit(), new Location(e.ships(), e.player()));
	}

	public static String imagefolder() {
		return imagesize + "x" + imagesize;
	}

}
