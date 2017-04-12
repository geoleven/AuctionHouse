/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Beans;

import dataAccessObjects.MessageDAO;
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
@ManagedBean (name = "message")
@SessionScoped
public class MessageBean implements Serializable{
    User user;
    
    String sendTo;
    String sendMsg;
    
    User displayUser;
    ArrayList<String> log;
    
    ArrayList<User> contactList;

    public ArrayList<String> getLog() {
        return log;
    }

    public void setLog(ArrayList<String> log) {
        this.log = log;
    }
    
    public User getDisplayUser() {
        return displayUser;
    }

    public void setDisplayUser(User displayUser) {
        this.displayUser = displayUser;
    }
    
    public ArrayList<User> getContactList() {
        return contactList;
    }

    public void setContactList(ArrayList<User> contactList) {
        this.contactList = contactList;
    }
    
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getSendTo() {
        return sendTo;
    }

    public void setSendTo(String sendTo) {
        this.sendTo = sendTo;
    }

    public String getSendMsg() {
        return sendMsg;
    }

    public void setSendMsg(String sendMsg) {
        this.sendMsg = sendMsg;
    }
    
    public String loadMessages(){
        user = UserDAO.getCurUser();
        
        contactList = MessageDAO.getContacts(user.getId());
        if(displayUser == null && contactList != null){
                if(contactList.size() >= 1){
                    displayUser = contactList.get(0);
                    log = MessageDAO.getLog(user.getId() , displayUser.getId());
                }
        }
        
        clearUnread();
        
        return "messages";
    }
    
    public String loadMessages(int id){
        
        System.out.println("load mesage with id called");
        
        displayUser = UserDAO.getUser(id);
        
        log = MessageDAO.getLog(user.getId() , displayUser.getId());
        
        return loadMessages();
    }
    
    public String sendMessage(){
        
        User receiver = UserDAO.getUser(sendTo);
        if(receiver != null)
            if(receiver.getId() != user.getId()){
                
                MessageDAO.addMessage(user.getId() , receiver.getId() , sendMsg);
                
                sendMsg = null;
                
                return loadMessages();
            }
        return "newmessage";
        
    }
    
    public String reply(){
        sendTo = displayUser.getUsername();
        
        sendMessage();
        
        return loadMessages(displayUser.getId());
    }
    
    public int getUnread(){
        return MessageDAO.getUnread(UserDAO.getCurUser().getId());
    }
    
    public String displayMenu(){
        return "Messages ("+Integer.toString(getUnread())+")";
    }
    
    public void clearUnread(){
        MessageDAO.clearUnread(UserDAO.getCurUser().getId());
    }
}
