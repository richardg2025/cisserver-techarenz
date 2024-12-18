package edu.cis.Model;

public class MenuItem {
    private String id;
    private String name;
    private String type;
    private double price;
    private String description;
    private int amountAvailable = 0;

    public MenuItem(String name, String type, String id, double price, String description) {
        this.name = name;
        this.type = type;
        this.id = id;
        this.price = price;
        this.description = description;
    }

    public MenuItem() {

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

    public double getPrice() {
        return price;
    }

    public String getDescription() {    
        return description;
    }                    

    public int getAmountAvailable() {    
        return amountAvailable;
    }

    public void setAmountAvailable(int amountAvailable) {
        this.amountAvailable = amountAvailable; // Fix: Properly set the amountAvailable
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

    public void setPrice(double price) {
        this.price = price;
    }

    public void minusAmountAvailable() {
        this.amountAvailable -= 1;
    }

    public void setDescription(String description) {    
        this.description = description;
    }

    @Override
    public String toString() {
        return "MenuItem{name='" + name + "', description='" + description + "', price=" + price +
                ", id='" + id + "', amountAvailable=" + amountAvailable + ", type='" + type + "'}";
    }


}
