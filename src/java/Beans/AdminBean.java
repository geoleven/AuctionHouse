/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Beans;

import dataAccessObjects.UserDAO;
import dbClass.User;
import java.io.Serializable;
import java.util.ArrayList;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author george
 */
@ManagedBean (name = "admin")
@SessionScoped
public class AdminBean implements Serializable{
    ArrayList<User> users;
    
    int page;
    int nPerPage;
    
    boolean allUsers;
    boolean prevResults;
    
    public AdminBean(){
        page = 1;
        nPerPage = 10;
        
        allUsers = true ;
        prevResults = true ;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getnPerPage() {
        return nPerPage;
    }

    public void setnPerPage(int nPerPage) {
        this.nPerPage = nPerPage;
    }
    
    public String nextPage(){
        page++;
        loadAdminPage();
        return "adminpage";
    }
    
    public String prevPage(){
        page--;
        loadAdminPage();
        return "adminpage";
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }
    
    public String loadAdminPage(){
        if(allUsers == true){
            if(prevResults == false){
               page = 1;
               prevResults = true;
            }            
            users = UserDAO.getAllUsers(nPerPage , (page - 1) * nPerPage);
        }
        else{
            if(prevResults == true){
                page = 1;
                prevResults = false;
            }           
            users = UserDAO.getUsersNotAccpted(nPerPage , (page - 1) * nPerPage);
        }
         
        return "adminpage";
    }
    
    public String getAllUsers(){
        allUsers = true;
        
        return loadAdminPage();
    }
    
    public String getUsersNotAccepted(){
        allUsers = false;
        
        return loadAdminPage();
    }
    
    public String activateUser(int id){
        
        UserDAO.accept(id);
        
        loadAdminPage();
        return "adminpage";
    }
}
