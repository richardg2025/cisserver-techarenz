package edu.cis.Model;

import java.util.ArrayList;

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

    public void addMenuItem(MenuItem menuItem) {
        this.menu.add(menuItem);
    }

    public void updateMenuItemAmountAvailable(String menuItemID, int amountAvailable) {
        for (MenuItem m : menu) {
            if (m.getId().equals(menuItemID)) {
                m.setAmountAvailable(amountAvailable);
                break;
            }
        }
    }
}
