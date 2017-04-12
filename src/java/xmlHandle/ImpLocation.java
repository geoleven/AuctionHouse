package xmlHandle;

import java.math.BigDecimal;

public class ImpLocation {
	String name = "";
	boolean set = false;
	BigDecimal longitude = new BigDecimal(0);
	BigDecimal latitude = new BigDecimal(0);
	
	public void print() {
		System.out.println("Location Name: " + name);
		if (set) {
			System.out.println("Longtitude: " + longitude.toString());
			System.out.println("Latitude: " + latitude.toString());
		}
	}
}
