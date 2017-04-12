/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dbClass;

import dataAccessObjects.BidDAO;
import dataAccessObjects.ItemDAO;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author george
 */
public class Item {
    
    int itemId;
    String name;
    float firstBid;
    float buyPrice;
    String started;
    Date ends;
    User seller;
    String description;

    public float getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(float buyPrice) {
        this.buyPrice = buyPrice;
    }
    
    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getFirstBid() {
        return firstBid;
    }

    public void setFirstBid(float buyPrice) {
        this.firstBid = buyPrice;
    }

    public String getStarted() {
        return started;
    }

    public void setStarted(String started) {
        this.started = started;
    }

    public Date getEnds() {
        return ends;
    }

    public void setEnds(Date ends) {
        this.ends = ends;
    }

    public User getSeller() {
        return seller;
    }

    public void setSeller(User seller) {
        this.seller = seller;
    }
    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public void print(){
        System.out.println("{ "+itemId+" , "+name+" , "+firstBid+" , "+description+" }");
    }
    
    public boolean hasBids(){
        return BidDAO.itemHasBids(itemId);
    }
    
    public float highestBid(){
        
        float maxBid = -1;
        
        ArrayList<Bid> bids = BidDAO.getItemBids(itemId);
        
        for(Bid b : bids){
            if(b.getAmount() > maxBid){
                maxBid = b.getAmount();
            }
        }
        
        if(maxBid == -1)
            return -1;
        else
            return maxBid;
    }
    
    public ArrayList<Bid> getBids(){
        
        System.out.println("Get Bid Called");
        
        ArrayList<Bid> bids = null;
        
        bids = BidDAO.getItemBids(itemId);
        
        for(Bid b : bids){
            System.out.println(b.amount);
        }
        
        return bids;
    }
    
    public float curPrice(){
        
        float p = highestBid();
        
        if(p == -1){
            p = firstBid;
        }
        
        return p;   
    }
    
    public String secondsTilEnds(){
        
        Date curDateTime = new Date();
        //System.out.println(curDateTime);
        //System.out.println(ends);
        
        long secDif = (ends.getTime() - curDateTime.getTime())/1000;
        
        //System.out.println(secDif);
        
        if(secDif > 0)
            return Long.toString(secDif);
        else
            return Integer.toString(0);
    }
    
    public String getCategories(){
        
        ArrayList<String> categories = ItemDAO.getItemCategories(itemId);
        
        String categLine = "";
        
        for( String c : categories ){
            categLine += c + " ";
        }
        
        return categLine;
    }
    
    public String getWinner(){
        User winner = ItemDAO.getWinner(itemId);
        
        if(winner != null)
            return winner.getUsername();
        else
            return "No Bidders";
    }
    
    public boolean isActive(){
        return ItemDAO.itemIsActive(itemId);
    }
    
    public void setLocation(Location l){
        ItemDAO.setLocation(itemId, l);
    }
    
    public Location getLocation(){
        return ItemDAO.getLocation(itemId);
    }
    
    public boolean hasBuyPrice(){
        
        if(buyPrice == 0)
            return false;
        
        return true;
    }
    
}
