package battleship.logic;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import battleship.Battleship;
import battleship.events.HitEvent;
import battleship.events.InitEvent;
import battleship.events.NotHitEvent;

import de.uniba.wiai.lspi.chord.com.Node;
import de.uniba.wiai.lspi.chord.data.ID;
import de.uniba.wiai.lspi.chord.data.URL;
import de.uniba.wiai.lspi.chord.service.NotifyCallback;
import de.uniba.wiai.lspi.chord.service.PropertiesLoader;
import de.uniba.wiai.lspi.chord.service.impl.ChordImpl;
import de.uniba.wiai.lspi.util.logging.Logger;

public class Game implements NotifyCallback {
		
	// Interval size
	public static final int I = 100;
	// Ships
	public static final int S = 10;
	// what ist that?
	boolean draw = false;
	// Adress amount of the game
	public static final BigInteger ADDRESS_AMOUNT = (new BigInteger("2"))
			.pow(160);

	// URI
	public final static String localhost = "127.0.0.1";
	public final boolean local;

	// testcounter for standardbroadcasttest
	public static ConcurrentHashMap<ID, Integer> testcounter = new ConcurrentHashMap<ID, Integer>();

	// Working vars
	private ChordImpl chord;
	private Logger logger;
	private Player player;
	private List<Player> allPlayers = new ArrayList<Player>();
	
	private Player shotAtMe = null;
	private Player lastTarget = null;
	private Player lastShooter = null;


	private Game(String host, int port, String bootHost, int bootPort,
			boolean local) {
		this.local = local;
		initGame();
		try {
			chord.join(url(host, port), url(bootHost, bootPort));
		} catch (Exception e) {
			System.err.println("game dont work " + e);
		}
	}

	private Game(String host, int port, boolean local) {
		this.local = local;
		initGame();
		try {
			chord.create(url(host, port));
		} catch (Exception e) {
			System.err.println("game dont work " + e);
		}
	}

	private void initGame() {
		PropertiesLoader.loadPropertyFile();
		chord = new ChordImpl();
		chord.setCallback(this);
		this.logger = Logger.getLogger(ChordImpl.class.getName()
				+ ".unidentified");
	}

	private String initProtocol() {
		if (local)
			return URL.KNOWN_PROTOCOLS.get(URL.LOCAL_PROTOCOL);
		else
			return URL.KNOWN_PROTOCOLS.get(URL.SOCKET_PROTOCOL);
	}

	private URL url(String host, int localPort) throws Exception {
		if (local)
			return new URL(initProtocol() + "://" + host + ":" + localPort
					+ "/");
		else
			return new URL(initProtocol() + "://" + host + ":" + localPort
					+ "/");
	}

	public static Game creatorNetwork(String host, int localPort) {
		return new Game(host, localPort, false);
	}

	public static Game creatorLocal(int localPort) {
		return new Game(localhost, localPort, true);
	}

	public static Game gameNetwork(String host, int port, String boothost,
			int bootPort) {
		return new Game(host, port, boothost, bootPort, false);
	}

	public static Game gameLocal(int port, int bootPort) {
		return new Game(localhost, port, localhost, bootPort, true);
	}

	public void init() {
		player = new Player(chord.getID(), chord.getURL(), ID.valueOf((chord
				.getPredecessorID().toBigInteger().add(BigInteger.ONE))
				.mod(ADDRESS_AMOUNT)));
		player.initializeField(true);
		// initialize the status map
		Map<Node, ID> nodes = chord.getRing();

		for (Map.Entry<Node, ID> entry : nodes.entrySet()) {
			Player p = new Player(entry.getKey().getNodeID(), entry.getKey()
					.getNodeURL(), entry.getValue());
			p.initializeField(false);
			if (!p.getId().equals(this.player.getId()))
				allPlayers.add(p);
		}
		Collections.sort(allPlayers);
	}

	public static void testNetwork() {
		String host = "141.22.28.165";
		int port = 2000;
		String bootHost = "141.22.28.156";
		int bootPort = 2000;

		List<Game> cbs = new ArrayList<Game>();

		 Game player = creatorNetwork(bootHost,bootPort);
		 cbs.add(player);

//		Game player = gameNetwork(host, port, bootHost, bootPort);
//		cbs.add(player);

		sleep(5000);

		for (Game g : cbs) {
			g.printFT();
			g.init();
			System.out.println(g.chord.getURL() + " has ships at: ");
			System.out.println(g.player.getAllShips());
			g.play();
		}

		System.out.println("Players:");
		for (Player p : cbs.get(0).allPlayers) {
			System.out.println(p);
			System.out.println(p.getNumFromID(p.getId()));
			System.out.println(p.getIDFromNum(p.getNumFromID(p.getId())));
			
		}

	
		// cbs.get(0).play();

		// standardbroadcasttest(cbs);
	}

	public static void testLocal() {

		int testAmount = 5;
		int bootPort = 2000;
		List<Game> cbs = new ArrayList<Game>();

		Game cb1 = creatorLocal(bootPort);
		cbs.add(cb1);

		for (int i = 1; i < testAmount; i++) {
			Game cb = gameLocal(2000 + i, bootPort);
			cbs.add(cb);
		}

		cbs.get(0).draw = true;
		sleep(5000);

		for (int i = 0; i < testAmount; i++) {
			cbs.get(i).printFT();
			cbs.get(i).init();
			System.out.println(cbs.get(i).chord.getURL() + " has ships at: ");
			System.out.println(cbs.get(i).player.getAllShips());
		}

		System.out.println("Players:");
		for (Player p : cbs.get(0).allPlayers) {
			System.out.println(p);
			System.out.println(p.getNumFromID(p.getId()));
			System.out.println(p.getIDFromNum(p.getNumFromID(p.getId())));
		}

		cbs.get(0).play();

		// standardbroadcasttest(cbs);
	}

	private static void standardbroadcasttest(List<Game> cbs) {
		for (int i = 0; i < 10; i++) {
			System.out.println("Test nr: " + i + " with current sender: "
					+ cbs.get(i).getString());
			cbs.get(i).chord.broadcast(cbs.get(i).chord.getID(), true);
			sleep(2000);
			System.out.println("");
		}

		for (Entry<ID, Integer> entry : testcounter.entrySet()) {
			System.out.println("Source: " + entry.getKey() + " Value: "
					+ entry.getValue());
		}
	}

	public void close() {
		chord.leave();
	}

	@Override
	public void retrieved(ID target) {
		shotAtMe=getDaddy(target);
		 boolean gotHit=player.gotShot(target);
		System.out.println("got hit! " + this.chord.getURL() + " and shot: "
				+ gotHit);
		System.out
				.println("Area which was hit: " + player.getNumFromID(target));

		
		if(gotHit){
			Battleship.bus().post(
					HitEvent.valueOf(this.player.getNumFromID(target), idOfPlayer(this.player)));
		}else{
			Battleship.bus().post(
					NotHitEvent.valueOf(this.player.getNumFromID(target), idOfPlayer(this.player)));
		}
		
		chord.broadcast(target, gotHit);
		
		if (player.getSunkenShips().size() < Game.S) {
			//randomStrat();
			sleep(1000);
			prioStrat();
		} else {
			System.out.println(player.fieldVis());
			for (int i = 0; i < allPlayers.size(); i++) {
				System.out.println(allPlayers.get(i).fieldVis());
			}
		}
	}

	private int idOfPlayer(Player p) {
		Integer i = allPlayers.indexOf(p);
		if (i == null && p == player)
			return 0;// first is player
		else
			return i + 1;
	}

	@Override
	public void broadcast(ID source, ID target, Boolean hit) {
		if (testcounter.get(source) != null) {
			testcounter.put(source, testcounter.get(source) + 1);
		} else {
			testcounter.put(source, 1);
		}

		System.out.println(getString() + " received Shot at "
				+ target.toString() + "which was " + (hit ? "hit" : "not hit"));

		Player p = getDaddy(target);

		if (hit) {
			boolean last = p.sunkShip(target);
			Battleship.bus().post(
					HitEvent.valueOf(p.getNumFromID(target), idOfPlayer(p)));
			if (last) {
				System.out.println("WAS LAST SHIP!!! " + p.toString()
						+ " IS DEAD!!!");
				if (p.equals(this.player)) {
					System.out.println("I WOOOON!!!!!!! YEAAAAAAAHHHH!!!!!!");
				}

			}
		} else {
			p.shotIntoWater(target);
			Battleship.bus().post(
					NotHitEvent.valueOf(p.getNumFromID(target), idOfPlayer(p)));
		}

		// remember backshooting
		setBackshooter(getDaddy(source), p);
	}

	private void setBackshooter(Player shooter, Player target) {

		shooter.allShooting++;
		if (lastTarget != null) {
			if (target.equals(lastShooter)) {
				shooter.backshooting++;
			}
		}
		lastTarget = target;
		lastShooter = shooter;
	}

	/*
	 * returns the responsible node for a specific id
	 */
	public Player getDaddy(ID id) {
		Player res = null;
		if (player.isMyNode(id))
			res = player;
		for (Player p : allPlayers) {
			if (p.isMyNode(id)) {
				res = p;
				break;
			}
		}

		return (res);
	}

	public void play() {
		// Init the GUI with amount of ships (x-axis) and players (y-axis)
		Battleship.bus().post(InitEvent.valueOf(I, allPlayers.size() + 1));
		System.out.println("Found " + allPlayers + " ENEMIES");
		Collections.sort(allPlayers);
		if (allPlayers.get(allPlayers.size() - 1).compareTo(
				player) < 0) {
			System.out.println("we are the first");
			prioStrat();
			
		} else System.out.println("we are NOT the first");
	}

	public void randomStrat() {
		Player player = allPlayers.get((int) (Math.random() * allPlayers.size()));
		int area = player.getAllNotShips().get(
				(int) (Math.random() * player.getAllNotShips().size()));

		System.out.println(player.toString() + "\n\t shoots at ship: "
				+ player.toString() + "\n\t and area: " + area);
		chord.retrieve(player.getIDFromNum(area));
		sleep(2000);
	}

	private List<Player> getRealEnemies(){
		List<Player> realEnemies=new ArrayList<Player>();
		for(Player p:allPlayers){
			if(!(player.isMyNode(p.getId()))){
				realEnemies.add(p);
			}
		}
		return realEnemies;
	}
	
	public void prioStrat() {
		List<Player> enemies=getRealEnemies();
		// next default target is the one who shot at me, or if i am the first,
		// than a random player.
		// (also) lowest priority

		
		Player target = (this.shotAtMe == null ? enemies.get((int) (Math
				.random() * enemies.size())) : shotAtMe);

		
		int area = target.getAllNotShips().get(
				(int) (Math.random() * target.getAllNotShips().size()));

		TreeMap<Player, Double> backshooting = getBackshootingMap();
		TreeMap<Player, Double> shipCounts = getShipCountMap();

		// the following target selection is only working if the first shoots
		// already happended

		if (shotAtMe != null) {

			Entry<Player, Double> first_backshooter = backshooting.firstEntry();
			Entry<Player, Double> lowestShipRatePlayer = shipCounts.lastEntry();

			// highest priority - somebody who shoots back
			if (first_backshooter.getValue() >= 0.99) {
				target = first_backshooter.getKey();
				System.out.println("found backshooting partner:");
				System.out.println("my ratio"
						+ this.player.getWaterShipsRatio() + " his ratio: "
						+ target.getWaterShipsRatio() + " win propability: "
						+ player.getWaterShipsRatio()
						/ target.getWaterShipsRatio());
			}
			//somebody with one rest ship and higher propabiliy than the best backshooter
			else if(lowestShipRatePlayer.getKey().getAllShips().size()==9 &&
					 lowestShipRatePlayer.getKey().getWaterShipsRatio() > first_backshooter
					.getValue()) {
				target=lowestShipRatePlayer.getKey();
				System.out.println("one ship target!");
				
			}
			
			/*
			 * assumption: not always shooting back --> he only shoots back at
			 * weaker enemys, but this is good enough --> find one who has a
			 * better ratio than me so that he will shoot back at me 
			 * - third highest priority
			*/
			else if(first_backshooter.getValue() > 0.5) {
				for (Entry<Player, Double> e : backshooting.entrySet()) {
					if (e.getKey().getWaterShipsRatio() >= this.player
							.getWaterShipsRatio()) {
						target = e.getKey();
						break;
					}
				}
				System.out.println("new target ist a small-ratio-backshooter");
			}
			
		
		}
		
		//For security
		while(target.getId().equals(this.player.getId())){
			target=enemies.get((int) (Math.random() * enemies.size()));
			System.out.println("chose random target for not choosing myself");
		}
		System.out.println(chord.getURL() + "\n\t shoots at ship: "
				+ target.toString() + "\n\t and area: " + area);
		chord.retrieve(target.getIDFromNum(area));
		
	}

	public void printFT() {
		System.out.println("Finger table " + chord.getURL().toString());
		List<Node> ft = chord.getFingerTable();
		Collections.sort(ft);
		System.out.println(chord.printFingerTable());
	}

	public String getString() {
		return (chord.getURL().toString());
	}

	private static void sleep(int t) {
		try {
			Thread.sleep(t);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private TreeMap<Player, Double> getBackshootingMap() {
		Map<Player, Double> backshootingRatios = new HashMap<Player, Double>();
		for (Player p : getRealEnemies()) {
			if(!p.getId().equals(this.player.getId())) {
				backshootingRatios.put(p, p.getBackshootingRatio());
			}
		}
		ValueComparator bvc = new ValueComparator(backshootingRatios);
		TreeMap<Player, Double> sortedBackshooting = new TreeMap<Player, Double>(
				bvc);
		sortedBackshooting.putAll(backshootingRatios);
		return sortedBackshooting;
	}

	

	private TreeMap<Player, Double> getShipCountMap() {
		Map<Player, Double> shipCount = new HashMap<Player, Double>();
		for (Player p : getRealEnemies()) {
			if(!p.getId().equals(this.player.getId())){
				shipCount.put(p, new Double(p.undiscoveredShips));
			}
			
		}
		ValueComparator bvc = new ValueComparator(shipCount);
		TreeMap<Player, Double> sortedShipCount = new TreeMap<Player, Double>(
				bvc);
		sortedShipCount.putAll(shipCount);
		return sortedShipCount;
	}

}

class ValueComparator implements Comparator<Player> {

	Map<Player, Double> base;

	public ValueComparator(Map<Player, Double> map) {
		this.base = map;
	}

	// Note: this comparator imposes orderings that are inconsistent with
	// equals.
	public int compare(Player a, Player b) {
		if (base.get(a) >= base.get(b)) {
			return -1;
		} else {
			return 1;
		} // returning 0 would merge keys
	}
}
