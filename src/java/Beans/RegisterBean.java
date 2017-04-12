/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Beans;

import dataAccessObjects.UserDAO;
import dbClass.Location;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.servlet.http.HttpSession;
import util.Converter;
import util.ErrorMessage;

/**
 *
 * @author george
 */
@ManagedBean(name = "register")
@SessionScoped
public class RegisterBean implements Serializable  {
    String username;
    String firstName;
    String lastName;
    String password;
    String repPassword;
    String email;
    String phone;
    String afm;
    
    Location location;
    
    public RegisterBean(){
        location = new Location();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
    
    public void setName(String Name){
        location.setName(Name);
    }
    
    public void setCountry(String country){
        location.setCountry(country);
    }
    
    public void setLatitude(BigDecimal lt){
        location.setLatitude(lt);
    }
    
    public void setLongitude(BigDecimal ln){
        location.setLongitude(ln);
    }
    
    
    public String tryRegister(){
        
        boolean error = false;
        
        if(username == null || username.equals("")){
            ErrorMessage.msg("registerform:username", "field Userame cant be empty");
            error = true;
        }
        else if(UserDAO.nameInUse(username)){
            ErrorMessage.msg("registerform:username", "Username already in use");
            error = true;
        }
        
        if(password == null || password.equals("") ||  repPassword == null || repPassword.equals("")){ 
            ErrorMessage.msg("registerform:password", "field Password cant be empty");
            error = true;
        }
        else if(!password.equals(repPassword)){
            ErrorMessage.msg("registerform:password", "Reapeat Password is not the same");
            error = true;
        }
        
        if(firstName == null || firstName.equals("")){
            ErrorMessage.msg("registerform:firstname", "field First Name cant be empty");
            error = true;
        }
        
        if(lastName == null || lastName.equals("")){
            ErrorMessage.msg("registerform:lastname", "field Last Name cant be empty");
            error = true;
        }
        
        if(email == null || email.equals("")){
            ErrorMessage.msg("registerform:email", "field Email cant be empty");
            error = true;
        }
        
        if(location.getName() == null || location.getName().equals("")){
            ErrorMessage.msg("registerform:locationname", "field Location Name cant be empty");
            error = true;
        }
        
        if(location.getCountry() == null || location.getCountry().equals("")){
            ErrorMessage.msg("registerform:locationcountry", "field Country cant be empty");
            System.out.println("Country null");
            error = true;
        }
        
        location.setLatitude(Converter.parseCoords("lat"));
        //System.out.println(location.getLatitude().toString());
        location.setLongitude(Converter.parseCoords("lng"));
        //System.out.println(location.getLongitude().toString());
        
        if(error == false){
            //add user to db
            int uid = UserDAO.addUser(username , password , firstName , lastName , email , phone , afm);

            //set users location
            UserDAO.setLocation(uid , location);

            HttpSession session = SessionBean.getSession();
            session.setAttribute("username", username);
            
            ErrorMessage.msg("registerform", "Registration successfull . Please wait for an admin to accept you (This could take a while)");         
            return "register";
        }
        
        ErrorMessage.msg("registerform", "Registration Unsuccessful . Stop being stupid and fill the form properly");
        return "register";
    }
        
}
