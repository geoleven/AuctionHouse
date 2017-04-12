/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Beans;

import dataAccessObjects.UserDAO;
import dbClass.User;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import util.BCrypt;
import util.DataConnect;
import util.ErrorMessage;

/**
 *
 * @author george
 */
@ManagedBean(name="login")
@SessionScoped
public class LoginBean implements Serializable {
    
    String username;
    String password;
    String message;
    
    User user;
    
    public LoginBean(){
        System.out.println("Created login bean");
        username = null;
        password = null;
        user = null;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }   
    
    public String validate(){
        
        System.out.println("In Validate");
        
        if(UserDAO.login(username , password)){
            HttpSession session = SessionBean.getSession();
            session.setAttribute("username", username);
            
            user = UserDAO.getCurUser();
            
            return "main";
        }
        
        ErrorMessage.msg("loginform", "Incorect Username or Password");
        
        return "welcome";
    }
    
    public String logout() {
        HttpSession session = SessionBean.getSession();
        session.invalidate();
        
        user = null;
        
        return "welcome";
    }
    
    public boolean isLoggedIn(){
        
        //System.out.println("LOGGED IN : "+user);
        
        if(user == null) return false;
        else return true;
    }
    
    public boolean isAdmin() {
        return UserDAO.isAdmin(user.getId());
    }
    
    public void redirectToMain() throws Exception{
        if(isLoggedIn()){
            System.out.println("User already logged in redirecting");
            
            FacesContext.getCurrentInstance().getExternalContext().redirect("main.xhtml");
        }
    }
    
     
     
}
