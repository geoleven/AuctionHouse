package dataAccessObjects;


import dbClass.Location;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import util.DataConnect;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author george
 */
public class LocationDAO {
    
    public static int addLocation(Location  l){
        Connection con = null;
        PreparedStatement ps = null;
        
        try {
            con = DataConnect.getConnection();

            ps = con.prepareStatement("Insert into Location (name , country , longitude , latitude ) VALUES ( ? , ? , ? , ? )" , new String[]{"idLocation"});
                ps.setString(1, l.getName());
                ps.setString(2, l.getCountry());
                ps.setBigDecimal(3 , l.getLongitude());
                ps.setBigDecimal(4 , l.getLatitude());
            
            ps.executeUpdate();
            
            ResultSet rs = ps.getGeneratedKeys();
            if (rs != null && rs.next()) {
                return rs.getInt(1);
            }
               
        } catch (SQLException ex) {
            System.out.println("addLocation error -->" + ex.getMessage());       
        } finally {
            DataConnect.close(con);
        }
        
        return -1;
    }
    
    public static int locationExists(Location l){
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            
            //System.out.println("location name "+l.getName());
            
            con = DataConnect.getConnection();

            ps = con.prepareStatement("SELECT * FROM Location l WHERE l.name = ? AND l.country = ?");
                ps.setString(1 , l.getName());
                ps.setString(2 , l.getCountry());
            
            rs = ps.executeQuery();
                
            if (rs.next()) {
                //System.out.println(rs.getInt("l.idLocation"));
                
                if(l.getLatitude() == null ){
                    if(rs.getBigDecimal("latitude") == null)
                        return rs.getInt("l.idLocation");
                    else
                      return -1;     
                }
                
                if(l.getLatitude().compareTo(rs.getBigDecimal("latitude")) == 0 && l.getLongitude().compareTo(rs.getBigDecimal("longitude")) == 0){
                    return rs.getInt("l.idLocation");
                }
                
                return -1;
            }
            else{
                //System.out.println("no next");
                return -1;
            }
               
        } catch (SQLException ex) {
            System.out.println("locationExists error -->" + ex.getMessage());       
        } finally {
            DataConnect.close(con);
        }
        
        return -1;
    }
    
}
