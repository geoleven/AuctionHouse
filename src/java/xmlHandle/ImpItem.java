package xmlHandle;

import java.sql.Timestamp;
import java.util.LinkedList;

public class ImpItem {
	int itemID;
	String name = "";
	LinkedList<String> categories = new LinkedList<String>();
	long currentBid = 0;
	long buyPrice = 0;
	long firstBid = 0;
	long numberOfBids = 0;
	LinkedList<ImpBid> bids = new LinkedList<ImpBid>();
	ImpLocation location = new ImpLocation();
	String country = "";
	Timestamp started;
	Timestamp ends;
	String sellerID = "";
	int sellerRt = 0;
	String description = "";
	
	public void print() {
		System.out.println("ItemID: " + itemID);
		System.out.println("Name: " + name);
		for(String cat : categories)
			System.out.println("Category: " + cat);
		System.out.println("Current Bid: " + currentBid);
		System.out.println("Buy Price: " + buyPrice);
		System.out.println("First Bid: " + firstBid);
		System.out.println("Number of Bids: " + numberOfBids);
		for(ImpBid b : bids)
			b.print();
		location.print();
		System.out.println("Country: " + country);
		System.out.println("Started at: " + started);
		System.out.println("Ends at: " + ends);
		System.out.println("SellerID: " + sellerID);
		System.out.println("Seller Rating: " + sellerRt);
		System.out.println("Description: " + description);
	}
}

