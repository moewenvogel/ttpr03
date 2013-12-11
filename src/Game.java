import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
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
	private List<Player> enemies = new ArrayList<Player>();
	public static Gui gui;


	public static void main(String[] args) throws Exception {
		testNetwork();
	}
	
	private Game(String host,int port,String bootHost,int bootPort, boolean local) throws Exception {
		this.local = local;
		initGame();
		chord.join(url(host,port), url(bootHost,bootPort));
	}

	private Game(String host,int port, boolean local) throws Exception {
		this.local = local;
		initGame();
		chord.create(url(host,port));
	}
	
	private void initGame() throws Exception{
		PropertiesLoader.loadPropertyFile();
		chord = new ChordImpl();
		chord.setCallback(this);
		this.logger = Logger.getLogger(ChordImpl.class.getName()
				+ ".unidentified");
	}
	
	private String initProtocol(){
		if (local)
			return URL.KNOWN_PROTOCOLS.get(URL.LOCAL_PROTOCOL);
		else
			return URL.KNOWN_PROTOCOLS.get(URL.SOCKET_PROTOCOL);
	}
	
	private URL url(String host,int localPort) throws Exception{
		if(local)
			return new URL(initProtocol() + "://"+host+":" + localPort + "/");
		else
			return new URL(initProtocol() + "://"+host+":" + localPort + "/");
	}

	public static Game creatorNetwork(String host,int localPort) throws Exception {
		return new Game(host,localPort,false);
	}
	
	public static Game creatorLocal(int localPort) throws Exception {
		return new Game(localhost,localPort,true);
	}

	public static Game gameNetwork(String host,int port,String boothost,int bootPort) throws Exception {
		return new Game(host,port,boothost,bootPort,false);
	}
	
	public static Game gameLocal(int port,int bootPort) throws Exception {
		return new Game(localhost,port,localhost,bootPort,true);
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
				enemies.add(p);
		}
		Collections.sort(enemies);
	}

	public static void testNetwork() throws Exception {
		String host = "141.22.64.99";
		int port = 2000;
		String bootHost = "141.22.95.8";
		int bootPort = 1234;
		
		gui = new Gui();
		gui.frame.setVisible(true);

		List<Game> cbs = new ArrayList<Game>();

//		 Game creator = creatorNetwork(bootHost,bootPort);
//		 cbs.add(creator);

		Game player = gameNetwork(host,port,bootHost,bootPort);
		cbs.add(player);

		cbs.get(0).draw = true;
		Thread.sleep(5000);

		for (Game g:cbs) {
			g.printFT();
			g.init();
			System.out.println(g.chord.getURL() + " has ships at: ");
			System.out.println(g.player.getAllShips());
		}

		System.out.println("Players:");
		for (Player p : cbs.get(0).enemies) {
			System.out.println(p);
			System.out.println(p.getNumFromID(p.getId()));
			System.out.println(p.getIDFromNum(p.getNumFromID(p.getId())));
		}
		
		Collections.sort(player.enemies);
		if(player.enemies.get(player.enemies.size()-1).compareTo(player.player)<0){
			System.out.println("we are the first");
			player.broadcast(player.chord.getID(), player.chord.getID(), true);
		}
		else
			System.out.println("we are NOT the first");

		// cbs.get(0).play();

		// standardbroadcasttest(cbs);
	}

	public static void testLocal() throws Exception {
		gui = new Gui();
		gui.frame.setVisible(true);

		int testAmount = 10;
		int bootPort = 2000;
		List<Game> cbs = new ArrayList<Game>();

		Game cb1 = creatorLocal(bootPort);
		cbs.add(cb1);

		for (int i = 1; i < testAmount; i++) {
			Game cb = gameLocal(2000 + i,bootPort);
			cbs.add(cb);
		}

		cbs.get(0).draw = true;
		Thread.sleep(5000);

		for (int i = 0; i < testAmount; i++) {
			cbs.get(i).printFT();
			cbs.get(i).init();
			System.out.println(cbs.get(i).chord.getURL() + " has ships at: ");
			System.out.println(cbs.get(i).player.getAllShips());
		}

		System.out.println("Players:");
		for (Player p : cbs.get(0).enemies) {
			System.out.println(p);
			System.out.println(p.getNumFromID(p.getId()));
			System.out.println(p.getIDFromNum(p.getNumFromID(p.getId())));
		}

		cbs.get(0).play();

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
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("got hit! " + this.chord.getURL() + " and shot: "
				+ player.gotShot(target));
		System.out
				.println("Area which was hit: " + player.getNumFromID(target));
		boolean shotShip = player.gotShot(target);
		chord.broadcast(target, shotShip);

		if (player.getSunkenShips().size() < Game.S) {
			randomStrat();
		} else {
			System.out.println(player.fieldVis());
			for (int i = 0; i < enemies.size(); i++) {
				System.out.println(enemies.get(i).fieldVis());
			}
		}
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
			if (last) {
				System.out.println("WAS LAST SHIP!!! " + p.toString()
						+ " IS DEAD!!!");

				// System.out.println(player.fieldVis());

				// for(int i=0; i<enemies.size();i++) {
				// System.out.println(enemies.get(i).fieldVis());
				// }

			}
		} else {
			p.shotIntoWater(target);
			// system.out.println("shot into water!");
		}

		if (draw)
			drawField();

	}

	/*
	 * returns the responsible node for a specific id
	 */
	public Player getDaddy(ID id) {
		Player res = null;
		if (player.isMyNode(id))
			res = player;
		for (Player p : enemies) {
			if (p.isMyNode(id)) {
				res = p;
				break;
			}
		}

		return (res);
	}

	public void play() {

		System.out.println("Found " + enemies
				+ " ENEMIES!!!! DIIIIIIEEEEEEE!!!");

		Collections.sort(enemies);
		// if(chord.getID().compareTo(enemies.get(enemies.size()-1).getId()) > 0
		// ){
		randomStrat();
		// }

	}

	public void randomStrat() {

		int ship = (int) (Math.random() * enemies.size());
		int area = (int) (Math.random() * Game.I);

		System.out.println(chord.getURL() + "\n\t shoots at ship: "
				+ enemies.get(ship).toString() + "\n\t and area: " + area);
		chord.retrieve(enemies.get(ship).getIDFromNum(area));
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

	public void drawField() {
		String field = player.fieldVis().concat("\n");
		for (Player p : this.enemies) {
			field = field.concat(p.fieldVis().concat("\n"));
		}
		gui.txtrLog.setText("");
		gui.txtrLog.setText(field);
	}

}
