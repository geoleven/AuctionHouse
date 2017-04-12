/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataAccessObjects;

import dbClass.Item;
import dbClass.Location;
import dbClass.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import util.DataConnect;

/**
 *
 * @author george
 */
public class ItemDAO {
    
    //Queries
    public static String addItem = "Insert into Items (name , seller , first_bid , ends , description) VALUES ( ? , ? , ? , ? , ?)" ;
    
    public static String addItemWithBuyPrice = "Insert into Items (name , seller , first_bid , buy_price , ends , description) VALUES ( ? , ? , ? , ? , ? , ?)" ;
    
    public static String addItemWithStartDate = "Insert into Items (name , seller , first_bid , started , ends , description , status) VALUES ( ? , ? , ? , ? , ? , ? , ?)" ;
    
    public static String addItemWithStartDateWithBuyPrice = "Insert into Items (name , seller , first_bid , buy_price , started , ends , description , status) VALUES ( ? , ? , ? , ? , ? , ? , ? , ?)" ;
    
    public static String getAllActiveItems = "Select * from Items where status = \"active\" ";
    
    public static String getAllItems = "Select * from Items ";
    
    public static String getItemById = "Select * from Items where item_id = ? ";
    
    public static String getItemByName = "Select * from Items where name = ?";
    
    public static String addCategory = "Insert into Item_has_Category (category_name , item_id) VALUES (? , ?)";
    
    public static String getCategoriesByItem = "Select * from Item_has_Category where item_id = ?";
    
    public static String getItemsbyCategory = "Select * from Item_has_Category where category_name = ?";
    
    
    public static int createItem(String name , int sellerId , int firstBid , Timestamp ends , String description){
        Connection con = null;
        PreparedStatement ps = null;
        
        try {
            con = DataConnect.getConnection();

            ps = con.prepareStatement(addItem, new String[]{"item_id"});
                ps.setString(1, name);
                ps.setInt(2, sellerId);
                ps.setInt(3, firstBid);
                ps.setTimestamp(4, ends);
                ps.setString(5, description);
            
            ps.executeUpdate();
            
            ResultSet rs = ps.getGeneratedKeys();
            if (rs != null && rs.next()) {
                return rs.getInt(1);
            }
               
        } catch (SQLException ex) {
            System.out.println("Create Item error -->" + ex.getMessage());       
        } finally {
            DataConnect.close(con);
        }
        
        return -1;
    }
    
    public static int createItem(String name , int sellerId , int firstBid , int buyPrice , Timestamp ends , String description){
        Connection con = null;
        PreparedStatement ps = null;
        
        try {
            con = DataConnect.getConnection();

            ps = con.prepareStatement(addItemWithBuyPrice, new String[]{"item_id"});
                ps.setString(1, name);
                ps.setInt(2, sellerId);
                ps.setInt(3, firstBid);
                ps.setInt(4, buyPrice);
                ps.setTimestamp(5, ends);
                ps.setString(6, description);
            
            ps.executeUpdate();
            
            ResultSet rs = ps.getGeneratedKeys();
            if (rs != null && rs.next()) {
                return rs.getInt(1);
            }
               
        } catch (SQLException ex) {
            System.out.println("Create Item error -->" + ex.getMessage());       
        } finally {
            DataConnect.close(con);
        }
        
        return -1;
    }
    
    public static int createItem(String name , int sellerId , int firstBid , Timestamp starts , Timestamp ends , String description){
        Connection con = null;
        PreparedStatement ps = null;
        
        try {
            con = DataConnect.getConnection();

            ps = con.prepareStatement(addItemWithStartDate, new String[]{"item_id"});
                ps.setString(1, name);
                ps.setInt(2, sellerId);
                ps.setInt(3, firstBid);
                ps.setTimestamp(4, starts);
                ps.setTimestamp(5, ends);
                ps.setString(6, description);
                ps.setString(7, "waiting");
            
            ps.executeUpdate();
            
            updateWaiting();
            
            ResultSet rs = ps.getGeneratedKeys();
            if (rs != null && rs.next()) {
                return rs.getInt(1);
            }
               
        } catch (SQLException ex) {
            System.out.println("Create Item error -->" + ex.getMessage());       
        } finally {
            DataConnect.close(con);
        }
        
        return -1;
    }
    
    public static int createItem(String name , int sellerId , int firstBid , int buyPrice , Timestamp starts , Timestamp ends , String description){
        Connection con = null;
        PreparedStatement ps = null;
        
        try {
            con = DataConnect.getConnection();

            ps = con.prepareStatement(addItemWithStartDateWithBuyPrice, new String[]{"item_id"});
                ps.setString(1, name);
                ps.setInt(2, sellerId);
                ps.setInt(3, firstBid);
                ps.setInt(4, buyPrice);
                ps.setTimestamp(5, starts);
                ps.setTimestamp(6, ends);
                ps.setString(7, description);
                ps.setString(8, "waiting");
            
            ps.executeUpdate();
            
            updateWaiting();
            
            ResultSet rs = ps.getGeneratedKeys();
            if (rs != null && rs.next()) {
                return rs.getInt(1);
            }
               
        } catch (SQLException ex) {
            System.out.println("Create Item error -->" + ex.getMessage());       
        } finally {
            DataConnect.close(con);
        }
        
        return -1;
    }
    
    public static ArrayList<Item> getAllItems( int nResults , int start ){
        Connection con = null;
        PreparedStatement ps = null;
        
        ArrayList<Item> items = new ArrayList<Item>(); 
        
        try {
            con = DataConnect.getConnection();

            ps = con.prepareStatement("Select * from Items Limit ? Offset ?");
                ps.setInt(1, nResults);
                ps.setInt(2, start);
            
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                
                Item it = new Item();
                
                it.setItemId(rs.getInt("item_id"));
                it.setName(rs.getString("name"));
                it.setFirstBid((float) rs.getInt("first_bid") / 100);
                it.setBuyPrice((float) rs.getInt("buy_price") / 100);
                it.setDescription(rs.getString("description"));
                it.setSeller(UserDAO.getUser(rs.getInt("seller")));
                
                items.add(it);
            }
            
            return items;
               
        } catch (SQLException ex) {
            System.out.println("Get all items error -->" + ex.getMessage());       
        } finally {
            DataConnect.close(con);
        }
        return null;
    }
    
    public static ArrayList<Item> searchItems(String search , int nResults , int start ){
        Connection con = null;
        PreparedStatement ps = null;
        
        ArrayList<Item> items = new ArrayList<Item>(); 
        
        try {
            
            System.out.println("In search items");
            
            con = DataConnect.getConnection();

            ps = con.prepareStatement("Select * from Items  , Item_has_Category where Items.item_id = Item_has_Category.item_id and (Items.name like ? or Item_has_Category.category_name like ?)  Limit ? Offset ?");
                ps.setString(1, search);
                ps.setString(2, search);
                ps.setInt(3, nResults);
                ps.setInt(4, start);
            
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                
                System.out.println("Result");
                
                Item it = new Item();
                
                it.setItemId(rs.getInt("Items.item_id"));
                it.setName(rs.getString("Items.name"));
                it.setFirstBid(rs.getInt("Items.first_bid")  / 100 );
                it.setDescription(rs.getString("Items.description"));
                it.setSeller(UserDAO.getUser(rs.getInt("Items.seller")));
                
                boolean allreadyIn = false;
                for(Item i : items){
                    if(i.getItemId() == it.getItemId()){
                        allreadyIn = true;
                        break;
                    }
                }
                
                if(allreadyIn == false)
                    items.add(it);
            }
            
            return items;
               
        } catch (SQLException ex) {
            System.out.println("Get all items error -->" + ex.getMessage());       
        } finally {
            DataConnect.close(con);
        }
        return null;
    }
    
    public static Item getItem( int id ){
        Connection con = null;
        PreparedStatement ps = null;
        
        try {
            con = DataConnect.getConnection();

            ps = con.prepareStatement(getItemById);
            
            ps.setInt(1, id);
            
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                
                Item it = new Item();
                
                it.setItemId(rs.getInt("item_id"));
                it.setName(rs.getString("name"));
                it.setFirstBid((float) rs.getInt("first_bid")  / 100 );
                it.setBuyPrice((float) rs.getInt("buy_price") / 100);
                it.setDescription(rs.getString("description"));
                it.setSeller(UserDAO.getUser(rs.getInt("seller")));
                it.setEnds(rs.getDate("ends"));
                
                return it;
            }     
               
        } catch (SQLException ex) {
            System.out.println("Get Item error -->" + ex.getMessage());       
        } finally {
            DataConnect.close(con);
        }
        return null;
    }
    
    public static Item getItem( String name ){
        Connection con = null;
        PreparedStatement ps = null;
        
        try {
            con = DataConnect.getConnection();

            ps = con.prepareStatement(getItemByName);
            
            ps.setString(1, name);
            
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                
                Item it = new Item();
                
                it.setItemId(rs.getInt("item_id"));
                it.setName(rs.getString("name"));
                it.setFirstBid((float) rs.getInt("first_bid")  / 100);
                it.setBuyPrice((float) rs.getInt("buy_price"));
                it.setDescription(rs.getString("description"));
                it.setSeller(UserDAO.getUser(rs.getInt("seller")));
                it.setEnds(rs.getDate("ends"));
                
                return it;
            }     
               
        } catch (SQLException ex) {
            System.out.println("Get Item error -->" + ex.getMessage());       
        } finally {
            DataConnect.close(con);
        }
        return null;
    }
    
    public static void addCategory(String name , int id){
        Connection con = null;
        PreparedStatement ps = null;
        
        try {
            con = DataConnect.getConnection();

            ps = con.prepareStatement(addCategory);
                ps.setString(1, name);
                ps.setInt(2, id);             
            
            ps.executeUpdate();
               
        } catch (SQLException ex) {
            System.out.println("Create Category error -->" + ex.getMessage());       
        } finally {
            DataConnect.close(con);
        }
        
    }
    
    public static ArrayList<String> getItemCategories( int id ){
        
        Connection con = null;
        PreparedStatement ps = null;
        
        ArrayList<String> categories = new ArrayList<String>(); 
        
        try {
            con = DataConnect.getConnection();

            ps = con.prepareStatement(getCategoriesByItem);
                ps.setInt(1 , id);
            
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                String category = rs.getString("category_name");
                
                categories.add(category);
            }
            
            return categories;
               
        } catch (SQLException ex) {
            System.out.println("Get all items error -->" + ex.getMessage());       
        } finally {
            DataConnect.close(con);
        }
        return null;
    }
    
    public static ArrayList<Integer> getItemsByCategory( int item ){
        
        Connection con = null;
        PreparedStatement ps = null;
        
        ArrayList<Integer> items = new ArrayList<Integer>(); 
        
        try {
            con = DataConnect.getConnection();

            ps = con.prepareStatement(getItemsbyCategory);
                ps.setInt(1 , item);
            
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Integer it = rs.getInt("item_id");
                
                items.add(it);
            }
            
            return items;
               
        } catch (SQLException ex) {
            System.out.println("Get all items error -->" + ex.getMessage());       
        } finally {
            DataConnect.close(con);
        }
        return null;
    }
    
    // Auction that have expired but are not set to disabled yet
    public static ArrayList<Item> recentlyExpired(){
        
        Connection con = null;
        PreparedStatement ps = null;
        
        ArrayList<Item> items = new ArrayList<Item>(); 
        
        try {
            con = DataConnect.getConnection();

            ps = con.prepareStatement("Select * from Items where ends < NOW() and status = \"active\" ");
            
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                
                Item it = new Item();
                
                it.setItemId(rs.getInt("item_id"));
                it.setName(rs.getString("name"));
                it.setFirstBid((float) rs.getInt("first_bid") / 100);
                it.setBuyPrice((float) rs.getInt("buy_price"));
                it.setDescription(rs.getString("description"));
                it.setSeller(UserDAO.getUser(rs.getInt("seller")));
                
                items.add(it);
            }
            
            return items;
               
        } catch (SQLException ex) {
            System.out.println("Get all items error -->" + ex.getMessage());       
        } finally {
            DataConnect.close(con);
        }
        return null;
    }
    
    public static void getNow(){
        Connection con = null;
        Statement ps = null;
        
        try{
            
            con = DataConnect.getConnection();
            
            ps = con.createStatement();
            
            ResultSet rs = ps.executeQuery("SELECT CURRENT_TIMESTAMP");
            
            if(rs.next()){
                System.out.println("in results");
                String now = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(rs.getTimestamp(1));
                System.out.println("cur time is : "+now);
            }
            
        } catch (SQLException ex) {
            System.out.println("clearExpired error -->" + ex.getMessage());       
        } finally {
            DataConnect.close(con);
        }
    }
    
    // Find and clear all expired Items
    public static void clearExpired(){
        
        Connection con = null;
        PreparedStatement ps = null;
        
        try{
            
            //get All expired items
            ArrayList<Item> expItems = recentlyExpired();
            
            //Change expired status to dissabled
            con = DataConnect.getConnection();

            ps = con.prepareStatement("Update Items set status=\"disabled\" where ends < NOW() and status = \"active\" ");
            
            ps.executeUpdate();
            
            //for each item
            for(Item it : expItems){
                
                System.out.println("In Clear Expired : found item : "+it.getName());
                getNow();
                
                //Add winner to User_won_Item if any 
                User winner = getWinner(it.getItemId());
                
                if(winner != null){
                    
                    ps = con.prepareStatement("Insert into User_won_Item (uid , item_id) VALUES ( ? , ? ) ");
                        ps.setInt(1, winner.getId());
                        ps.setInt(2, it.getItemId());
                        
                    ps.executeUpdate();
                }
                
            }
        } catch (SQLException ex) {
            System.out.println("clearExpired error -->" + ex.getMessage());       
        } finally {
            DataConnect.close(con);
        }
        
    }
    
    public static void addToWon(int itemId , int uid){
        
        Connection con = null;
        PreparedStatement ps = null;
        
        try {
            con = DataConnect.getConnection();
            
            System.out.println("itemid = "+itemId);
            
            ps = con.prepareStatement("Update Items set status=\"disabled\" where item_id = ? ");
                ps.setInt(1, itemId);
            
            ps.executeUpdate();
            
            System.out.println("First Update");
            
            DataConnect.close(con);
            con = DataConnect.getConnection();
            
            ps = con.prepareStatement("Insert into User_won_Item (uid , item_id) VALUES ( ? , ? ) ");
                ps.setInt(1, uid);
                ps.setInt(2, itemId);
            
            ps.executeUpdate();
            
            System.out.println("Second Update");
            
        } catch (SQLException ex) {
            System.out.println("addToWon error -->" + ex.getMessage());       
        } finally {
            DataConnect.close(con);
        }
        
    }
    
    //Find all items that need to be activated
    public static void updateWaiting(){
        Connection con = null;
        PreparedStatement ps = null;
        
        System.out.println("In Update Waiting");
        
        try {
            con = DataConnect.getConnection();

            ps = con.prepareStatement("Update Items set status=\"active\" where started > NOW() and status = \"waiting\" ");
            
            ps.executeUpdate();
           
        } catch (SQLException ex) {
            System.out.println("Get all items error -->" + ex.getMessage());       
        } finally {
            DataConnect.close(con);
        }
    }
    
    
    public static User getWinner(int itemId){
        
        Connection con = null;
        PreparedStatement ps = null;
        
        ArrayList<Item> items = new ArrayList<Item>(); 
        
        try {
            con = DataConnect.getConnection();

            ps = con.prepareStatement("Select Users.* from Bid , Users where Bid.item_id = ? and Users.uid = Bid.bidder order by Bid.amount DESC");
                ps.setInt(1 , itemId);
                
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                
                
                User winner = new User();

                winner.setId(rs.getInt("Users.uid"));
                winner.setUsername(rs.getString("Users.username"));
                winner.setPassword(rs.getString("Users.password"));
                winner.setFirstName(rs.getString("Users.firstName"));
                winner.setLastName(rs.getString("Users.lastName"));
                winner.setEmail(rs.getString("Users.email"));
                winner.setPhone(rs.getString("Users.phone"));
                winner.setAfm(rs.getString("Users.afm"));

                //System.out.println("User "+winner.getUsername());

                return winner;
            }            
        } catch (SQLException ex) {
            System.out.println("Get all items error -->" + ex.getMessage());       
        } finally {
            DataConnect.close(con);
        }
        
        return null;
        
    }
    
    public static boolean itemIsActive(int itemdId){
        Connection con = null;
        PreparedStatement ps = null;
        
        try {
            con = DataConnect.getConnection();

            ps = con.prepareStatement("Select * from Items where item_id = ? and status = \"active\" ");
                ps.setInt(1, itemdId);
            
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
               
                return true;
               
            }
               
        } catch (SQLException ex) {
            System.out.println("Get all items error -->" + ex.getMessage());       
        } finally {
            DataConnect.close(con);
        }
        return false;
    }
    
    public static void editItem(int itemId , String colName , String newValue){
        Connection con = null;
        PreparedStatement ps = null;
        
        try {
            con = DataConnect.getConnection();
            
            if(colName.equals("name")){
                ps = con.prepareStatement("Update Items set name= ? where item_id = ? ");
                    ps.setString(1, newValue);
                    ps.setInt(2, itemId);
            }
            
            if(colName.equals("fist_bid")){
                ps = con.prepareStatement("Update Items set fist_bid= ? where item_id = ? ");
                    ps.setInt(1, Integer.parseInt(newValue));
                    ps.setInt(2, itemId);
            }
            
            if(colName.equals("location")){
                ps = con.prepareStatement("Update Items set location= ? where item_id = ? ");
                    ps.setString(1, newValue);
                    ps.setInt(2, itemId);
            }
            
            if(colName.equals("country")){
                ps = con.prepareStatement("Update Items set country= ? where item_id = ? ");
                    ps.setString(1, newValue);
                    ps.setInt(2, itemId);
            }
            
            ps.executeUpdate();
           
        } catch (SQLException ex) {
            System.out.println("Get all items error -->" + ex.getMessage());       
        } finally {
            DataConnect.close(con);
        }
    }
    
    public static void setLocation(int id , Location l){
        Connection con = null;
        PreparedStatement ps = null;
        
        try {
            con = DataConnect.getConnection();
            
            // check if location exists
            int locationId = LocationDAO.locationExists(l);
            
            //System.out.println(locationId);
            
               
            if(locationId == -1){
                //if not create location
                
                //System.out.println("Creating location ");
                
                locationId = LocationDAO.addLocation(l);
                
                //System.out.println(locationId);
            }
            
            ps = con.prepareStatement("Insert Into Item_has_Location (item_id , idLocation) VALUES ( ? , ? ) ");
                ps.setInt(1 , id);
                ps.setInt(2, locationId);
            
            ps.executeUpdate();
               
        } catch (SQLException ex) {
            System.out.println("Item setLocation error -->" + ex.getMessage());       
        } finally {
            DataConnect.close(con);
        }
    }
    
    public static Location getLocation(int id){
        Connection con = null;
        PreparedStatement ps = null;
        
        try {
            con = DataConnect.getConnection();
            
            ps = con.prepareStatement("Select l.* From Location l , Item_has_Location ihl Where l.idLocation = ihl.idLocation AND ihl.item_id = ? ");
                ps.setInt(1 , id);
                
            
            ResultSet rs = ps.executeQuery();
            
            if(rs.next()){
                Location l = new Location();
                
                l.setName(rs.getString("l.name"));
                l.setCountry(rs.getString("l.country"));
                l.setLatitude(rs.getBigDecimal("l.latitude"));
                l.setLongitude(rs.getBigDecimal("l.longitude"));
                
                return l;
            }
            else{
                return null;
            }
               
        } catch (SQLException ex) {
            System.out.println("Item setLocation error -->" + ex.getMessage());       
        } finally {
            DataConnect.close(con);
        }
        
        return null;
    } 
    
}
