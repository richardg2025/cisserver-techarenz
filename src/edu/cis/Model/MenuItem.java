package edu.cis.Model;

public class MenuItem {
    private String id;
    private String name;
    private String type;
    private String price;
    private String description;
    private int amountAvailable = 0;

    public MenuItem(String name, String type, String id, String price, String description) {
        this.name = name;
        this.type = type;
        this.id = id;
        this.price = price;
        this.description = description;
    }

    public String getName() {    
        return name;
    }    

    public String getType() {    
        return type;
    }    

    public String getId() {    
        return id;
    }    

    public String getPrice() {    
        return price;
    }    

    public String getDescription() {    
        return description;
    }                    

    public int getAmountAvailable() {    
        return amountAvailable;
    }    

    public void setAmountAvailable(int amountAvailable) {    
        this.amountAvailable = amountAvailable;
    }               

    public void setName(String name) {    
        this.name = name;
    }    

    public void setType(String type) {    
        this.type = type;
    }    

    public void setId(String id) {    
        this.id = id;
    }    

    public void setPrice(String price) {    
        this.price = price;
    }    

    public void setDescription(String description) {    
        this.description = description;
    }   

    public String toString() {
        return "Name: " + name + "\nType: " + type + "\nID: " + id + "\nPrice: " + price + "\nDescription: " + description + "\nAmount Available: " + amountAvailable;
    }


}
