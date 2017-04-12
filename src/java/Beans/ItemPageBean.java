/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Beans;

import dataAccessObjects.BidDAO;
import dataAccessObjects.ItemDAO;
import dataAccessObjects.UserDAO;
import dbClass.Item;
import dbClass.User;
import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.servlet.http.HttpSession;

/**
 *
 * @author george
 */
@ManagedBean (name="itempage")
@SessionScoped
public class ItemPageBean implements Serializable {
    
    Item item;
    
    float myBid;
    
    @ManagedProperty(value="#{login}") 
    LoginBean loginBean;

    public LoginBean getLoginBean() {
        return loginBean;
    }

    public void setLoginBean(LoginBean bean1) {
        this.loginBean = bean1;
    }
    
    public float getMyBid() {
        return myBid;
    }
    
    public void setMyBid(float myBid){
        this.myBid = myBid;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }
    
    public String loadItem(int id){
        
        item = ItemDAO.getItem(id);
        
        return "itempage";
    }
    
    public String placeBid(){
        
        HttpSession session = SessionBean.getSession();
        
        if(myBid < item.getFirstBid()){
            return "itempage";
        }
        
        if(loginBean.isLoggedIn()){
            User cur = UserDAO.getCurUser();
       
            BidDAO.createBid(item.getItemId() ,cur.getId() ,(int) (myBid * 100));
            
            return "itempage";
        }
        else{
            return "welcome";
        }
        
    }
    
    public boolean isOwner(){
        
        User loggedUser = UserDAO.getCurUser();
        if(loggedUser == null)
            return false;
        
        if(loggedUser.getId() == item.getSeller().getId())
            return true;
        
        return false;
    }
    
    public String buyNow(){
        System.out.println("Congratulations you just bought "+item.getName());
        
        int uid = UserDAO.getCurUser().getId();
        
        ItemDAO.addToWon(item.getItemId() , uid);
        
        return "itempage";
    }
    
    public boolean displayMap(){
        if(item.getLocation().getLatitude() == null)
            return false;
        return true;
    }
    
}
