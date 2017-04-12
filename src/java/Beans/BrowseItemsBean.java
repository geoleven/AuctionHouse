/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Beans;

import dataAccessObjects.ItemDAO;
import dataAccessObjects.UserDAO;
import dbClass.Item;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import util.recommendKNNCF;

/**
 *
 * @author george
 */
@ManagedBean (name = "browse")
@SessionScoped
public class BrowseItemsBean implements Serializable {
    
    String search;
    
    int page;
    int nPerPage;
    
    ArrayList<Item> results;
    
    @ManagedProperty(value = "#{itemExpire}")
    ItemStatusUpdateBean itemExpire;
    
    public BrowseItemsBean(){
        
        page = 1;
        nPerPage = 10;
        
        results = null;
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

    public ItemStatusUpdateBean getItemExpire() {
        return itemExpire;
    }

    public void setItemExpire(ItemStatusUpdateBean itemExpire) {
        this.itemExpire = itemExpire;
    }
    
    
    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public ArrayList<Item> getResults() {
        return results;
    }

    public void setResults(ArrayList<Item> results) {
        this.results = results;
    }
    
    public String displayResults(){
        page = 1;       
        updateResults();       
        return "main";
    }
    
    public String nextResults(){
        page++;
        updateResults();
        return "main";
    }
    
    public String prevResults(){
        page--;
        updateResults();
        return "main";
    }
    
    public String getRecomendations() throws Exception{
        results = new ArrayList<Item>();
        
        LinkedList<Integer> list = recommendKNNCF.getRecommendations(UserDAO.getCurUser().getId(), 5);
        
        
        if(list != null){
            for(int i = 0 ; i < list.size() ; i++){
                results.add(ItemDAO.getItem(list.get(i)));
            }
        }
        
        return "main";
    }
    
    public void updateResults(){
        
        itemExpire.updateStatus();
        
        //System.out.println(search);
        
        if(search == null || search.equals(""))
            getAllItems();
        else{
            String s = "%";
            s +=search;
            s +="%";
            results = ItemDAO.searchItems(s , nPerPage , (page - 1) * nPerPage);
        }
        
    }   
    public void getAllItems(){   
        results = ItemDAO.getAllItems(nPerPage , (page - 1) * nPerPage);
    }
    
    public boolean resultsEmpty(){
        
        if(results == null)
            return true;
        else if(results.size() == 0)
            return true;
        
        return false;
    }
    
    public boolean isLogged(){
        if(UserDAO.getCurUser() == null)
            return false;
        
        return true;
    }
    
    public String getShortDescription(Item it){
        
        if(it.getDescription() == null)
            return null;
        
        int stringLength = 300;
        
        String desc = it.getDescription();
        
        if(desc.length() > stringLength){
            
            desc = desc.substring(0, Math.min(desc.length(), stringLength));
            
            desc += "...";
            
            return desc;
        }
        
        return desc;
    }
    
}
