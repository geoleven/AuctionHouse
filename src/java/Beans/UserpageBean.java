/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Beans;

import dataAccessObjects.UserDAO;
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
@ManagedBean(name="userpage")
@SessionScoped
public class UserpageBean implements Serializable {
    User user;
    
    @ManagedProperty(value = "#{itemExpire}")
    ItemStatusUpdateBean itemExpire;

    public ItemStatusUpdateBean getItemExpire() {
        return itemExpire;
    }

    public void setItemExpire(ItemStatusUpdateBean itemExpire) {
        this.itemExpire = itemExpire;
    }
    
    public UserpageBean(){
        user = null;
    }
    
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    
    public String loadPage(){
        HttpSession session = SessionBean.getSession();
        user = UserDAO.getUser(session.getAttribute("username").toString());
        
        itemExpire.updateStatus();
        
        return "userpage";
    }
    
    public String loadPage(int uid){
        user = UserDAO.getUser(uid);
        
        itemExpire.updateStatus();
        
        return "userpage";
    }
    
    public String loadPage(String username){
        user = UserDAO.getUser(username);
        
        itemExpire.updateStatus();
        
        return "userpage";
    }
    
    public boolean isLoggedUser(){
        HttpSession session = SessionBean.getSession();
        if(session.getAttribute("username") == null)
            return false;
        if (user.getUsername().equals(session.getAttribute("username").toString())){
            return true;
        }
        return false;
    }
    
    public boolean dispayMap(){
        if(user.getLocation().getLatitude() == null)
            return false;
        return true;
    }
    
}
