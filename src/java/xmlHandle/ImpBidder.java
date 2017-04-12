package xmlHandle;

public class ImpBidder {
	String uname = "";
	int rating = 0;
	ImpLocation location = new ImpLocation();
	String country = "";
	
	public void print() {
		System.out.println("Bidder username: " + uname);
		System.out.println("Bidder rating: " + rating);
		location.print();
		System.out.println("Bidder country: " + country);
	}
}
