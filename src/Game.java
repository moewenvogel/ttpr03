
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.spi.LoggingEvent;

import de.uniba.wiai.lspi.chord.com.Node;
import de.uniba.wiai.lspi.chord.console.command.Wait;
import de.uniba.wiai.lspi.chord.data.ID;
import de.uniba.wiai.lspi.chord.data.URL;
import de.uniba.wiai.lspi.chord.service.NotifyCallback;
import de.uniba.wiai.lspi.chord.service.PropertiesLoader;
import de.uniba.wiai.lspi.chord.service.impl.ChordImpl;
import de.uniba.wiai.lspi.util.logging.Logger;
import de.uniba.wiai.lspi.util.logging.Logger.LogLevel;
public class Game implements NotifyCallback {
	
	public static final int I=100;
	public static final int S=10;
	public static final BigInteger ADDRESS_AMOUNT=new BigInteger(Integer.toString((int)Math.pow(2, 160)));
	ChordImpl chord;
	Logger logger;
	Map<ID, Player> players=new HashMap<ID,Player>();
	
	private Game(int localPort, int bootstrapPort, boolean isCreator) throws Exception {
		try {
			PropertiesLoader.loadPropertyFile();
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		chord=new ChordImpl();
		chord.setCallback(this);
		
		this.logger = Logger.getLogger(ChordImpl.class.getName()
				+ ".unidentified");

		
		
		String protocol = URL.KNOWN_PROTOCOLS.get(URL.SOCKET_PROTOCOL ) ;
		URL localUrl = new URL(protocol+"://127.0.0.1:"+localPort+"/");
		URL bootstrapUrl=new URL(protocol+"://127.0.0.1:"+bootstrapPort+"/");
		if(isCreator){
			
			chord.create(bootstrapUrl);

			}
		else{
			chord.join(localUrl,bootstrapUrl);
			
		}
		
		
		System.out.println("I am: "+this.getString());
	}
	 

	/*TODO: Players: mapping von ID auf Felder
			Callback: eigenes feld initialisieren und verwalten (retrieve)
	 */
	
	public static void main(String[] args) throws Exception {
		int testAmount=10;
		int baseport=9090;
		List<Game> cbs=new ArrayList<Game>();
		cbs.add(new Game(baseport,baseport,true));
		for(int i=1;i<testAmount;i++){
			Game cb=new Game(baseport+i,baseport,false);
			cbs.add(cb);
		}
	//	for(int i=1;i<testAmount;i++){
			Thread.sleep(5000);
			cbs.get(1).chord.broadcast(cbs.get(1).chord.getID(), true);
	//	}

		//cb.play();
	//	cbs.get(0).chord.broadcast(cbs.get(0).chord.getID(), true);
	
		/*for(int i=0;i<testAmount;i++){
			cbs.get(i).broadcast(
					cbs.get(0).chord.getID(), 
			*/
		
			
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
		boolean knockedOut=false;
		Player respNode=players.get(getDaddy(target));
	/*	if(hit){
			knockedOut=respNode.sunkShip(target);
			if(knockedOut) System.out.println(source+" HAS WON!!! "+respNode+" IS DEAD!!!");
		}else{
			players.get(getDaddy(target)).shotIntoWater(target);
			System.out.println(source+" has shot into the water of "+respNode);
		}*/
		
		System.out.println(getString()+"received Shot at "+target.toString());
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
		System.out.println("Recognised "+playerAmount+" players");
		
		//initialize the status map
		Map<Node,ID> nodes=chord.getRing();

		for (Map.Entry<Node, ID> entry : nodes.entrySet()) {
			Player p = new Player(entry.getKey(),entry.getValue());
			players.put(p.getId(), p);
		}
		
		
		
		
		// initialize own field
		
		
		
		// TODO game loop

		
	}
	
	public ID easyStrat(){
		/*Einfache Strategie:
		 * Schiesse auf den Knoten mit den meisten Schiffen, ausser jemand hat 
		 * nur noch ein Schiff --> dann auf den schiessen
		 * */
		
		
		return(null);
	}
	
	public String getString(){
		return(chord.getURL().toString());
	}




}
