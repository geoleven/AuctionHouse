/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Beans;

import dataAccessObjects.ItemDAO;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import dataAccessObjects.UserDAO;
import dbClass.Location;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import util.Converter;
import util.ErrorMessage;

/**
 *
 * @author george
 */
@ManagedBean (name = "auction")
@SessionScoped
public class CreateAuctionBean implements Serializable {
    
    int itemId;
    String name;
    float firstBid;
    float buyPrice;
    String categories;
    Date starts;
    Date ends;
    int sellerId;
    String description;
    
    Location location;
    
    public CreateAuctionBean(){
        location = new Location();
    }

    public float getFirstBid() {
        return firstBid;
    }

    public void setFirstBid(float firstBid) {
        this.firstBid = firstBid;
    }

    public float getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(float buyPrice) {
        this.buyPrice = buyPrice;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
    
    public Date getStarts() {
        return starts;
    }

    public void setStarts(Date starts) {
        this.starts = starts;
    }
    
    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemID(int itemID) {
        this.itemId = itemID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getEnds() {
        return ends;
    }

    public void setEnds(Date ends) {
        this.ends = ends;
    }

    public int getSellerId() {
        return sellerId;
    }

    public void setSellerId(int sellerId) {
        this.sellerId = sellerId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public String createAuction() throws Exception{
        
        boolean error = false;
        
        if(name == null || name.equals("")){
            ErrorMessage.msg("itemcreateform:name", "field Name cant be empty");
            error = true;
        }
        
        if(firstBid <= 0 ){
            ErrorMessage.msg("itemcreateform:firstbid", "wrong price ");
            error = true;
        }
        
        if(buyPrice < 0 ){
            ErrorMessage.msg("itemcreateform:buyprice", "wrong price");
        }
        
        if(location.getName() == null || location.getName().equals("")){
            ErrorMessage.msg("itemcreateform:locationname", "field Location cant be empty");
            error = true;
        }
        
        if(location.getCountry() == null || location.getCountry().equals("")){
            ErrorMessage.msg("itemcreateform:locationcountry", "field Country cant be empty");
            error = true;
        }
        
        location.setLatitude(Converter.parseCoords("lat"));
        //System.out.println(location.getLatitude().toString());
        location.setLongitude(Converter.parseCoords("lng"));
        //System.out.println(location.getLongitude().toString());
        
        
        if(error == false){
            // Get Seller
            int sellerId = UserDAO.getCurUser().getId();

             int itemId;
            // Create Item
            if(starts != null){
                System.out.println("Creating item with starting date");
                if(buyPrice > 0)
                    itemId = ItemDAO.createItem(name , sellerId ,(int) (firstBid * 100) ,(int) (buyPrice * 100),Converter.dateToTimestamp(starts) , Converter.dateToTimestamp(ends) , description );
                else
                    itemId = ItemDAO.createItem(name , sellerId , (int) (firstBid * 100) ,Converter.dateToTimestamp(starts) , Converter.dateToTimestamp(ends) , description );
            }
            else{
                System.out.println("Creating item without starting date");
                if(buyPrice > 0)
                    itemId = ItemDAO.createItem(name , sellerId , (int) (firstBid * 100) , (int) (buyPrice * 100) , Converter.dateToTimestamp(ends) , description );
                else
                    itemId = ItemDAO.createItem(name , sellerId , (int) (firstBid * 100) , Converter.dateToTimestamp(ends) , description );
            }

            // Break Categories String
            ArrayList<String> cat = breakCategories();

            // Insert categories
            for(String c : cat){
                ItemDAO.addCategory(c , itemId);
            }

            //Insert Location
            ItemDAO.setLocation(itemId, location);

            //System.out.println("Start time : "+starts.toString());
            //System.out.println("End time : "+ends.toString());
            
            System.out.println("Item Created Succesfully");

            return "main";
        }
        
        return "auctionCreate";
    }
    
    public ArrayList<String> breakCategories(){
        Pattern pattern = Pattern.compile("\\w+");
        Matcher matcher = pattern.matcher(categories);
        
        ArrayList<String> cat = new ArrayList<String>();
        
        while (matcher.find()) {
            cat.add(matcher.group());
        }
        
        return cat;
    }
}
