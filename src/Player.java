import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.uniba.wiai.lspi.chord.com.Node;
import de.uniba.wiai.lspi.chord.data.ID;
import de.uniba.wiai.lspi.chord.data.URL;
import de.uniba.wiai.lspi.chord.service.Chord;
import de.uniba.wiai.lspi.chord.service.impl.NodeImpl;


public class Player  implements Comparable<Player>{

	
	private ID id;
	private URL url;
	private ID minRespID;
	private Set<Integer> sunkenShips = new HashSet<Integer>();
	private Set<Integer> emptyWater= new HashSet<Integer>();
	boolean local=false;
	public Map<Integer,Boolean> field=new HashMap<Integer,Boolean>();
	

	public Player(ID node, URL url, ID minRespID){
		
		id=node;
		this.url=url;
		this.minRespID=minRespID;
		System.out.println("Hello, my URL is: "+url+" and my ID: "+id+" my min resp"+ this.minRespID);
	}

	
	public void initializeField(boolean local){
		for(int i=0; i<Game.I; i++){
			field.put(i, false);
		}
		this.local=local;
		
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
		emptyWater.add(getNumFromID(id));
	}
	
	public boolean gotShot(ID id){
		int area=getNumFromID(id);
		boolean hit=field.get(area).booleanValue();
		if(hit){
			sunkenShips.add(area);
		}else{
			emptyWater.add(area);
		}
		return hit;
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
		sunkenShips.add(getNumFromID(id));
		return(sunkenShips.size()==Game.S? true:false);
	}
	
	public int getNumFromID(ID id){
		BigInteger sizeOfAdresspace=NodeImpl.getDistance(this.minRespID,this.id);
		BigInteger sizeToId=NodeImpl.getDistance(this.minRespID,id);
		BigInteger oneFieldSize=sizeOfAdresspace.divide(new BigInteger( Integer.toString(Game.I)));
		
		double d=(sizeToId.divide(oneFieldSize)).doubleValue();

		
		int r =(int)Math.floor(d);
//		System.out.println("getNumFomID: " + r);
//		System.out.println("getIDFromNum: " + getIDFromNum(r));
//		System.out.println("getIDFromNum: " + getIDFromNum(r));
		return (r>=Game.I?r-1:r);
	}
	
	public ID getIDFromNum(int num){
		//System.out.println("minresp: " + this.minRespID);
		//System.out.println("id: " + this.id);
		BigInteger sizeOfAdresspace=NodeImpl.getDistance(this.minRespID,this.id);
		//System.out.println("sizeOfAdresspace: " + sizeOfAdresspace);
		BigInteger oneFieldSize=sizeOfAdresspace.divide(new BigInteger( Integer.toString(Game.I)));
		//System.out.println("oneFieldSize: " + oneFieldSize);

		
		ID fieldID=ID.valueOf(( (oneFieldSize.multiply(new BigInteger(Integer.toString(num))))
														.add(this.minRespID.toBigInteger())
														.add(BigInteger.ONE)
														)
														.mod(Game.ADDRESS_AMOUNT)
				);
		System.out.println("fieldID: " + fieldID);

		return fieldID;
	}
	
	
	/* sorting players by their node id
	 * */
	@Override
	public int compareTo(Player p) {
		return this.id.compareTo(p.id);
		
		//return(Integer.valueOf(this.sunkenShips.size()).compareTo(p.sunkenShips.size()));
	}
	
	


	
	public Set<Integer> getSunkenShips() {
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
	public boolean isMyNode(ID id){
		BigInteger sizeOfAdresspace=NodeImpl.getDistance(this.minRespID,this.id);
		BigInteger distanceToId=NodeImpl.getDistance(this.minRespID,id);

		if(distanceToId.compareTo(sizeOfAdresspace)<=0){
			return true;
		}else {
			return false;
		}
	}
	
	public String fieldVis(){
		String res="";
		for(int i=0; i<field.size();i++){
			boolean ship=field.get(i);
			
			if(sunkenShips.contains(i)){
				res=res.concat("x");
			}		
			else if(local && ship )
				res=res.concat("O");
		
			else if( emptyWater.contains(i)){
				res=res.concat("e");
			}
			else{
				res=res.concat(".");
			}
		}
		return(res);
	}




}
