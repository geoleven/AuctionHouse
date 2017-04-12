/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dbClass;

import dataAccessObjects.UserDAO;
import java.util.ArrayList;

/**
 *
 * @author george
 */

public class User {
    int id;
    String status;
    String firstName;
    String lastName;
    String username;
    String password;
    String email;
    String phone;
    
    String afm;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAfm() {
        return afm;
    }

    public void setAfm(String afm) {
        this.afm = afm;
    }
    
    public ArrayList<Item> getActiveAuctions(){
       
        return UserDAO.getActiveAuctions(id);
    }
    
    public ArrayList<Item> getDisabledAuctions(){
        
        return UserDAO.getDisabledAuctions(id);
    }
    
    public ArrayList<Item> getItemsWon(){
        return UserDAO.getItemsWon(id);
    }
    
    public boolean isAccepted(){
        return UserDAO.isAccepted(id);
    }
    
    public Location getLocation(){
        return UserDAO.getLocation(id);
    }
    
    public void setLocation( Location l){
        
    }
}
