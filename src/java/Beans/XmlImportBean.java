/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Beans;

import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import xmlHandle.xmlHandle;

/**
 *
 * @author george
 */
@ManagedBean (name = "xmlimport")
@SessionScoped
public class XmlImportBean implements Serializable {
    
    String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
    
    public String importxml() throws Exception{
        
        xmlHandle.xmlImportAll(path);
    
        return "importxml";
    
    }
    
    public String exportxml() throws Exception{
        
        xmlHandle.xmlExport(path);
        
        return "importxml";
    }
    
}
