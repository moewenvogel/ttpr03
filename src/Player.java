import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.uniba.wiai.lspi.chord.com.Node;
import de.uniba.wiai.lspi.chord.data.ID;
import de.uniba.wiai.lspi.chord.data.URL;
import de.uniba.wiai.lspi.chord.service.impl.NodeImpl;


public class Player  implements Comparable<Player>{

	private Node node;
	private ID id;
	private URL url;
	private ID maxRespID;
	private List<ID> sunkenShips;
	private Map<Integer,Boolean> field=new HashMap<Integer,Boolean>();
	
	public Player(Node node, ID maxRespID){
		this.node=node;
		id=node.getNodeID();
		url=node.getNodeURL();
		this.maxRespID=ID.valueOf(
					(maxRespID.toBigInteger()
					.add(Callback.ADDRESS_AMOUNT)
					.subtract(new BigInteger("1"))
				.mod(Callback.ADDRESS_AMOUNT))
				);

		
	}

	
	public void initializeField(boolean local){
		for(int i=0; i<Callback.I; i++){
			field.put(i, false);
		}
		
		//fill field only with ships if it is my node
		if(local){
			
			for(int i=0; i<Callback.S; i++){
				int random=(int)Math.random()*Callback.I;
				while(field.get(random)==true){
					random=(int)Math.random()*Callback.I;
				}
				field.put(random, true);
			}
		}
	}
	
	public void shotIntoWater(ID id){
		field.put(getNumOfField(id), false);
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
		return(sunkenShips.size()==Callback.S? true:false);
	}
	
	public int getNumOfField(ID id){
		BigInteger sizeOfAdresspace=NodeImpl.getDistance(this.id,this.maxRespID);
		BigInteger oneFieldSize=sizeOfAdresspace.divide(new BigInteger( Integer.toString(Callback.I)));
		int r=Integer.parseInt(id.toBigInteger().divide(oneFieldSize).toString());
		return r;
	}
	
	
	/* sorting players in order of their amount of sunken ships
	 * */
	@Override
	public int compareTo(Player p) {
		return(Integer.compare(this.sunkenShips.size(), p.sunkenShips.size()));
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
		return maxRespID;
	}
	
	@Override 
	public String toString(){
		return this.url.toString()+"["+this.id+"]";
	}
	
	public String getStatusString(){
		String r=this.toString();
		r=r+"\n\t[alive: "+(Callback.S-this.sunkenShips.size())+"]\n\t[unknown water left: "+(Callback.I-this.sunkenShips.size()-this.getAllWater().size());
		
		return r;
	}




}
