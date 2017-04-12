/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataAccessObjects;

import dbClass.Bid;
import dbClass.Item;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import util.DataConnect;

/**
 *
 * @author george
 */
public class BidDAO {
    
    //Queries
    public static String createBid = "Insert into Bid (item_id , bidder , amount ) VALUES ( ? , ? , ?)";
    
    public static String getItemBids = "Select * from Bid where item_id = ? order by amount";
    
    public static void createBid(int itemId , int bidder , int amount){
        Connection con = null;
        PreparedStatement ps = null;
        
        try {
            con = DataConnect.getConnection();

            ps = con.prepareStatement(createBid);
                ps.setInt(1, itemId);
                ps.setInt(2, bidder);
                ps.setInt(3, amount);
            
            int i = ps.executeUpdate();
               
        } catch (SQLException ex) {
            System.out.println("Create Bid error -->" + ex.getMessage());       
        } finally {
            DataConnect.close(con);
        }
    }
    
    public static boolean itemHasBids(int itemId){
        Connection con = null;
        PreparedStatement ps = null;
        
        try {
            con = DataConnect.getConnection();
            
            ps = con.prepareStatement(getItemBids);
                ps.setInt(1 , itemId);
            
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()) {
                
                return true;
            }
               
        } catch (SQLException ex) {
            System.out.println("Get Item error -->" + ex.getMessage());       
        } finally {
            DataConnect.close(con);
        }
        return false;
    }
    
    public static ArrayList<Bid> getItemBids(int itemId){
        Connection con = null;
        PreparedStatement ps = null;
        
        ArrayList<Bid> bids = new ArrayList<Bid>();
        
        try {
            con = DataConnect.getConnection();
            
            ps = con.prepareStatement(getItemBids);
                ps.setInt(1 , itemId);
            
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()) {
                
                Bid b = new Bid();
                
                b.setId(rs.getInt("bid_id"));
                b.setItemId(rs.getInt("item_id"));
                b.setBidder(rs.getInt("bidder"));
                b.setAmount((float) rs.getInt("amount") / 100);
                b.setTime(rs.getTimestamp("time"));
                
                bids.add(b);    
            }
            
            Collections.reverse(bids);
            
            return bids;
               
        } catch (SQLException ex) {
            System.out.println("Get Item error -->" + ex.getMessage());       
        } finally {
            DataConnect.close(con);
        }
        return null;
    }
}
