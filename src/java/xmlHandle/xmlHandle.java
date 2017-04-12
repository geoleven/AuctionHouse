package xmlHandle;

import java.util.LinkedList;
import java.util.Locale;
import java.text.SimpleDateFormat;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import util.BCrypt;
import util.DataConnect;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import java.io.FilenameFilter;

public class xmlHandle {
	
	public static boolean xmlImportAll(String path) throws Exception {
		boolean res = true;
		File dir = new File(path);
		File[] fl = dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String filename) {
				return filename.endsWith(".xml");
			}
		});
		for (File fd : fl) {
			res = (res && xmlImport(fd.getAbsolutePath()));
		}
		return res;
	}
	
	
	public static boolean xmlImport(String file, Connection dbCon) throws Exception {
		return xmlImport(file.substring(file.lastIndexOf("/")+1), file.substring(0, file.lastIndexOf("/")+1), dbCon);
	}
	
	public static boolean xmlImport(String file) throws Exception {
		Connection dbCon = DataConnect.getConnection();
		boolean res = xmlImport(file.substring(file.lastIndexOf("/")+1), file.substring(0, file.lastIndexOf("/")+1), dbCon);
		DataConnect.close(dbCon);
		return res;
	}
	
	public static boolean xmlImport(String filename, String filepath) throws Exception {
		Connection dbCon = DataConnect.getConnection();
		boolean res = xmlImport(filename, filepath, dbCon);
		DataConnect.close(dbCon);
		return res;
	}

	public static boolean xmlImport(String filename, String filepath, Connection db) throws Exception {
		String namepluspath = filepath.concat(filename);

		namepluspath = namepluspath.trim();
		
		SAXParserFactory parserFactor = SAXParserFactory.newInstance();
		SAXParser parser = parserFactor.newSAXParser();
		SAXHandler handler = new SAXHandler();
		parser.parse(new FileInputStream(namepluspath), handler);

		for (ImpItem itm : handler.itemList) {

			// For debugging purposes
			// itm.print();

			int tuid = 0;
			int tlid = 0;
			String cname = "";
			int selleruid = 0;

			PreparedStatement isUserPresent = db.prepareStatement("SELECT u.uid FROM Users u WHERE u.username = ?");
			
			String salt = BCrypt.gensalt();
			String saltedPass = BCrypt.hashpw("pass", salt);
			PreparedStatement addToUsers = db.prepareStatement(
					"INSERT INTO Users (username, rating, password, firstname, lastname, email, phone, afm, salt) "
							+ "VALUES (?,?,'" + saltedPass + "','firstname','lastname','email@email.com','phone','afm', '" + salt + "')",
					new String[] { "uid" });
			PreparedStatement isLocPresent = db.prepareStatement(
					"SELECT l.idLocation FROM Location l WHERE l.name = ? AND l.longitude = ? AND l.latitude = ? AND l.country = ?");
			PreparedStatement addLocOfUserI = db.prepareStatement(
					"INSERT INTO Location (name, country, longitude, latitude) " + "VALUES (?,?,?,?)",
					new String[] { "idLocation" });
			PreparedStatement addLocOfUserN = db.prepareStatement(
					"INSERT INTO Location (name, country) " + "VALUES (?,?)", new String[] { "idLocation" });
			PreparedStatement isUhLpresent = db.prepareStatement(
					"SELECT UhL.uid FROM User_has_Location UhL WHERE UhL.uid = ? AND UhL.idLocation = ?");
			PreparedStatement addUhLofUser = db
					.prepareStatement("INSERT INTO User_has_Location (uid, idLocation) VALUES (?,?)");
			PreparedStatement isBidPresent = db.prepareStatement(
					"SELECT b.bid_id FROM Bid b WHERE b.item_id = ? AND b.bidder = ? AND b.amount = ? AND b.time = ?");
			PreparedStatement isItemPresent = db.prepareStatement("SELECT i.item_id FROM Items i WHERE i.item_id = ?");
			PreparedStatement addItemToDB = db.prepareStatement(
					"INSERT INTO Items (item_id, name, seller, buy_price, started, ends, description, first_bid) "
							+ "VALUES (?,?,?,?,?,?,?,?)");
			PreparedStatement isIhLpresent = db.prepareStatement(
					"SELECT IhL.item_id FROM Item_has_Location IhL WHERE IhL.item_id = ? AND IhL.idLocation = ?");
			PreparedStatement addIhL = db
					.prepareStatement("INSERT INTO Item_has_Location (item_id, idLocation) VALUES (?,?)");
			PreparedStatement addBid = db
					.prepareStatement("INSERT INTO Bid (item_id, bidder, amount, time) VALUES (?,?,?,?)");
			PreparedStatement isCatPresent = 
					db.prepareStatement("SELECT c.id FROM Item_has_Category c WHERE c.item_id = ? AND c.category_name = ?");
			PreparedStatement addCategory = 
					db.prepareStatement("INSERT INTO Item_has_Category (item_id, category_name) VALUES (?,?)");
			PreparedStatement isUserAccepted = 
					db.prepareStatement("SELECT au.accepted_users_id FROM Accepted_Users au WHERE au.uid = ?");
			PreparedStatement addToAccepted = 
					db.prepareStatement("Insert INTO Accepted_Users (uid) VALUES (?)");

			{
				// create the seller (user)
				cname = itm.sellerID;
				isUserPresent.setString(1, cname);
				ResultSet trs = isUserPresent.executeQuery();
				if (!trs.next()) {
					addToUsers.setString(1, cname);
					addToUsers.setInt(2, itm.sellerRt);
					addToUsers.executeUpdate();
					ResultSet rs = addToUsers.getGeneratedKeys();
					if (rs != null && rs.next()){
						selleruid = rs.getInt(1);
						isUserAccepted.setInt(1, selleruid);
						ResultSet iua = isUserAccepted.executeQuery();
						if(!iua.next()) {
							addToAccepted.setInt(1, selleruid);
							addToAccepted.executeUpdate();
						}
					}
				} else {
					ResultSet rs = isUserPresent.executeQuery();
					if (rs != null && rs.next()) {
						selleruid = rs.getInt("uid");
						isUserAccepted.setInt(1, selleruid);
						ResultSet iua = isUserAccepted.executeQuery();
						if(!iua.next()) {
							addToAccepted.setInt(1, selleruid);
							addToAccepted.executeUpdate();
						}
					}
				}

				String cLocationN = itm.location.name;
				String cCountry = itm.country;
				isLocPresent.setString(1, cLocationN);
				isLocPresent.setString(4, cCountry);
				if (itm.location.set) {
					BigDecimal cLong = itm.location.longitude;
					BigDecimal cLati = itm.location.latitude;
					isLocPresent.setBigDecimal(2, cLong);
					isLocPresent.setBigDecimal(3, cLati);
				} else {
					isLocPresent.setString(2, "NULL");
					isLocPresent.setString(3, "NULL");
				}
				trs = isLocPresent.executeQuery();
				if (!trs.next()) {
					if (itm.location.set) {
						addLocOfUserI.setString(1, cLocationN);
						addLocOfUserI.setString(2, cCountry);
						addLocOfUserI.setBigDecimal(3, itm.location.longitude);
						addLocOfUserI.setBigDecimal(4, itm.location.latitude);
						addLocOfUserI.executeUpdate();
						ResultSet rs = addLocOfUserI.getGeneratedKeys();
						if (rs != null && rs.next())
							tlid = rs.getInt(1);
					} else {
						addLocOfUserN.setString(1, cLocationN);
						addLocOfUserN.setString(2, cCountry);
						addLocOfUserN.executeUpdate();
						ResultSet rs = addLocOfUserN.getGeneratedKeys();
						if (rs != null && rs.next())
							tlid = rs.getInt(1);
					}
				} else {
					tlid = trs.getInt("idLocation");
				}
				isUhLpresent.setInt(1, selleruid);
				isUhLpresent.setInt(2, tlid);
				trs = isUhLpresent.executeQuery();
				if (!trs.next()) {
					addUhLofUser.setInt(1, selleruid);
					addUhLofUser.setInt(2, tlid);
					addUhLofUser.executeUpdate();
				}

			}

			// Add the item
			{
				isItemPresent.setInt(1, itm.itemID);
				ResultSet trs = isItemPresent.executeQuery();
				if (!trs.next()) {
					addItemToDB.setInt(1, itm.itemID);
					addItemToDB.setString(2, itm.name);
					addItemToDB.setInt(3, selleruid);
					addItemToDB.setLong(4, itm.buyPrice);
					addItemToDB.setTimestamp(5, itm.started);
					addItemToDB.setTimestamp(6, itm.ends);
					addItemToDB.setString(7, itm.description);
					addItemToDB.setLong(8, itm.firstBid);
					addItemToDB.executeUpdate();
				}
				isCatPresent.setInt(1, itm.itemID);
				addCategory.setInt(1, itm.itemID);
				for(String cat : itm.categories) {
					isCatPresent.setString(2, cat);
					ResultSet icpRS = isCatPresent.executeQuery();
					if(!icpRS.next()){
						addCategory.setString(2, cat);
						addCategory.executeUpdate();
					}
				}
				isIhLpresent.setInt(1, itm.itemID);
				isIhLpresent.setInt(2, tlid);
				trs = isIhLpresent.executeQuery();
				if (!trs.next()) {
					addIhL.setInt(1, itm.itemID);
					addIhL.setInt(2, tlid);
					addIhL.executeUpdate();
				}
			}

			for (ImpBid ibd : itm.bids) {
				// create the user for every bidder
				cname = ibd.bidder.uname;
				isUserPresent.setString(1, cname);
				ResultSet trs = isUserPresent.executeQuery();
				if (!trs.next()) {
					addToUsers.setString(1, cname);
					addToUsers.setInt(2, ibd.bidder.rating);
					addToUsers.executeUpdate();
					ResultSet rs = addToUsers.getGeneratedKeys();
					if (rs != null && rs.next()) {
						tuid = rs.getInt(1);
						isUserAccepted.setInt(1, tuid);
						ResultSet iua = isUserAccepted.executeQuery();
						if(!iua.next()) {
							addToAccepted.setInt(1, tuid);
							addToAccepted.executeUpdate();
						}
					}
				} else {
					ResultSet rs = isUserPresent.executeQuery();
					if (rs != null && rs.next()) {
						tuid = rs.getInt("uid");
						isUserAccepted.setInt(1, tuid);
						ResultSet iua = isUserAccepted.executeQuery();
						if(!iua.next()) {
							addToAccepted.setInt(1, tuid);
							addToAccepted.executeUpdate();
						}
					}
				}

				String cLocationN = ibd.bidder.location.name;
				String cCountry = ibd.bidder.country;
				isLocPresent.setString(1, cLocationN);
				isLocPresent.setString(4, cCountry);
				if (ibd.bidder.location.set) {
					BigDecimal cLong = ibd.bidder.location.longitude;
					BigDecimal cLati = ibd.bidder.location.latitude;
					isLocPresent.setBigDecimal(2, cLong);
					isLocPresent.setBigDecimal(3, cLati);
				} else {
					isLocPresent.setString(2, "NULL");
					isLocPresent.setString(3, "NULL");
				}
				trs = isLocPresent.executeQuery();
				if (!trs.next()) {
					if (ibd.bidder.location.set) {
						addLocOfUserI.setString(1, cLocationN);
						addLocOfUserI.setString(2, cCountry);
						addLocOfUserI.setBigDecimal(3, ibd.bidder.location.longitude);
						addLocOfUserI.setBigDecimal(4, ibd.bidder.location.latitude);
						addLocOfUserI.executeUpdate();
						ResultSet rs = addLocOfUserI.getGeneratedKeys();
						if (rs != null && rs.next())
							tlid = rs.getInt(1);
					} else {
						addLocOfUserN.setString(1, cLocationN);
						addLocOfUserN.setString(2, cCountry);
						addLocOfUserN.executeUpdate();
						ResultSet rs = addLocOfUserN.getGeneratedKeys();
						if (rs != null && rs.next())
							tlid = rs.getInt(1);
					}
				} else {
					tlid = trs.getInt("idLocation");
				}
				isUhLpresent.setInt(1, tuid);
				isUhLpresent.setInt(2, tlid);
				trs = isUhLpresent.executeQuery();
				if (!trs.next()) {
					addUhLofUser.setInt(1, tuid);
					addUhLofUser.setInt(2, tlid);
					addUhLofUser.executeUpdate();
				}

				isBidPresent.setInt(1, itm.itemID);
				isBidPresent.setInt(2, tuid);
				isBidPresent.setLong(3, ibd.amount);
				isBidPresent.setTimestamp(4, ibd.time);
				trs = isBidPresent.executeQuery();
				if (!trs.next()) {
					addBid.setInt(1, itm.itemID);
					addBid.setInt(2, tuid);
					addBid.setLong(3, ibd.amount);
					addBid.setTimestamp(4, ibd.time);
					addBid.executeUpdate();
				}	
			}
		}

		return true;
	}

	public static boolean xmlExport(String file) throws Exception {
		Connection dbCon = DataConnect.getConnection();
		boolean res = xmlExport(file.substring(file.lastIndexOf("/")+1), file.substring(0, file.lastIndexOf("/")+1), dbCon, 0, 0);
		DataConnect.close(dbCon);
		return res;
	}
	
	public static boolean xmlExport(String filename, String filepath, Connection db) throws Exception {
		return xmlExport(filename, filepath, db, 0, 0);
	}
	
	public static boolean xmlExport(String filename, String filepath, Connection db, int firstItem) throws Exception {
		PreparedStatement getNoOfItems = 
				db.prepareStatement("SELECT count(distinct i.item_id) FROM Items i");
		ResultSet rs = getNoOfItems.executeQuery();
		if(rs.next())
			return xmlExport(filename, filepath, db, firstItem, rs.getInt(1));
		else
			return false;
	}

	public static boolean xmlExport(String filename, String filepath, Connection db, int firstItem, int lastItem) throws Exception {
		
		StringBuilder out =  new StringBuilder("<?xml version=\"1.0\"?>\n<Items>\n");
		boolean allFlag = false;
		if (firstItem == 0 && lastItem == 0)
			allFlag = true;
		if (firstItem>lastItem) {
			System.err.println("No items to export!");
			return false;
		}
		
		PreparedStatement getAllItems = db.prepareStatement("SELECT * FROM Items");
		PreparedStatement getCategories = 
				db.prepareStatement("SELECT IhC.category_name FROM Item_has_Category IhC, Items i WHERE i.item_id = ? AND i.item_id = IhC.item_id");
		PreparedStatement getCurrentBid = 
				db.prepareStatement("SELECT b.amount FROM Bid b, Items i WHERE i.item_id = ? AND i.item_id = b.item_id ORDER BY b.time DESC LIMIT 1");
//		PreparedStatement getFirstBid = 
//				db.prepareStatement("SELECT b.amount FROM Bid b, Items i WHERE i.item_id = ? AND i.item_id = b.item_id ORDER BY amount ASC LIMIT 1");
		PreparedStatement getNoOfBids = 
				db.prepareStatement("SELECT count(distinct b.bid_id) FROM Bid b WHERE b.item_id = ?");
		PreparedStatement getAllBids = 
				db.prepareStatement("SELECT * FROM Bid b WHERE b.item_id = ? ORDER BY b.time ASC");
		PreparedStatement getUserInfo = 
				db.prepareStatement("SELECT u.username, u.rating FROM Users u WHERE u.uid = ?");
		PreparedStatement getUserLoc = 
				db.prepareStatement("SELECT l.name, l.country, l.longitude, l.latitude FROM Location l, User_has_Location UhL "
						+ "WHERE UhL.uid = ? AND UhL.idLocation = l.idLocation");
		PreparedStatement getItemLoc = 
				db.prepareStatement("SELECT l.longitude, l.latitude, l.name, l.country FROM Item_has_Location IhL, Location l "
						+ "WHERE IhL.item_id = ? AND l.idLocation = IhL.idLocation");
		
		
		ResultSet allItems = getAllItems.executeQuery();
		if(!allFlag && firstItem > 0) {
			for(int counter = 0; counter < firstItem; counter++) {
				allItems.next();
			}
		}
		int counter = 0;
		while(allItems.next() && (counter < lastItem-firstItem || allFlag)) {
			
			emit(out, "<Item ItemID=\"");
			emit(out, Integer.toString(allItems.getInt("item_id")));
			emit(out, "\">\n<Name>");
			emit(out, allItems.getString("name"));
			emit(out, "</Name>\n");
			
			getCategories.setInt(1, allItems.getInt("item_id"));
			ResultSet categoriesRS = getCategories.executeQuery();
			while(categoriesRS.next()) {
				emit(out, "<Category>");
				emit(out, categoriesRS.getString("category_name"));
				emit(out, "</Category>\n");
			}
			emit(out, "<Currently>$");	
			getCurrentBid.setInt(1, allItems.getInt("item_id"));
			ResultSet currentAmountRS = getCurrentBid.executeQuery();
			if(currentAmountRS.next() && currentAmountRS.getLong("amount") >= allItems.getLong("first_bid"))
				emit(out, amountToString(currentAmountRS.getLong("amount")));
			else
				emit(out, amountToString(allItems.getLong("first_bid")));
			emit(out, "</Currently>\n");
			
			if(allItems.getLong("buy_price") != 0) {
				emit(out, "<Buy_Price>$");
				emit(out, amountToString(allItems.getLong("buy_price")));
				emit(out, "</Buy_Price>\n");
			}
			
			
			if(allItems.getLong("first_bid") != 0) {
				emit(out, "<First_Bid>$");
				emit(out, amountToString(allItems.getLong("first_bid")));
				emit(out, "</First_Bid>\n");
			}
				
			
			emit(out, "<Number_of_Bids>");
			getNoOfBids.setInt(1, allItems.getInt("item_id"));
			ResultSet noOfBidsRS = getNoOfBids.executeQuery();
			noOfBidsRS.next();
			emit(out, Integer.toString(noOfBidsRS.getInt(1)));
			emit(out, "</Number_of_Bids>\n");
			if(noOfBidsRS.getInt(1) == 0)
				emit(out, "<Bids/>\n");
			else {
				emit(out, "<Bids>\n");
				getAllBids.setInt(1, allItems.getInt("item_id"));
				ResultSet allBidsRS = getAllBids.executeQuery();
				for(int cbid = 0; cbid < noOfBidsRS.getInt(1) && allBidsRS.next(); cbid++) {
					emit(out, "<Bid>\n<Bidder UserID=\"");
					getUserInfo.setInt(1, allBidsRS.getInt("bidder"));
					ResultSet bidderInfoRS = getUserInfo.executeQuery();
					bidderInfoRS.next();
					emit(out, bidderInfoRS.getString("username") + "\" Rating=\"");
					emit(out, Integer.toString(bidderInfoRS.getInt("rating")) + "\">\n");
					getUserLoc.setInt(1, allBidsRS.getInt("bidder"));
					ResultSet userLocRS = getUserLoc.executeQuery();
					if(userLocRS.next()) {
						if(userLocRS.getString("name") != null)
							emit(out, "<Location>" + userLocRS.getString("name") + "</Location>\n");
						if(userLocRS.getString("country") != null)
							emit(out, "<Country>" + userLocRS.getString("country") + "</Country>\n");
					}
					emit(out, "</Bidder>\n<Time>" + timestampToString(allBidsRS.getTimestamp("time")) + "</Time>\n");
					emit(out, "<Amount>$" + amountToString(allBidsRS.getLong("amount")) + "</Amount>\n</Bid>");
				}
				emit(out, "</Bids>\n");
			}
			emit(out, "<Location");
			getItemLoc.setInt(1, allItems.getInt("item_id"));
			ResultSet itemLocRS = getItemLoc.executeQuery();
			if(itemLocRS.next()) {
				if (itemLocRS.getBigDecimal("longitude") != null)
					emit(out,  " Longitude=\"" + itemLocRS.getBigDecimal("longitude").toString() + "\"");
				if(itemLocRS.getBigDecimal("latitude") != null)
					emit(out, " Latitude=\"" + itemLocRS.getBigDecimal("latitude").toString() + "\"");
				emit(out, ">" + itemLocRS.getString("name") + "</Location>\n");
				emit(out, "<Country>" + itemLocRS.getString("country") + "</Country>\n");
			} else {
				emit(out, "> </Location>\n<Country> </Country>\n");
			}
			emit(out, "<Started>" + timestampToString(allItems.getTimestamp("started")) + "</Started>\n");
			emit(out, "<Ends>" + timestampToString(allItems.getTimestamp("ends")) + "</Ends>\n");
			getUserInfo.setInt(1,  allItems.getInt("seller"));
			ResultSet sellerInfoRS = getUserInfo.executeQuery();
			sellerInfoRS.next();
			emit(out, "<Seller UserID=\"" + sellerInfoRS.getString("username") + "\" Rating=\"");
			emit(out, Integer.toString(sellerInfoRS.getInt("rating")) + "\"/>\n");
			emit(out, "<Description>" + allItems.getString("description") + "</Description>\n</Item>");					
			counter++;
		}
		emit(out, "</Items>\n");	
		
		PrintWriter fout = new PrintWriter(new OutputStreamWriter(new FileOutputStream(filepath+filename, false) , StandardCharsets.UTF_8), true);
		fout.println(out.toString().replace("&", "&amp;"));
		fout.close();
		
		return true;
	}

	private static void emit(StringBuilder prev, String addition) {
		prev = prev.append(addition);
	}
	
	private static String amountToString(long amount) {
		//return Long.toString(amount/100) + "." + Long.toString(amount%100);
		String temp = Long.toString(amount%100);
		if (temp.length() < 2)
			temp = temp + "0";
		temp = Long.toString(amount/100) + "." + temp;
		return temp;
	}
	
	private static String timestampToString(Timestamp ts) {
//		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMM-dd-yy HH:mm:ss", Locale.UK);
//		String temp = "";
		return new SimpleDateFormat("MMM-dd-yy HH:mm:ss").format(ts);	
	}


}

/**
 * The Handler for SAX Events.
 */
class SAXHandler extends DefaultHandler {

	LinkedList<ImpItem> itemList = new LinkedList<ImpItem>();
	ImpItem itm = null;
	ImpBid tBid = null;
	String content = null;
	StringBuilder contentSB = new StringBuilder();
	boolean inBidder = false;
	DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMM-dd-yy HH:mm:ss", Locale.UK);

	@Override
	// Triggered when the start of tag is found.
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		contentSB.delete(0, contentSB.length());
		switch (qName) {
		// Create a new ImpItem object when the start tag is found
		case "Item":
			itm = new ImpItem();
			itm.itemID = Integer.parseInt(attributes.getValue("ItemID"));
			break;
		case "Bid":
			tBid = new ImpBid();
			break;
		case "Bidder":
			inBidder = true;
			tBid.bidder.uname = attributes.getValue("UserID");
			tBid.bidder.rating = Integer.parseInt(attributes.getValue("Rating"));
			break;
		case "Location":
			if (inBidder) {
				if (attributes.getValue("Longitude") != null && attributes.getValue("Latitude") != null) {
					tBid.bidder.location.set = true;
					tBid.bidder.location.longitude = BigDecimal
							.valueOf(Double.parseDouble(attributes.getValue("Longitude")));
					tBid.bidder.location.latitude = BigDecimal
							.valueOf(Double.parseDouble(attributes.getValue("Latitude")));
				}
			} else {
				if (attributes.getValue("Longitude") != null && attributes.getValue("Latitude") != null) {
					itm.location.set = true;
					itm.location.longitude = BigDecimal.valueOf(Double.parseDouble(attributes.getValue("Longitude")));
					itm.location.latitude = BigDecimal.valueOf(Double.parseDouble(attributes.getValue("Latitude")));
				}
			}
			break;
		case "Seller":
			itm.sellerID = attributes.getValue("UserID");
			itm.sellerRt = Integer.parseInt(attributes.getValue("Rating"));
			break;
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		content = contentSB.toString();
		switch (qName) {
		// Add the item to list once end tag is found
		case "Item":
			itemList.add(itm);
			break;
		// For all other end tags the item has to be updated.
		case "Name":
			itm.name = content;
			break;
		case "Category":
			itm.categories.add(content);
			break;
		case "Currently":
			itm.currentBid = Integer.parseInt(content.substring(1).replace(".", "").replace(",", ""));
			break;
		case "First_Bid":
			itm.firstBid = Integer.parseInt(content.substring(1).replace(".", "").replace(",", ""));
			break;
		case "Buy_Price":
			itm.buyPrice = Integer.parseInt(content.substring(1).replace(".", "").replace(",", ""));
			break;
		case "Number_of_bids":
			itm.numberOfBids = Integer.parseInt(content);
			break;
		case "Bid":
			if (tBid != null) {
				itm.bids.add(tBid);
				tBid = null;
			}
			break;
		case "Bidder":
			inBidder = false;
			break;
		case "Location":
			if (inBidder) {
				tBid.bidder.location.name = content;
			} else {
				itm.location.name = content;
			}
			break;
		case "Country":
			if (inBidder) {
				tBid.bidder.country = content;
			} else {
				itm.country = content;
			}
			break;
		case "Time":
			tBid.time = Timestamp.valueOf(LocalDateTime.parse(content, dtf));
			break;
		case "Amount":
			tBid.amount = Integer.parseInt(content.substring(1).replace(".", "").replace(",", ""));
			break;
		case "Started":
			itm.started = Timestamp.valueOf(LocalDateTime.parse(content, dtf));
			break;
		case "Ends":
			itm.ends = Timestamp.valueOf(LocalDateTime.parse(content, dtf));
			break;
		case "Description":
			itm.description = content;
			break;

		}

	}

	@Override
	public void characters(char ch[], int start, int length) {
		if (contentSB != null) {
			for (int i = start; i < start + length; i++) {
				contentSB.append(ch[i]);
			}
		}
	}

}