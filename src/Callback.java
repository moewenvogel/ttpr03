
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.uniba.wiai.lspi.chord.com.Node;
import de.uniba.wiai.lspi.chord.data.ID;
import de.uniba.wiai.lspi.chord.data.URL;
import de.uniba.wiai.lspi.chord.service.NotifyCallback;
import de.uniba.wiai.lspi.chord.service.PropertiesLoader;
import de.uniba.wiai.lspi.chord.service.impl.ChordImpl;
import de.uniba.wiai.lspi.util.logging.Logger;
public class Callback implements NotifyCallback {
	
	public static final int I=100;
	public static final int S=10;
	public static final BigInteger ADDRESS_AMOUNT=new BigInteger(Integer.toString((int)Math.pow(2, 160)));
	ChordImpl chord;
	Logger logger;
	Map<ID, Player> players=new HashMap<ID,Player>();
	
	private Callback() throws Exception {
		PropertiesLoader.loadPropertyFile();
		chord=new ChordImpl();
		chord.setCallback(this);
		
		this.logger = Logger.getLogger(ChordImpl.class.getName()
				+ ".unidentified");
		
		String protocol = URL.KNOWN_PROTOCOLS.get(URL.SOCKET_PROTOCOL ) ;
		
		URL bootstrap=new URL(protocol+"://127.0.0.2:8080/");
		URL url = new URL(protocol+"://127.0.0.1:9081/");
			chord.create(url);
			chord.join(bootstrap);
	}
	

	/*TODO: Players: mapping von ID auf Felder
			Callback: eigenes feld initialisieren und verwalten (retrieve)
	 */
	
	public static void main(String[] args) throws Exception {
		
		Callback cb=new Callback();
		cb.play();
		
		
			
	}
	
	public void close(){
		chord.leave();
	}

	@Override
	public void retrieved(ID target) {
		// TODO Auto-generated method stub
	}

	@Override
	public void broadcast(ID source, ID target, Boolean hit) {
		// TODO Auto-generated method stub		
		logger.info(source+" shot at "+target+" and hit: "+hit);
		boolean knockedOut=false;
		Player respNode=players.get(getDaddy(target));
		if(hit){
			knockedOut=respNode.sunkShip(target);
			if(knockedOut)logger.info(source+" HAS WON!!! "+respNode+" IS DEAD!!!");
		}else{
			players.get(getDaddy(target)).shotIntoWater(target);
			logger.info(source+" has shot into the water of "+respNode);
		}
	}
	

	/*
	 * returns the responsible node for a specific id
	 * */	
	public Player getDaddy(ID id){
		Player res=null;
		for(Map.Entry<ID, Player> entry: players.entrySet()){
			if(id.isInInterval(entry.getKey(), entry.getValue().getLastRespID())){
				res=entry.getValue();
				break;
			}
		}
		
		return(res);
	}
	
	public void play(){
		//calculate the number of players:
		int ftSize=chord.getFingerTable().size();
		int playerAmount=(int) Math.pow(2, 160)/ftSize;
		logger.info("Recognised "+playerAmount+" players");
		
		//initialize the status map
		Map<Node,Node> nodes=chord.getRing();

		for (Map.Entry<Node, Node> entry : nodes.entrySet()) {
			Player p = new Player(entry.getKey(),entry.getValue().getNodeID());
			players.put(p.getId(), p);
		}
		
		
		// initialize own field
		
		
		
		// TODO game loop
		while(true){
			
		}
		
	}
	
	public ID easyStrat(){
		/*Einfache Strategie:
		 * Schiesse auf den Knoten mit den meisten Schiffen, ausser jemand hat 
		 * nur noch ein Schiff --> dann auf den schiessen
		 * */
		
		
		return(null);
	}
	

}
