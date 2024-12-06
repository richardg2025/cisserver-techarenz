package edu.cis.Model;

public class Order {
    private String itemID;
    private String orderID;
    private String Type;

    public Order(String itemID, String orderID, String type) {
        this.itemID = itemID;
        this.orderID = orderID;
        Type = type;
    }

    public String getItemID() {
        return itemID;
    }    

    public String getOrderID() {    
        return orderID;
    }    

    public String getType() {    
        return Type;
    }           

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }       

    public void setType(String type) {
        Type = type;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    public String toString() {
        return "Order{" + "itemID=" + itemID + ", orderID=" + orderID + ", Type=" + Type + '}';
    }
}
