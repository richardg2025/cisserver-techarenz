package edu.cis.Model;

import java.util.ArrayList;

public class CISUser {
    private String userID;
    private String name;
    private String yearLevel;
    private ArrayList<Order> orders;
    private double money;

    public CISUser(String userID, String name, String yearLevel) {
        this.userID = userID;
        this.name = name;
        this.yearLevel = yearLevel;
        this.orders = new ArrayList<Order>();
        this.money = 50;
    }

    public CISUser() {

    }

    public String getUserID() {
        return userID;
    }

    public String getName() {
        return name;
    }    

    public String getYearLevel() {    
        return yearLevel;
    }    

    public ArrayList<Order> getOrders() {    
        return orders;
    }        

    public double getMoney() {    
        return money;
    }        

    public void setUserID(String userID) {    
        this.userID = userID;
    }        

    public void setName(String name) {    
        this.name = name;
    }        

    public void setYearLevel(String yearLevel) {    
        this.yearLevel = yearLevel;
    }        

    public void setOrders(ArrayList<Order> orders) {    
        this.orders = orders;        
    }                                    

    public void setMoney(double money) {    
        this.money = money;        
    }           

    public String toString() {
        return "CISUser{" + "userID=" + userID + ", name=" + name + ", yearLevel=" + yearLevel + ", orders=" + orders + ", money=" + money + '}';
    }

    public void spend(double price) {
        this.money -= price;
    }
}
