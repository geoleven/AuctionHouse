/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Beans;

import dataAccessObjects.ItemDAO;
import dbClass.Item;
import dbClass.User;
import java.io.Serializable;
import java.util.Date;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author george
 */
@ManagedBean ( name = "editItem")
@SessionScoped
public class editItemBean implements Serializable{
    
    int itemId;
    String name;
    int firstBid;
    String location;
    String country;
    String started;
    Date ends;
    User seller;
    String description;
    
    Item item;
    
    @ManagedProperty(value ="#{itempage}")
    ItemPageBean itemPage;

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public ItemPageBean getItemPage() {
        return itemPage;
    }

    public void setItemPage(ItemPageBean itemPage) {
        this.itemPage = itemPage;
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

    public int getBuyPrice() {
        return firstBid;
    }

    public void setBuyPrice(int buyPrice) {
        this.firstBid = buyPrice;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
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
    
    public String load(int itemId){
        
        System.out.println("edit item loading : item id = "+itemId);
        
        item = ItemDAO.getItem(itemId);
        
        return "editItem";
    }
    
    public String change(){
        
        if(item == null){
            System.out.println("@@@@@@@@@@@@@");
        }
        else{
            if(name !=null){
                if(!name.equals("") && !name.equals(item.getName())){
                    System.out.println("Changing items name");
                    ItemDAO.editItem(item.getItemId() , "name"  , name);
                }
            }


            if(firstBid > 0 && firstBid != item.getFirstBid()){
                System.out.println("Changing items firstBid");
                ItemDAO.editItem(item.getItemId() , "first_bid"  , Integer.toString(firstBid));
            }

        }
        
        return itemPage.loadItem(item.getItemId());
    }
    
}
