/*
 * File: CIServer.java
 * ------------------------------
 * This program implements a basic ecommerce network management server.
 */

package edu.cis.Controller;

import java.util.ArrayList;

import acm.program.*;
import edu.cis.Model.*;
import edu.cis.Utils.SimpleServer;

public class CIServer extends ConsoleProgram
        implements SimpleServerListener
{

    /* The internet port to listen to requests on */
    private static final int PORT = 8000;

    /* The server object. All you need to do is start it */
    private SimpleServer server = new SimpleServer(this, PORT);

    private ArrayList<CISUser> users = new ArrayList<>();
    private ArrayList<MenuItem> menu = new ArrayList<>();
    private Menu menuObj = new Menu(menu);

    /**
     * Starts the server running so that when a program sends
     * a request to this server, the method requestMade is
     * called.
     */
    public void run()
    {
        println("Starting server on port " + PORT);
        server.start();
    }

    /**
     * When a request is sent to this server, this method is
     * called. It must return a String.
     *
     * @param request a Request object built by SimpleServer from an
     *                incoming network request by the client
     */
    public String requestMade(Request request)
    {
        String cmd = request.getCommand();
        println(request.toString());

        switch (cmd) {
            case CISConstants.PING:
                final String PING_MSG = "Hello, internet";
                println("   => " + PING_MSG);
                return PING_MSG;

            case CISConstants.CREATE_USER:
                return createUser(request);

            case CISConstants.PLACE_ORDER:
                return placeOrder(request);

            case CISConstants.DELETE_ORDER:
                return deleteOrder(request);

            case CISConstants.GET_CART:
                return getCart(request);

            case CISConstants.ADD_MENU_ITEM:
                return addMenuItem(request);

            case CISConstants.GET_ORDER:
                return getOrder(request);

            case CISConstants.GET_USER:
                return getUser(request);

            case CISConstants.GET_ITEM: // Fix: Handle the "getItem" command
                return getItem(request);

            default:
                return "Error: Unknown command " + cmd + ".";
        }
    }

    public String createUser(Request request){
        String userID = request.getParam(CISConstants.USER_ID_PARAM);
        String userName = request.getParam(CISConstants.USER_NAME_PARAM);
        String yearLevel = request.getParam(CISConstants.YEAR_LEVEL_PARAM);

        if(userID == null || userName == null || yearLevel == null){
            return CISConstants.PARAM_MISSING_ERR;
        }

        for (CISUser user : users) {
            if (user.getUserID().equals(userID)) {
                return CISConstants.DUP_USER_ERR;
            }
        }

        CISUser user = new CISUser(userID, userName, yearLevel);
        users.add(user);
        return CISConstants.SUCCESS;
    }

    public String addMenuItem(Request request){
        String menuItemName = request.getParam(CISConstants.ITEM_NAME_PARAM);
        String menuItemType = request.getParam(CISConstants.ITEM_TYPE_PARAM);
        String menuItemID = request.getParam(CISConstants.ITEM_ID_PARAM);
        double price;
        try {
            price = Double.parseDouble(request.getParam(CISConstants.PRICE_PARAM));
        } catch (NumberFormatException e) {
            return CISConstants.PARAM_MISSING_ERR;
        }
        String description = request.getParam(CISConstants.DESC_PARAM);

        if(menuItemName == null || menuItemType == null || menuItemID == null || price == 0 || description == null){
            return CISConstants.PARAM_MISSING_ERR;
        }

        MenuItem menuItem = new MenuItem(menuItemName, menuItemType, menuItemID, price, description);
        menuItem.setAmountAvailable(10); // Initialize amountAvailable to 10
        menuObj.addMenuItem(menuItem);
        return CISConstants.SUCCESS;
    }

    public String placeOrder(Request request) {
        String userID = request.getParam(CISConstants.USER_ID_PARAM);
        String orderID = request.getParam(CISConstants.ORDER_ID_PARAM);
        String menuItemID = request.getParam(CISConstants.ITEM_ID_PARAM);
        String orderType = request.getParam(CISConstants.ORDER_TYPE_PARAM);

        if (userID == null || orderID == null || menuItemID == null || orderType == null) {
            return CISConstants.PARAM_MISSING_ERR;
        }

        // Find the user
        CISUser user = null;
        for (CISUser u : users) {
            if (u.getUserID().equals(userID)) {
                user = u;
                break;
            }
        }

        if (user == null) {
            return CISConstants.USER_INVALID_ERR;
        }

        // Find the menu item
        MenuItem item = null;
        for (MenuItem m : menu) {
            if (m.getId().equals(menuItemID)) {
                item = m;
                break;
            }
        }

        if (item == null) {
            return CISConstants.INVALID_MENU_ITEM_ERR;
        }

        // Check inventory
        if (item.getAmountAvailable() < 1) {
            return CISConstants.SOLD_OUT_ERR;
        }

        // Check if the user has enough money
        if (user.getMoney() < item.getPrice()) {
            return CISConstants.USER_BROKE_ERR;
        }

        // Check if the user already placed the same order
        for (Order o : user.getOrders()) {
            if (o.getOrderID().equals(orderID)) {
                return CISConstants.DUP_ORDER_ERR;
            }
        }

        // *** FIXED ***
        // Decrement `amountAvailable` only WHEN ORDER IS SUCCESSFULLY PLACED
        item.minusAmountAvailable(); // Reduces amountAvailable by 1
        menuObj.updateMenuItemAmountAvailable(menuItemID, item.getAmountAvailable());

        // Create the order and add to the user's list of orders
        Order order = new Order(menuItemID, orderID, orderType);
        user.getOrders().add(order);
        user.spend(item.getPrice()); // Deduct price from the user's balance

        return CISConstants.SUCCESS;
    }

    public String deleteOrder(Request request) {
        String userID = request.getParam(CISConstants.USER_ID_PARAM);
        String orderID = request.getParam(CISConstants.ORDER_ID_PARAM);

        if (userID == null || orderID == null) {
            return CISConstants.PARAM_MISSING_ERR;
        }

        // Find the user
        CISUser user = null;
        for (CISUser u : users) {
            if (u.getUserID().equals(userID)) {
                user = u;
                break;
            }
        }

        if (user == null) {
            return CISConstants.USER_INVALID_ERR;
        }

        // Find the order to remove
        Order orderToRemove = null;
        for (Order o : user.getOrders()) {
            if (o.getOrderID().equals(orderID)) {
                orderToRemove = o;
                break;
            }
        }

        if (orderToRemove == null) {
            return CISConstants.ORDER_INVALID_ERR;
        }

        // Find the menu item and increment its amountAvailable (but cap it at 8 max)
        for (MenuItem m : menu) {
            if (m.getId().equals(orderToRemove.getItemID())) {
                // FIX: Only increment if amountAvailable < 8 to avoid over-incrementing
                if (m.getAmountAvailable() < 8) {
                    m.setAmountAvailable(m.getAmountAvailable() + 1);
                    menuObj.updateMenuItemAmountAvailable(orderToRemove.getItemID(), m.getAmountAvailable());
                }
                break;
            }
        }

        // Remove the order from the user's orders
        user.getOrders().remove(orderToRemove);

        return CISConstants.SUCCESS;
    }
    public String getOrder(Request request){
        String userID = request.getParam(CISConstants.USER_ID_PARAM);
        String orderID = request.getParam(CISConstants.ORDER_ID_PARAM);

        if (userID == null || orderID == null) {
            return CISConstants.PARAM_MISSING_ERR;
        }

        CISUser user = null;
        for (CISUser u : users) {
            if (u.getUserID().equals(userID)) {
                user = u;
                break;
            }
        }

        if (user == null) {
            return CISConstants.USER_INVALID_ERR;
        }

        for (Order o : user.getOrders()) {
            if (o.getOrderID().equals(orderID)) {
                return o.toString();
            }
        }

        return CISConstants.ORDER_INVALID_ERR;
    }

    public String getUser(Request request){
        String userID = request.getParam(CISConstants.USER_ID_PARAM);

        if (userID == null) {
            return CISConstants.PARAM_MISSING_ERR;
        }

        for (CISUser user : users) {
            if (user.getUserID().equals(userID)) {
                return user.toString();
            }
        }

        return CISConstants.USER_INVALID_ERR;
    }

    public String getItem(Request request) {
        // Get the menuItemID from the request parameters
        String menuItemID = request.getParam(CISConstants.ITEM_ID_PARAM);

        if (menuItemID == null) {
            return CISConstants.PARAM_MISSING_ERR; // Return error if the parameter is missing
        }

        // Find the matching MenuItem in the menu list
        for (MenuItem m : menu) {
            if (m.getId().equals(menuItemID)) {
                // Return the current state of the MenuItem (toString represents the state correctly)
                return m.toString();
            }
        }

        // Return error if no matching item is found
        return CISConstants.INVALID_MENU_ITEM_ERR;
    }

    public String getCart(Request request){
        String userID = request.getParam(CISConstants.USER_ID_PARAM);

        if (userID == null) {
            return CISConstants.PARAM_MISSING_ERR;
        }

        CISUser user = null;
        for (CISUser u : users) {
            if (u.getUserID().equals(userID)) {
                user = u;
                break;
            }
        }

        if (user == null) {
            return CISConstants.USER_INVALID_ERR;
        }

        return user.getOrders().toString();
    }

    public static void main(String[] args)
    {
        CIServer f = new CIServer();
        f.start(args);
    }
}

//yay