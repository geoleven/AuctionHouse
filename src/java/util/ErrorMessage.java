/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

/**
 *
 * @author george
 */
public class ErrorMessage {
    
    public static void msg( String component , String msg){
        FacesContext.getCurrentInstance().addMessage(component,  new FacesMessage(msg));
    }
    
}
