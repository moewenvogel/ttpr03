import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import de.uniba.wiai.lspi.chord.com.Node;
import de.uniba.wiai.lspi.chord.data.ID;
import de.uniba.wiai.lspi.chord.data.URL;
import de.uniba.wiai.lspi.chord.service.NotifyCallback;
import de.uniba.wiai.lspi.chord.service.PropertiesLoader;
import de.uniba.wiai.lspi.chord.service.impl.ChordImpl;
import de.uniba.wiai.lspi.util.logging.Logger;

public class Game implements NotifyCallback {

	// Interval
	public static final int I = 100;
	// Ships
	public static final int S = 10;

	public static ConcurrentHashMap<ID, Integer> testcounter = new ConcurrentHashMap<ID, Integer>();
	public static final BigInteger ADDRESS_AMOUNT = (new BigInteger("2"))
			.pow(160);
	ChordImpl chord;
	Logger logger;
	Player player;
	Map<ID, Player> players = new HashMap<ID, Player>();

	private Game(int localPort, boolean isCreator) throws Exception {
		
			PropertiesLoader.loadPropertyFile();
	
		chord = new ChordImpl();
		chord.setCallback(this);

		this.logger = Logger.getLogger(ChordImpl.class.getName()
				+ ".unidentified");

		String protocol = URL.KNOWN_PROTOCOLS.get(URL.LOCAL_PROTOCOL);
		// URL localUrl = new URL(protocol +
		// "://127.0.0."+(int)(Math.random()*200+1)+":"+localPort +"/");
		URL localUrl = new URL(protocol + "://127.0.0.1:" + localPort + "/");
		URL bootstrapUrl = new URL(protocol + "://127.0.0.1:2000/");

		if (isCreator) {
			chord.create(bootstrapUrl);
		} else {
			chord.join(localUrl, bootstrapUrl);
		}
	}

	public static Game game(int localPort) throws Exception {
		return new Game(localPort, false);
	}

	public static Game creator(int localPort) throws Exception {
		return new Game(localPort, true);
	}

	public void init() {
		player = new Player(chord.getID(), chord.getURL(), ID.valueOf((chord
				.getPredecessorID().toBigInteger().add(BigInteger.ONE))
				.mod(ADDRESS_AMOUNT)));
	}

	public static boolean isFirst(String[] args) {
		if (args == null) {
			return false;
		} else if (args.length > 0 && args[0].equals("first")) {
			System.out.println("first");
			return true;
		} else {
			return false;
		}
	}

	/*
	 * TODO Player getIDFromNum und getNumFromID debuggen
	 */

	public static void main(String[] args) throws Exception {
		int testAmount = 10;
		int baseport = 8080;
		List<Game> cbs = new ArrayList<Game>();
		boolean starter = isFirst(args);

		Game cb1 = creator(baseport);
		cbs.add(cb1);

		for (int i = 1; i < testAmount; i++) {
			Game cb = game(2000 + i);
			cbs.add(cb);
		}

		Thread.sleep(5000);

		for (int i = 0; i < testAmount; i++) {
			cbs.get(i).printFT();
			cbs.get(i).init();
			cbs.get(i).player.initializeField(true);
			System.out.println(cbs.get(i).chord.getURL() + " has ships at: ");
			System.out.println(cbs.get(i).player.getAllShips());
		}

		System.out.println("printing playground");
		Map<Node, ID> playground = cbs.get(1).chord.getRing();

		List<Player> players = new ArrayList<Player>();

		for (Entry<Node, ID> entry : playground.entrySet()) {
			players.add(new Player(entry.getKey().getNodeID(), entry.getKey()
					.getNodeURL(), entry.getValue()));
		}
		Collections.sort(players);
		System.out.println("Players:");
		for (Player p : players) {
			System.out.println(p);
			System.out.println(p.getNumFromID(p.getId()));
			System.out.println(p.getIDFromNum(p.getNumFromID(p.getId())));
		}

		// GAME TEST - DO NOT DELETE
		for (int i = 0; i < 10; i++) {
			int ship = (int) (Math.random() * players.size());
			int area = (int) (Math.random() * Game.I);
			System.out.println(cbs.get(0).chord.getURL()
					+ "\n\t shoots at ship: " + cbs.get(ship).getString()
					+ "\n\t and area: " + area);
			cbs.get(0).chord.retrieve(players.get(ship).getIDFromNum(area));

			Thread.sleep(2000);
		}

		// standardbroadcasttest(cbs);
	}

	private static void standardbroadcasttest(List<Game> cbs) throws Exception {
		for (int i = 0; i < 10; i++) {
			System.out.println("Test nr: " + i + " with current sender: "
					+ cbs.get(i).getString());
			cbs.get(i).chord.broadcast(cbs.get(i).chord.getID(), true);
			Thread.sleep(2000);
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
		System.out.println("got hit! " + this.chord.getURL() + " and shot: "
				+ player.gotShot(target));
		System.out.println("Area which was hit: " + player.getNumFromID(target));
		boolean shotShip = player.gotShot(target);
		chord.broadcast(target, shotShip);
	}

	@Override
	public void broadcast(ID source, ID target, Boolean hit) {
		if (testcounter.get(source) != null) {
			testcounter.put(source, testcounter.get(source) + 1);
		} else {
			testcounter.put(source, 1);
		}

		boolean knockedOut = false;
		Player respNode = players.get(getDaddy(target));

		/*
		 * if(hit){ knockedOut=respNode.sunkShip(target); if(knockedOut)
		 * System.out.println(source+" HAS WON!!! "+respNode+" IS DEAD!!!");
		 * }else{ players.get(getDaddy(target)).shotIntoWater(target);
		 * System.out.println(source+" has shot into the water of "+respNode); }
		 */

		System.out.println(getString() + " received Shot at "
				+ target.toString() + "which was " + (hit ? "hit" : "not hit"));
	}

	/*
	 * returns the responsible node for a specific id
	 */
	public Player getDaddy(ID id) {
		Player res = null;
		for (Map.Entry<ID, Player> entry : players.entrySet()) {
			if (id.isInInterval(entry.getKey(), entry.getValue()
					.getLastRespID())) {
				res = entry.getValue();
				break;
			}
		}

		return (res);
	}

	public void play() {
		// calculate the number of players:
		int ftSize = chord.getFingerTable().size();
		int playerAmount = (int) Math.pow(2, 160) / ftSize;
		System.out.println("Recognised " + playerAmount + " players");

		// initialize the status map
		Map<Node, ID> nodes = chord.getRing();

		for (Map.Entry<Node, ID> entry : nodes.entrySet()) {
			Player p = new Player(entry.getKey().getNodeID(), entry.getKey()
					.getNodeURL(), entry.getValue());
			players.put(p.getId(), p);
		}

		// initialize own field

		// TODO game loop

	}

	public ID easyStrat() {
		/*
		 * Einfache Strategie: Schiesse auf den Knoten mit den meisten Schiffen,
		 * ausser jemand hat nur noch ein Schiff --> dann auf den schiessen
		 */

		return (null);
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

}
