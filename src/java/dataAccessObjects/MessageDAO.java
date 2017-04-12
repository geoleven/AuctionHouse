/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataAccessObjects;

import dbClass.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import util.DataConnect;

/**
 *
 * @author george
 */
public class MessageDAO {
    
    public static void addMessage(int senderId , int receiverId , String msg ){
        Connection con = null;
        PreparedStatement ps = null;
        
        try {
            con = DataConnect.getConnection();

            ps = con.prepareStatement("Insert into Messages (sender , receiver , msg) VALUES ( ? , ? , ?) ");
               ps.setInt(1, senderId);
               ps.setInt(2, receiverId);
               ps.setString(3, msg);
            
            int i = ps.executeUpdate();

               
        } catch (SQLException ex) {
            System.out.println("Add Message error -->" + ex.getMessage());       
        } finally {
            DataConnect.close(con);
        }
    }
    
    public static ArrayList<User> getContacts(int id){
        Connection con = null;
        PreparedStatement ps = null;
        
        ArrayList<User> contacts  = new ArrayList<User>();
        
        try {
            con = DataConnect.getConnection();

            ps = con.prepareStatement("Select * from Messages where sender = ? or receiver = ? order by timesend DESC ");
               ps.setInt(1, id);
               ps.setInt(2, id);
            
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()){
                
                //System.out.println("fount result sender = "+rs.getInt("sender")+" receiver "+rs.getInt("receiver")+" msg "+rs.getString("msg"));
                
                int contactId;
                
                if(rs.getInt("sender")!=id)
                    contactId = rs.getInt("sender");
                else
                    contactId = rs.getInt("receiver");
                
                //not in the list allready
                boolean allreadyIn = false;
                for(User u : contacts){
                    if(u.getId() == contactId){
                        allreadyIn = true;
                        break;
                    }
                }
                
                if(allreadyIn == false){
                    contacts.add(UserDAO.getUser(contactId));
                }
            }
            //System.out.println("Exiting ");
            return contacts;
               
        } catch (SQLException ex) {
            System.out.println("Add Message error -->" + ex.getMessage());       
        } finally {
            DataConnect.close(con);
        }
        
        return null;
    }
    
    public static ArrayList<String> getLog(int id1 , int id2){
        Connection con = null;
        PreparedStatement ps = null;
        
        ArrayList<String> log  = new ArrayList<String>();
        
        try {
            con = DataConnect.getConnection();

            ps = con.prepareStatement("Select * from Messages where (sender = ? and receiver = ?) or (receiver = ? and sender = ?) order by timesend ASC ");
               ps.setInt(1, id1);
               ps.setInt(2, id2);
               ps.setInt(3, id1);
               ps.setInt(4, id2);
            
            ResultSet rs = ps.executeQuery();
            
            User u = UserDAO.getUser(id2);
            
            while (rs.next()){
                String message = "";
                
                if(id1 == rs.getInt("sender"))
                    message +="You (";
                else
                    message +=u.getUsername()+" (";
                message += rs.getDate("timesend");
                message +=") : ";
                message +=rs.getString("msg");
                
                log.add(message);
            }
            
            return log;
               
        } catch (SQLException ex) {
            System.out.println("Add Message error -->" + ex.getMessage());       
        } finally {
            DataConnect.close(con);
        }
        
        return null;
    }
    
    
    public static int getUnread(int id){
        Connection con = null;
        PreparedStatement ps = null;
        
        try {
            con = DataConnect.getConnection();

            ps = con.prepareStatement("Select * from Messages where (sender = ? or receiver = ?) and status = \"unread\" ");
               ps.setInt(1, id);
               ps.setInt(2, id);
               
            ResultSet rs = ps.executeQuery();
            
            int noUnread = 0;
            while (rs.next()){
                noUnread ++;
            }
            
            return noUnread;
               
        } catch (SQLException ex) {
            System.out.println("Add Message error -->" + ex.getMessage());       
        } finally {
            DataConnect.close(con);
        }
        return 0;
    }
    
    public static void clearUnread(int id){
        Connection con = null;
        PreparedStatement ps = null;
        
        try {
            con = DataConnect.getConnection();

            ps = con.prepareStatement("Update Messages set status=\"read\" where (sender = ? or receiver = ?) and status = \"unread\" ");
               ps.setInt(1, id);
               ps.setInt(2, id);
               
            ps.executeUpdate();
               
        } catch (SQLException ex) {
            System.out.println("Add Message error -->" + ex.getMessage());       
        } finally {
            DataConnect.close(con);
        }
    }
    
}
