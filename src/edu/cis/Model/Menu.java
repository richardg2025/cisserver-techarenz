package edu.cis.Model;

public class Menu {
    private ArrayList<MenuItem> menu = new ArrayList<MenuItem>();
    private String adminID;

    public Menu(ArrayList<MenuItem> menu) {
        this.menu = menu;
    }

    public ArrayList<MenuItem> getMenu() {
        return menu;
    }

    public void setMenu(ArrayList<MenuItem> menu) {
        this.menu = menu;
    }

    public String getAdminID() {
        return adminID;
    }

    public void setAdminID(String adminID) {
        this.adminID = adminID;
    }

    public String toString() {
        return "Menu{" + "menu=" + menu + ", adminID=" + adminID + '}';
    }
}
