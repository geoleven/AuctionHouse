/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataAccessObjects;

import Beans.SessionBean;
import dbClass.Item;
import dbClass.Location;
import dbClass.User;
import util.DataConnect;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;  
import java.sql.SQLException;
import java.util.ArrayList;
import javax.servlet.http.HttpSession;
import util.BCrypt;

/**
 *
 * @author george
 */

public class UserDAO {
    
    //Queries
    
    public static String getHashSalt = "SELECT password, salt FROM Users WHERE username = ?";
    
    public static String login = "Select * from Users , Accepted_Users where Accepted_Users.uid = Users.uid and Users.username = ? and Users.password = ?";
    
    public static String nameInUse = "Select username from Users where username = ?";
    
    public static String addUser = "Insert Into Users ( firstName , lastName , username , password , email , phone , afm , salt ) VALUES ( ? , ? , ? , ? , ? , ? , ? , ? )";
    
    public static String getUserbyName = "Select * from Users where username = ?";
    
    public static String getUserbyId = "Select * from Users where uid = ?";
    
    public static String itemsWon = "";
    
    public static boolean login(String name , String password){
        Connection con = null;
        PreparedStatement ps = null;
 
        try {
            
            con = DataConnect.getConnection();
            
            ps = con.prepareStatement(getHashSalt);
            ps.setString(1, name);
            
            String passwordHash = "";
            String passwordSalt = "";
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                passwordHash = rs.getString("password");
                passwordSalt = rs.getString("salt");
            }
            
            System.out.println(passwordHash+" "+passwordSalt);
            
            ps = con.prepareStatement(login);
            ps.setString(1, name);
            ps.setString(2, passwordHash);
 
            rs = ps.executeQuery();
          
            if (rs.next() && BCrypt.checkpw(password, passwordHash)) {
                //result found, means valid inputs
                return true;
            }
        } catch (SQLException ex) {
            System.out.println("Login error -->" + ex.getMessage());
            return false;
        } finally {
            DataConnect.close(con);
        }
        
        return false;
    }
    
    public static boolean isAdmin(int uid){
        Connection con = null;
        PreparedStatement ps = null;
 
        try {
            
            con = DataConnect.getConnection();
            
            ps = con.prepareStatement("Select * from Admins where uid = ?");
            ps.setInt(1, uid);
            
            ResultSet rs = ps.executeQuery();
 
            if (rs.next()) {
                //result found, means valid inputs
                return true;
            }
        } catch (SQLException ex) {
            System.out.println("isAdmin error -->" + ex.getMessage());
            return false;
        } finally {
            DataConnect.close(con);
        }
        
        return false;
    }
    
    public static boolean isAccepted(int uid){
        
        Connection con = null;
        PreparedStatement ps = null;
 
        try {
            
            con = DataConnect.getConnection();
            
            ps = con.prepareStatement("Select * from Accepted_Users where uid = ?");
            ps.setInt(1, uid);
            
            ResultSet rs = ps.executeQuery();
 
            if (rs.next()) {
                //result found, means valid inputs
                return true;
            }
        } catch (SQLException ex) {
            System.out.println("isAccepted error -->" + ex.getMessage());
            return false;
        } finally {
            DataConnect.close(con);
        }
        
        return false;
    }
    
    public static void accept(int uid){
        Connection con = null;
        PreparedStatement ps = null;
 
        try {
            
            con = DataConnect.getConnection();
            
            ps = con.prepareStatement("Insert Into Accepted_Users (uid) VALUES (?)");
                ps.setInt(1, uid);
            
            ps.executeUpdate();
 
        } catch (SQLException ex) {
            System.out.println("accept error -->" + ex.getMessage());
            
        } finally {
            DataConnect.close(con);
        }
        
        
    }
    
    public static ArrayList<User> getAllUsers(){
        Connection con = null;
        PreparedStatement ps = null;
        
        ArrayList<User> users = new ArrayList<User>();
 
        try {
            
            con = DataConnect.getConnection();
            
            ps = con.prepareStatement("Select * from Users");
            
            ResultSet rs = ps.executeQuery();
 
            while (rs.next()) {
                User u = new User();
                
                u.setId(rs.getInt("uid"));
                u.setUsername(rs.getString("username"));
                u.setPassword(rs.getString("password"));
                
                users.add(u);                
            }
            
            return users;
            
        } catch (SQLException ex) {
            System.out.println("Get All Users error -->" + ex.getMessage());
            
        } finally {
            DataConnect.close(con);
        }
        
        return null;
    }
    
    public static ArrayList<User> getAllUsers( int nResults , int start ){
        Connection con = null;
        PreparedStatement ps = null;
        
        ArrayList<User> users = new ArrayList<User>();            
 
        try {
            
            con = DataConnect.getConnection();
            
            ps = con.prepareStatement("Select * from Users Limit ? Offset ?");
                ps.setInt(1, nResults);
                ps.setInt(2, start);
            
            ResultSet rs = ps.executeQuery();
 
            while (rs.next()) {
                User u = new User();
                
                u.setId(rs.getInt("uid"));
                u.setUsername(rs.getString("username"));
                u.setPassword(rs.getString("password"));
                
                users.add(u);                
            }
            
            return users;
            
        } catch (SQLException ex) {
            System.out.println("Get All Users error -->" + ex.getMessage());
            
        } finally {
            DataConnect.close(con);
        }
        
        return null;
    }
    
    public static ArrayList<User> getUsersNotAccpted( int nResults , int start ){
        Connection con = null;
        PreparedStatement ps = null;
        
        ArrayList<User> users = new ArrayList<User>();            
 
        try {
            
            con = DataConnect.getConnection();
            
            ps = con.prepareStatement("SELECT Users.* from Users left join Accepted_Users on Users.uid = Accepted_Users.uid where Accepted_Users.uid is null Limit ? Offset ?");
                ps.setInt(1, nResults);
                ps.setInt(2, start);
            
            ResultSet rs = ps.executeQuery();
 
            while (rs.next()) {
                User u = new User();
                
                u.setId(rs.getInt("Users.uid"));
                u.setUsername(rs.getString("Users.username"));
                u.setPassword(rs.getString("Users.password"));
                
                users.add(u);                
            }
            
            return users;
            
        } catch (SQLException ex) {
            System.out.println("Get All Users error -->" + ex.getMessage());
            
        } finally {
            DataConnect.close(con);
        }
        
        return null;
    }
    
    public static boolean nameInUse(String name){
        Connection con = null;
        PreparedStatement ps = null;
        
        try {
            con = DataConnect.getConnection();

            ps = con.prepareStatement(nameInUse);
                ps.setString(1, name);
            
            ResultSet rs = ps.executeQuery();
 
            if (rs.next()) {
                //result found, means valid inputs
                return true;
            }
               
        } catch (SQLException ex) {
            System.out.println("nameInUse error -->" + ex.getMessage());
            return false;
        } finally {
            DataConnect.close(con);
        }
        return false;
    }
    
     public static int addUser(String username ,String password ,String firstName ,String lastName ,String email ,String phone ,String afm){
        Connection con = null;
        PreparedStatement ps = null;
        
        try {
            con = DataConnect.getConnection();

            String salt = BCrypt.gensalt();
            
            ps = con.prepareStatement(addUser ,  new String[]{"uid"});
                ps.setString(1, firstName);
                ps.setString(2, lastName);
                ps.setString(3, username);
                ps.setString(4, BCrypt.hashpw(password, salt));
                ps.setString(5, email);
                ps.setString(6, phone);
                ps.setString(7, afm);
                ps.setString(8, salt);
            
            int i = ps.executeUpdate();
            
            ResultSet rs = ps.getGeneratedKeys();
            if (rs != null && rs.next()) {
                return rs.getInt(1);
            }
               
        } catch (SQLException ex) {
            System.out.println("Register error -->" + ex.getMessage());       
        } finally {
            DataConnect.close(con);
        }
        
        return -1;
    }
    
    public static User getUser(String name){
        Connection con = null;
        PreparedStatement ps = null;
 
        try {
            
            con = DataConnect.getConnection();
            
            ps = con.prepareStatement(getUserbyName);
            ps.setString(1, name);
 
            ResultSet rs = ps.executeQuery();
            
            User u = new User();
            
            if (rs.next()) {
                
                u.setId(rs.getInt("uid"));
                u.setUsername(rs.getString("username"));
                u.setPassword(rs.getString("password"));
                u.setFirstName(rs.getString("firstName"));
                u.setLastName(rs.getString("lastName"));
                u.setEmail(rs.getString("email"));
                u.setPhone(rs.getString("phone"));
                u.setAfm(rs.getString("afm"));
                
                return u;
            }
        } catch (SQLException ex) {
            System.out.println("get User error -->" + ex.getMessage());
            return null;
        } finally {
            DataConnect.close(con);
        }
        
        return null;
    }
    
    public static User getUser(int id){
        Connection con = null;
        PreparedStatement ps = null;
 
        try {
            
            con = DataConnect.getConnection();
            
            ps = con.prepareStatement(getUserbyId);
            ps.setInt(1, id);
 
            ResultSet rs = ps.executeQuery();
            
            User u = null;
            
            if (rs.next()) {
                
                u = new User();
                
                u.setId(rs.getInt("uid"));
                u.setUsername(rs.getString("username"));
                u.setPassword(rs.getString("password"));
                u.setFirstName(rs.getString("firstName"));
                u.setLastName(rs.getString("lastName"));
                u.setEmail(rs.getString("email"));
                u.setPhone(rs.getString("phone"));
                u.setAfm(rs.getString("afm"));
                
                return u;
            }
            
            return null;
            
        } catch (SQLException ex) {
            System.out.println("Get User error -->" + ex.getMessage());
            return null;
        } finally {
            DataConnect.close(con);
        }
        
    }
    
    public static User getCurUser(){
        HttpSession session = SessionBean.getSession();
        
        if(session.getAttribute("username") != null)
            return getUser(session.getAttribute("username").toString());
        
        return null;
    }
    
    public static ArrayList<Item> getActiveAuctions(int uid){
        Connection con = null;
        PreparedStatement ps = null;
        
        ArrayList<Item> items = new ArrayList<Item>();
        
        try {
            
            con = DataConnect.getConnection();
            
            ps = con.prepareStatement("Select * from Items where seller = ? and status = \"active\" ");
                ps.setInt(1, uid);
 
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
            System.out.println("getActiveAuctions error -->" + ex.getMessage());
        } finally {
            DataConnect.close(con);
        }
        
        return items;
    }
    
    
    public static ArrayList<Item> getDisabledAuctions(int uid){
        Connection con = null;
        PreparedStatement ps = null;
        
        ArrayList<Item> items = new ArrayList<Item>();
        
        try {
            
            con = DataConnect.getConnection();
            
            ps = con.prepareStatement("Select * from Items where seller = ? and status = \"disabled\" ");
                ps.setInt(1, uid);
 
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
            System.out.println("getDisabledAuctions error -->" + ex.getMessage());
        } finally {
            DataConnect.close(con);
        }
        
        return items;
    }
    
    public static ArrayList<Item> getItemsWon(int uid){
        Connection con = null;
        PreparedStatement ps = null;
        
        ArrayList<Item> items = new ArrayList<Item>();
        
        User u = UserDAO.getUser(uid);
        
        try {
            
            con = DataConnect.getConnection();
            
            ps = con.prepareStatement("Select Items.* from Items , User_won_Item where Items.item_id = User_won_Item.item_id and User_won_Item.item_id = ? ");
                ps.setInt(1, uid);
 
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                
                Item it = new Item();
                
                it.setItemId(rs.getInt("item_id"));
                it.setName(rs.getString("name"));
                it.setFirstBid((float) rs.getInt("first_bid") / 100);
                it.setBuyPrice((float) rs.getInt("buy_price") / 100);
                it.setDescription(rs.getString("description"));
                it.setSeller(UserDAO.getUser(rs.getInt("seller")));
                
                
                if(it.getWinner().equals(u.getUsername())){
                    System.out.println("Found a winner");
                    boolean alreadyIN = false;
                    for(Item i : items){
                        if(i.getItemId() == it.getItemId()){
                            alreadyIN = true;
                            break;
                        }
                    }
                    if(alreadyIN == false)
                        items.add(it);
                }
            }
            
            return items;
            
        } catch (SQLException ex) {
            System.out.println("getItemsWon error -->" + ex.getMessage());
        } finally {
            DataConnect.close(con);
        }
        
        return items;
    }
    
    public static void editUser (String username , String colName , String newValue){
        Connection con = null;
        PreparedStatement ps = null;
        
        try {
            con = DataConnect.getConnection();
            
            if(colName.equals("firstName")){
                ps = con.prepareStatement("Update Users set firstName= ? where username = ? ");
                    ps.setString(1, newValue);
                    ps.setString(2, username);
            }           
            else if(colName.equals("lastName")){
                ps = con.prepareStatement("Update Users set lastName= ? where username = ? ");
                    ps.setString(1, newValue);
                    ps.setString(2, username);
            }
            else if(colName.equals("email")){
                ps = con.prepareStatement("Update Users set email= ? where username = ? ");
                    ps.setString(1, newValue);
                    ps.setString(2, username);
            }
            else if(colName.equals("phone")){
                ps = con.prepareStatement("Update Users set phone= ? where username = ? ");
                    ps.setString(1, newValue);
                    ps.setString(2, username);
            }
            else if(colName.equals("afm")){
                ps = con.prepareStatement("Update Users set afm= ? where username = ? ");
                    ps.setString(1, newValue);
                    ps.setString(2, username);
            }
            else if(colName.equals("password")){
                ps = con.prepareStatement("Update Users set password= ? where username = ? ");
                PreparedStatement ps2 = con.prepareStatement("SELECT salt FROM Users WHERE username = ?");
                ResultSet rs2 = ps2.executeQuery();
                String salt = "";
                if(rs2.next())
                    salt = rs2.getString("salt");
                ps.setString(1, BCrypt.hashpw(newValue, salt));
                ps.setString(2, username);
            }    
            
            ps.executeUpdate();
           
        } catch (SQLException ex) {
            System.out.println("Get all items error -->" + ex.getMessage());       
        } finally {
            DataConnect.close(con);
        }
    }
    
    public static void setLocation(int uid , Location l){
        Connection con = null;
        PreparedStatement ps = null;
        
        try {
            con = DataConnect.getConnection();
            
            //System.out.println("Checking location exists");
            
            // check if location exists
            int locationId = LocationDAO.locationExists(l);
            
            System.out.println(locationId);
            
               
            if(locationId == -1){
                //if not create location
                
                //System.out.println("Creating location ");
                
                locationId = LocationDAO.addLocation(l);
                
                System.out.println(locationId);
            }
            
            ps = con.prepareStatement("Insert Into User_has_Location (uid , idLocation) VALUES ( ? , ? ) ");
                ps.setInt(1 , uid);
                ps.setInt(2, locationId);
            
            ps.executeUpdate();
               
        } catch (SQLException ex) {
            System.out.println("setLocation error -->" + ex.getMessage());       
        } finally {
            DataConnect.close(con);
        }
    }
    
    public static Location getLocation(int uid){
        Connection con = null;
        PreparedStatement ps = null;
        
        try {
            con = DataConnect.getConnection();
            
            ps = con.prepareStatement("Select l.* From Location l , User_has_Location uhl Where l.idLocation = uhl.idLocation AND uhl.uid = ? ");
                ps.setInt(1 , uid);
                
            
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
            System.out.println("setLocation error -->" + ex.getMessage());       
        } finally {
            DataConnect.close(con);
        }
        
        return null;
        
    }
}
