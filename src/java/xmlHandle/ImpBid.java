package xmlHandle;

import java.sql.Timestamp;

public class ImpBid {
	ImpBidder bidder = new ImpBidder();
	Timestamp time;
	long amount = 0;
	
	public void print() {
		bidder.print();
		System.out.println("Time of bid: " + time);
		System.out.println("Amount: " + amount);
	}
}
