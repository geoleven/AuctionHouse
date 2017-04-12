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
@ManagedBean (name = "editUser")
@SessionScoped
public class EditUserBean implements Serializable{
    String firstName;
    String lastName;
    String email;
    String phone;
    String address;
    String afm;
    String oldPassword;
    String password;
    String repPassword;
    
    @ManagedProperty(value ="#{userpage}")
    UserpageBean userPage;

    public UserpageBean getUserPage() {
        return userPage;
    }

    public void setUserPage(UserpageBean userPage) {
        this.userPage = userPage;
    }
    

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAfm() {
        return afm;
    }

    public void setAfm(String afm) {
        this.afm = afm;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRepPassword() {
        return repPassword;
    }

    public void setRepPassword(String repPassword) {
        this.repPassword = repPassword;
    }
    
    public String change(){
        
        HttpSession session = SessionBean.getSession();
        User user = UserDAO.getUser(session.getAttribute("username").toString());
        
        if(firstName !=null){
            if(!firstName.equals("") && !firstName.equals(user.getFirstName())){
                
                UserDAO.editUser(user.getUsername() , "firstName"  , firstName);
            }
        }
        
        if(lastName !=null){
            if(!lastName.equals("") && !lastName.equals(user.getLastName())){
                
                UserDAO.editUser(user.getUsername() , "lastName"  , lastName);
            }
        }
        
        if(email !=null){
            if(!email.equals("") && !email.equals(user.getEmail())){
                
                UserDAO.editUser(user.getUsername() , "email"  , email);
            }
        }
         
        if(phone !=null){
            if(!phone.equals("") && !phone.equals(user.getPhone())){
                
                UserDAO.editUser(user.getUsername() , "phone"  , phone);
            }
        }
         
        if(afm !=null){
            if(!afm.equals("") && !afm.equals(user.getAfm())){
                
                UserDAO.editUser(user.getUsername() , "afm"  , afm);
            }
        }
        
        
        return userPage.loadPage();
    }
    
    public String changePassword(){
        
        HttpSession session = SessionBean.getSession();
        User user = UserDAO.getUser(session.getAttribute("username").toString());
        
        if(password !=null){
            if(!password.equals("") && !password.equals(user.getPassword()) && password.equals(repPassword)){
                
                UserDAO.editUser(user.getUsername() , "password"  , password);
            }
        }
        
        
        return userPage.loadPage();
    }
}
