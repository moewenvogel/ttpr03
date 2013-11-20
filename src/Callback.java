import java.net.MalformedURLException;

import de.uniba.wiai.lspi.chord.com.Broadcast;
import de.uniba.wiai.lspi.chord.com.Node;
import de.uniba.wiai.lspi.chord.data.ID;
import de.uniba.wiai.lspi.chord.data.URL;
import de.uniba.wiai.lspi.chord.service.Chord;
import de.uniba.wiai.lspi.chord.service.NotifyCallback;
import de.uniba.wiai.lspi.chord.service.PropertiesLoader;
import de.uniba.wiai.lspi.chord.service.ServiceException;
import de.uniba.wiai.lspi.chord.service.impl.ChordImpl;
public class Callback implements NotifyCallback {
	
	ChordImpl chord;
	
	
	private Callback() throws Exception {
		PropertiesLoader.loadPropertyFile();
		chord=new ChordImpl();
		chord.setCallback(this);
		
		String protocol = URL.KNOWN_PROTOCOLS.get(URL.SOCKET_PROTOCOL ) ;
		
		URL bootstrap=new URL(protocol+"://127.0.0.2:8080/");
		URL url = new URL(protocol+"://127.0.0.1:9081/");
			chord.create(url);
			chord.join(bootstrap);
	}
	
	/**
	 * @param args
	 * 
	 * MAIN THINGS TODO:
	 * 1) adding broadcast code to chordImpl
	 * 2) adding broadcast code to NodeImpl
	 * 3) NotifyCallback and Retrieve in this class
	 * 4) GAME!!!
	 * @throws Exception 
	 * 
	 */
	public static void main(String[] args) throws Exception {
		
		Callback cb=new Callback();
			
		//TODO: GAME!!!!
		
			
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
		System.out.println(source+" shot at "+target+" and hit: "+hit);
		
	}
	

}
