/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Beans;

import dataAccessObjects.ItemDAO;
import java.io.Serializable;
import java.util.Date;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

/**
 *
 * @author george
 */
@ManagedBean (name = "itemExpire" , eager = true)
@ApplicationScoped
public class ItemStatusUpdateBean implements Serializable {
    
    Date lastCheck;
    
    public ItemStatusUpdateBean(){
        
        System.out.println("Create ItemStatusUpdateBean");
        
        lastCheck = null;
    }
    
    public Date getLastCheck() {
        return lastCheck;
    }

    public void setLastCheck(Date lastCheck) {
        this.lastCheck = lastCheck;
    }
    
    public void updateStatus(){
        
        System.out.println("In updateStatus");
        ItemDAO.getNow();
        
        if(lastCheck == null){ //First Check
            lastCheck = new Date();
            ItemDAO.updateWaiting();
            ItemDAO.clearExpired();  
        }
        else{
            Date curDateTime = new Date();
            
            long secDif = (curDateTime.getTime() - lastCheck.getTime())/1000;
            
            if(secDif > 5){
                //System.out.println("5 secs have passed");
                lastCheck = new Date();
                ItemDAO.updateWaiting();
                ItemDAO.clearExpired();
            }
            else{
                //System.out.println("5 secs have not passed");
            }
               
        }
              
    }
    
}
