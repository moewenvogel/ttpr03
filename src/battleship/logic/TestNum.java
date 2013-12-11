package battleship.logic;
import java.math.BigInteger;

import de.uniba.wiai.lspi.chord.data.ID;
import de.uniba.wiai.lspi.chord.service.impl.NodeImpl;


public class TestNum {

	public static void Test(){
		ID min = ID.valueOf(new BigInteger("90"));
		ID max = ID.valueOf(new BigInteger("20"));

		for (int i = 90; i <= 120; i++) {
			System.out.println("testing: " + i);
			ID target = ID.valueOf(new BigInteger(Integer.toString(i%100)));
			int temp = getNumFromID(min, max, target, 10);
			System.out.println(" get num from id: " + temp);
			System.out.println("get id from num "
					+ getIDFromNum(min, max, temp, 10).toDecimalString());
		}
	}
	
	
	public static int getNumFromID(ID min, ID max, ID target, int iv) {
		BigInteger sizeOfAdresspace = NodeImpl.getDistance(min, max,  new BigInteger("100"));
		BigInteger sizeToId = NodeImpl.getDistance(min, target,  new BigInteger("100"));
		BigInteger oneFieldSize = sizeOfAdresspace.divide(new BigInteger(
				Integer.toString(iv)));

		double d = (sizeToId.divide(oneFieldSize)).doubleValue();

		int r = (int) Math.floor(d);
		// System.out.println("getNumFomID: " + r);
		// System.out.println("getIDFromNum: " + getIDFromNum(r));
		// System.out.println("getIDFromNum: " + getIDFromNum(r));
		return (r >= iv ? r - 1 : r);
	}

	public static ID getIDFromNum(ID min, ID max, int num, int iv) {
		BigInteger sizeOfAdresspace = NodeImpl.getDistance(min, max, new BigInteger("100"));
		System.out.println("size of adress space "+sizeOfAdresspace);
		BigInteger oneFieldSize = sizeOfAdresspace.divide(new BigInteger(
				Integer.toString(iv)));

		ID fieldID = ID.valueOf(((oneFieldSize.multiply(new BigInteger(Integer
				.toString(num)))).add(min.toBigInteger()).add(BigInteger.ONE))
				.mod(new BigInteger("100")));
		return fieldID;
	}
}
