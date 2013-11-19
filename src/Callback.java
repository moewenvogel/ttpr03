import java.net.MalformedURLException;

import de.uniba.wiai.lspi.chord.data.ID;
import de.uniba.wiai.lspi.chord.data.URL;
import de.uniba.wiai.lspi.chord.service.Chord;
import de.uniba.wiai.lspi.chord.service.NotifyCallback;
import de.uniba.wiai.lspi.chord.service.ServiceException;
import de.uniba.wiai.lspi.chord.service.impl.ChordImpl;
public class Callback implements NotifyCallback {

	
	
	/**
	 * @param args
	 * 
	 * MAIN THINGS TODO:
	 * 1) adding broadcast code to chordImpl
	 * 2) adding broadcast code to NodeImpl
	 * 3) NotifyCallback and Retrieve in this class
	 * 4) GAME!!!
	 * 
	 */
	public static void main(String[] args) {
		Callback cb=new Callback();
		Chord chord=new ChordImpl();
		URL url=null;
		URL bootstrap=null;
		try {
			url = new URL("localhost:8181");
			bootstrap=new URL("google.com");
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		
		try {
			chord.create(url);
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		try {
			chord.join(bootstrap);
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		//TODO: GAME!!!!
		
		
		
		try {
			chord.leave();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}

	@Override
	public void retrieved(ID target) {
		// TODO Auto-generated method stub
	}

	@Override
	public void broadcast(ID source, ID target, Boolean hit) {
		// TODO Auto-generated method stub		
	}

}
