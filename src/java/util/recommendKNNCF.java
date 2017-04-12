package util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedList;

import util.DataConnect;


public class recommendKNNCF {
	
	private static final boolean debugging = true;
	private static final int NUMOFNEIGHBS = 30;
	// private static final int NUMOFRECPUSR = 5;
	
	public static LinkedList<Integer> getRecommendations(int uid, int numOfRecs) throws Exception {
		Connection dbCon = DataConnect.getConnection();
		LinkedList<Integer> res = getRecommendations(uid, numOfRecs, dbCon);
		DataConnect.close(dbCon);
		return res;
	}
	
	public static LinkedList<Integer> getRecommendations(int uid, int numOfRecs, Connection db) throws Exception {
		
		LinkedList<Integer> results = null;
		
		PreparedStatement catsU1L =
				db.prepareStatement(
						"SELECT "
						+ "		DISTINCT(c.category_name) "
						+ "FROM "
						+ "		Item_has_Category c, Bid b "
						+ "WHERE "
						+ "		b.bidder = ? "
						+ "		AND b.item_id = c.item_id");
		catsU1L.setInt(1,  uid);
		ResultSet User1Cats = catsU1L.executeQuery();

		// cflag indicates that User1 has some categories of interest
		boolean cflag = User1Cats.next();
		
		PreparedStatement neighbours = 
				db.prepareStatement(
		"SELECT "
		+ "    b.bidder, COUNT(DISTINCT (b.item_id)) AS cdi "
		+ "FROM "
		+ "    Bid b, "
		+ "   (SELECT "
		+ "        b1.item_id, b1.bidder "
		+ "    FROM "
		+ "        Bid b1 "
		+ "    WHERE "
		+ "        b1.bidder = ?) AS fub "
		+ "WHERE "
		+ "    b.bidder != fub.bidder "
		+ "        AND b.item_id = fub.item_id "
		+ "GROUP BY b.bidder "
		+ "ORDER BY cdi DESC "
		+ "LIMIT ?");
		
		PreparedStatement nbrBidsSameCat = 
				db.prepareStatement(
						"SELECT DISTINCT " + 
						"    (c.item_id), COUNT(DISTINCT (fuc.category_name)) AS cdc " + 
						"FROM " + 
						"    Items i, " + 
						"    Bid b, " + 
						"    Item_has_Category c, " + 
						"    (SELECT DISTINCT " + 
						"        (c1.category_name), b1.bidder " + 
						"    FROM " + 
						"        Item_has_Category c1, Bid b1 " + 
						"    WHERE " + 
						"        b1.bidder = ? " + 
						"            AND b1.item_id = c1.item_id) AS fuc, " + 
						"    (SELECT DISTINCT " + 
						"        (b2.item_id) " + 
						"    FROM " + 
						"        Bid b2 " + 
						"    WHERE " + 
						"        b2.item_id NOT IN (SELECT " + 
						"                b3.item_id " + 
						"            FROM " + 
						"                Bid b3 " + 
						"            WHERE " + 
						"                b3.bidder = ?)) AS nbi " + 
						"WHERE " + 
						"    b.bidder = ? " +
                                                "   AND fuc.bidder != i.seller " +
                                                "   AND i.item_id = b.item_id " +
						"        AND c.category_name = fuc.category_name " + 
						"        AND b.item_id = c.item_id " + 
						"        AND nbi.item_id = b.item_id " + 
						"GROUP BY c.item_id " +
						"HAVING cdc > 0 " +
						"ORDER BY cdc DESC");
		
		
		// Retrieve as much neighbors as you can < to limit
		int totalWeight = 0;
		neighbours.setInt(1, uid);
		neighbours.setInt(2, NUMOFNEIGHBS);
		ResultSet knnRS = neighbours.executeQuery();
		LinkedList<ComBidsWithUser> knnIDs = null;
		if (knnRS.next()) {
			knnIDs = new LinkedList<ComBidsWithUser>();
			do {
				if (debugging)
					System.err.println("I have neighbor: " + knnRS.getInt("bidder") + " with cdi: " + knnRS.getInt("cdi"));
				knnIDs.addLast(new ComBidsWithUser(knnRS.getInt("bidder"), knnRS.getInt("cdi")));
				totalWeight = totalWeight + knnRS.getInt("cdi");
			} while (knnRS.next());
		} else {
			return results;
		}
		
		LinkedList<Integer> trid = new LinkedList<Integer>();
		LinkedList<Double> triw = new LinkedList<Double>();
		
		/* For every neighbor find his bid with categories of interest and add them to the lists (trid = item_id, triw = recommendation weight) */
		for (ComBidsWithUser nbr : knnIDs) {
			double myWeight = (double)(nbr.cbs) / (double) (totalWeight) ;
			if (cflag) {
				nbrBidsSameCat.setInt(1, uid);
				nbrBidsSameCat.setInt(2, uid);
				nbrBidsSameCat.setInt(3, nbr.uid);
				ResultSet isc = nbrBidsSameCat.executeQuery();
				int twh = 0;
				while (isc.next()) {
					twh = twh + isc.getInt("cdc");
				}
				isc.beforeFirst();
				while (isc.next()) {
					if (debugging)
						System.err.println("From neighbor: " + nbr.uid + " with nWeight: " + myWeight + " and itemID: " + isc.getInt("item_id") + " with itemW: " + isc.getInt("cdc") + " and twh: " + twh);
					trid.add(isc.getInt("item_id"));
					// triw.add(((double) (isc.getInt("cdc")) / (double) twh) * myWeight);
					triw.add((double) (isc.getInt("cdc")) * myWeight);
				}	
			}
		}
		
		LinkedList<Integer> tresid = new LinkedList<Integer>();
		LinkedList<Double> tresiw = new LinkedList<Double>();
		
		// Sum the weights of multiple recommendations for a single item and order them descending by weight
		if(cflag && !trid.isEmpty()) {
			while(!trid.isEmpty()) {
				if (tresid.contains(trid.peek())){
					int cInd = tresid.indexOf(trid.pop());
					tresiw.set(cInd, tresiw.get(cInd) + triw.pop());
				} else {
					tresid.add(trid.pop());
					tresiw.add(triw.pop());
				}
			}	
			if (debugging) {
				for(int i = 0; i < tresid.size(); i++){
					System.err.println("ItemID: " + tresid.get(i) + " with weight: " + tresiw.get(i));
				}
			}
			results = getTopNids(numOfRecs, tresid, tresiw);
		}
		return results;
	}

	private static int partitionParallel(LinkedList<Double> arr, LinkedList<Integer> arr2, int left, int right) {
		int i = left, j = right;
		double tmp1;
		int tmp2;
		double pivot = arr.get((left + right) / 2);
		while (i <= j) {
			while (arr.get(i) < pivot)
				i++;
			while (arr.get(j) > pivot)
				j--;
			if (i <= j) {
				tmp1 = arr.get(i);
				tmp2 = arr2.get(i);
				arr.set(i, arr.get(j));
				arr2.set(i, arr2.get(j));
				arr.set(j, tmp1);
				arr2.set(j, tmp2);
				i++;
				j--;
			}
		}
		return i;
	}

	private static void quickSortParallel(LinkedList<Double> arr, LinkedList<Integer> arr2, int left, int right) {
		int index = partitionParallel(arr, arr2, left, right);
		if (left < index - 1)
			quickSortParallel(arr, arr2, left, index - 1);
		if (index < right)
			quickSortParallel(arr, arr2, index, right);
	}
	
	private static LinkedList<Integer> getTopNids(int n, LinkedList<Integer> lli, LinkedList<Double> lld) {
		LinkedList<Integer> r = new LinkedList<Integer>();
		quickSortParallel(lld, lli, 0, lld.size()-1);
		for(int c1 = 0; c1 < n && lli.size() > 0; c1++) {
			if (debugging)
				System.err.println(lli.getLast());
			r.add(lli.removeLast());
		}
		return r;
	}
	
}

class ComBidsWithUser {
	int uid;
	int cbs;
	
	public ComBidsWithUser() {
		uid = 0;
		cbs = 0;
	}
	
	public ComBidsWithUser(int cuid, int ccbs) {
		uid = cuid;
		cbs = ccbs;
	}
}
