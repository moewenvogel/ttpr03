import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.uniba.wiai.lspi.chord.com.Node;
import de.uniba.wiai.lspi.chord.data.ID;
import de.uniba.wiai.lspi.chord.data.URL;
import de.uniba.wiai.lspi.chord.service.Chord;
import de.uniba.wiai.lspi.chord.service.impl.NodeImpl;


public class Player  implements Comparable<Player>{

	
	private ID id;
	private URL url;
	private ID minRespID;
	private List<ID> sunkenShips = new ArrayList<ID>();
	private Map<Integer,Boolean> field=new HashMap<Integer,Boolean>();

	public Player(ID node, URL url, ID maxRespID){
		
		id=node;
		this.url=url;
		this.minRespID=maxRespID;
		System.out.println("Hello, my URL is: "+url+" and my ID: "+id);
	}

	
	public void initializeField(boolean local){
		for(int i=0; i<Game.I; i++){
			field.put(i, false);
		}
		
		//fill field only with ships if it is my node
		if(local){
			
			for(int i=0; i<Game.S; i++){
				int random=(int)(Math.random()*Game.I);
				while(field.get(random).booleanValue()==true){
					random=(int)(Math.random()*Game.I);
				}
				field.put(random, true);
			}
		}
	}
	
	public void shotIntoWater(ID id){
		field.put(getNumFomID(id), false);
	}
	
	public boolean gotShot(ID id){
		int area=getNumFomID(id);
		return field.get(area).booleanValue();
	}
	
	public List<Integer> getAllShips(){
		List<Integer> ships=new ArrayList<Integer>();
		for(Map.Entry<Integer,Boolean> entry:field.entrySet()){
			if(entry.getValue()){
				ships.add(entry.getKey());
			}
		}
		return ships;
	}
	
	public List<Integer> getAllWater(){
		List<Integer> ships=new ArrayList<Integer>();
		for(Map.Entry<Integer,Boolean> entry:field.entrySet()){
			if(!entry.getValue()){
				ships.add(entry.getKey());
			}
		}
		return ships;
	}
	//returns true if all ships have died
	public boolean sunkShip(ID id){
		sunkenShips.add(id);
		return(sunkenShips.size()==Game.S? true:false);
	}
	
	public int getNumFomID(ID id){
		BigInteger sizeOfAdresspace=NodeImpl.getDistance(this.minRespID,this.id);
		BigInteger sizeToId=NodeImpl.getDistance(id,this.id);
		BigInteger oneFieldSize=sizeOfAdresspace.divide(new BigInteger( Integer.toString(Game.I)));
		
		double d=(sizeToId.divide(oneFieldSize)).doubleValue();

		int r =(int)Math.floor(d);
		return r;
	}
	
	public ID getIDFromNum(int num){
		BigInteger sizeOfAdresspace=NodeImpl.getDistance(this.minRespID,this.id);
		BigInteger oneFieldSize=sizeOfAdresspace.divide(new BigInteger( Integer.toString(Game.I)));
		
		ID fieldID=ID.valueOf(( (oneFieldSize.multiply(new BigInteger(Integer.toString(num))))
														.add(this.minRespID.toBigInteger())
														.add(BigInteger.ONE)
														)
														.mod(Game.ADDRESS_AMOUNT)
				);
		return fieldID;
	}
	
	
	/* sorting players by their node id
	 * */
	@Override
	public int compareTo(Player p) {
		return this.id.compareTo(p.id);
		
		//return(Integer.valueOf(this.sunkenShips.size()).compareTo(p.sunkenShips.size()));
	}
	
	

	
	public Map<Integer,Boolean> getField(){
		return field;
	}

	
	public List<ID> getSunkenShips() {
		return sunkenShips;
	}


	public ID getId() {
		return id;
	}
	
	public ID getLastRespID() {
		return minRespID;
	}
	
	@Override 
	public String toString(){
		return this.url.toString()+"["+this.id+"]";
	}
	
	public String getStatusString(){
		String r=this.toString();
		r=r+"\n\t[alive: "+(Game.S-this.sunkenShips.size())+"]\n\t[unknown water left: "+(Game.I-this.sunkenShips.size()-this.getAllWater().size());
		
		return r;
	}




}
