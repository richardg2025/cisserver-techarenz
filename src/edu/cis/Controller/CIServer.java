/*
 * File: CIServer.java
 * ------------------------------
 * When it is finished, this program will implement a basic
 * ecommerce network management server.  Remember to update this comment!
 */

package edu.cis.Controller;

import java.util.ArrayList;

import acm.program.*;
import edu.cis.Model.*;

import edu.cis.Model.CISConstants;
import edu.cis.Model.Request;
import edu.cis.Model.SimpleServerListener;
import edu.cis.Utils.SimpleServer;


public class CIServer extends ConsoleProgram
        implements SimpleServerListener
{

    /* The internet port to listen to requests on */
    private static final int PORT = 8000;

    /* The server object. All you need to do is start it */
    private SimpleServer server = new SimpleServer(this, PORT);


    private ArrayList<CISUser> users = new ArrayList<CISUser>();
    private ArrayList<MenuItem> menu = new ArrayList<MenuItem>();
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

        // your code here.
        if (request.getCommand().equals(CISConstants.PING))
        {
            final String PING_MSG = "Hello, internet";

            //println is used instead of System.out.println to print to the server GUI
            println("   => " + PING_MSG);
            return PING_MSG;
        }

        if(request.getCommand().equals(CISConstants.CREATE_USER)){
            return createUser(request);
        }
        else if(request.getCommand().equals(CISConstants.PLACE_ORDER)){
            return placeOrder(request);
        }
        else if(request.getCommand().equals(CISConstants.DELETE_ORDER)){
            return deleteOrder(request);
        }
        else if(request.getCommand().equals(CISConstants.GET_CART)){
            return getCart(request);
        }
        else if(request.getCommand().equals(CISConstants.ADD_MENU_ITEM)){
            return addMenuItem(request);
        }
        else if(request.getCommand().equals(CISConstants.GET_ORDER)){
            return getOrder(request);
        }
        else if(request.getCommand().equals(CISConstants.GET_USER)){
            return getUser(request);
        }

        return "Error: Unknown command " + cmd + ".";
    }

    public String createUser(Request request){
        String userName = request.getParam(CISConstants.USER_NAME_PARAM);
        String yearLevel = request.getParam(CISConstants.YEAR_LEVEL_PARAM);
        String userID = request.getParam(CISConstants.USER_ID_PARAM);

        if(userName == null || yearLevel == null || userID == null){
            return CISConstants.PARAM_MISSING_ERR;
        }

        CISUser user = new CISUser(userName, yearLevel, userID);
        users.add(user);
        return CISConstants.SUCCESS;
    }

    public String addMenuItem(Request request){
        String menuItemName = request.getParam(CISConstants.ITEM_NAME_PARAM);
        String menuItemType = request.getParam(CISConstants.ITEM_TYPE_PARAM);
        String menuItemID = request.getParam(CISConstants.ITEM_ID_PARAM);
        double price = Double.parseDouble(request.getParam(CISConstants.PRICE_PARAM));
        String description = request.getParam(CISConstants.DESC_PARAM); 

        if(menuItemName == null || menuItemType == null || menuItemID == null || price == 0 || description == null){
            return CISConstants.PARAM_MISSING_ERR;
        }

        MenuItem menuItem = new MenuItem(menuItemName, menuItemType, menuItemID, price, description);
        menuObj.addMenuItem(menuItem);
        return CISConstants.SUCCESS;
    }

    public String placeOrder(Request request){
        String userID = request.getParam(CISConstants.USER_ID_PARAM);
        String orderID = request.getParam(CISConstants.ORDER_ID_PARAM);
        String orderType = request.getParam(CISConstants.ORDER_TYPE_PARAM);
        String menuItemID = request.getParam(CISConstants.ITEM_ID_PARAM);
        String name = request.getParam(CISConstants.USER_NAME_PARAM);
        String yearLevel = request.getParam(CISConstants.YEAR_LEVEL_PARAM);

        if(orderID == null){
            return CISConstants.ORDER_INVALID_ERR;
        }
        if(menu.size() == 0){
            return CISConstants.EMPTY_MENU_ERR;
        }

        CISUser user = new CISUser(userID, name, yearLevel);
        boolean userExists = false;
        for (CISUser u : users) {
            if(u.getUserID().equals(userID)){
                user = u;
                userExists = true;
                break;
            }
        }
        if(!userExists){
            return CISConstants.USER_INVALID_ERR;
        }
        
        boolean orderExists = false;
        for (Order o : user.getOrders()) {
            if(o.getOrderID().equals(orderID)){
                orderExists = true;
                break;
            }
        }
        if(orderExists){
            return CISConstants.DUP_ORDER_ERR;
        }

        for (CISUser u : users) {
            if(u.getUserID().equals(userID)){ 
                continue;
            }
            for (Order o : u.getOrders()) {
                if(o.getOrderID().equals(orderID)){
                    return CISConstants.ORDER_INVALID_ERR;
                }
            }
        }

        MenuItem item = new MenuItem();
        boolean menuItemExists = false;
        for (MenuItem m : menu) {
            if(m.getId().equals(menuItemID)){
                menuItemExists = true;
                break;
            }
            //check if menu item is sold out or not using get amount avalible
            if(m.getAmountAvailable() < 1 && m.getId().equals(menuItemID)){
                return CISConstants.SOLD_OUT_ERR;
            }
        }
        if(!menuItemExists){
            return CISConstants.INVALID_MENU_ITEM_ERR;
        }
        if(item.getPrice() > user.getMoney()){
            return CISConstants.USER_BROKE_ERR;
        }

        Order order = new Order(orderID, orderType, menuItemID);
        item.setAmountAvailable();
        user.getOrders().add(order);
        user.spend(item.getPrice());

        return CISConstants.SUCCESS;

    }

    public String deleteOrder(Request request){
        String userID = request.getParam(CISConstants.USER_ID_PARAM);
        String orderID = request.getParam(CISConstants.ORDER_ID_PARAM);
        String name = request.getParam(CISConstants.USER_NAME_PARAM);
        String yearLevel = request.getParam(CISConstants.YEAR_LEVEL_PARAM);

        CISUser user = new CISUser(userID, name, yearLevel);
        boolean userExists = false;
        for (CISUser u : users) {
            if(u.getUserID().equals(userID)){
                user = u;
                userExists = true;
                break;
            }
        }
        if(!userExists){
            return CISConstants.USER_INVALID_ERR;
        }
        
        boolean orderExists = false;
        for (Order o : user.getOrders()) {
            if(o.getOrderID().equals(orderID)){
                orderExists = true;
                user.getOrders().remove(o);
                break;
            }
        }
        if(!orderExists){
            return CISConstants.ORDER_INVALID_ERR;
        }

        return CISConstants.SUCCESS;
    }   

    public String getOrder (Request request){
        String userID = request.getParam(CISConstants.USER_ID_PARAM);
        String name = request.getParam(CISConstants.USER_NAME_PARAM);
        String yearLevel = request.getParam(CISConstants.YEAR_LEVEL_PARAM);

        CISUser user = new CISUser(userID, name, yearLevel);
        boolean userExists = false;
        for (CISUser u : users) {
            if(u.getUserID().equals(userID)){
                user = u;
                userExists = true;
                break;
            }
        }
        if(!userExists){
            return CISConstants.USER_INVALID_ERR;    
        }    

        Order order = new Order();
        boolean orderExists = false;
        for (Order o : user.getOrders()) {
            if(o.getOrderID().equals(userID)){
                order = o;
                orderExists = true;
                break;
            }
        }
        if(!orderExists){
            return CISConstants.ORDER_INVALID_ERR;
        }
        return order.toString();
    }

    public String getUser (Request request){
        String userID = request.getParam(CISConstants.USER_ID_PARAM);
        String name = request.getParam(CISConstants.USER_NAME_PARAM);
        String yearLevel = request.getParam(CISConstants.YEAR_LEVEL_PARAM);

        CISUser user = new CISUser(userID, name, yearLevel);
        boolean userExists = false;
        for (CISUser u : users) {
            if(u.getUserID().equals(userID)){
                user = u;
                userExists = true;
                break;
            }
        }
        if(!userExists){
            return CISConstants.USER_INVALID_ERR;    
        }    
        return user.toString();
    }

    public String getItem (Request request){
        String menuItemID = request.getParam(CISConstants.ITEM_ID_PARAM);
        MenuItem menuItem = new MenuItem();
        boolean menuItemExists = false;
        for (MenuItem m : menu) {
            if(m.getId().equals(menuItemID)){
                menuItem = m;
                return menuItem.toString();
            }
        }
        if(!menuItemExists){
            return CISConstants.INVALID_MENU_ITEM_ERR;
        }

        String msg = menuItem.toString();
        return msg;
    }
        
    public String getCart (Request request){
        String userID = request.getParam(CISConstants.USER_ID_PARAM);
        String name = request.getParam(CISConstants.USER_NAME_PARAM);
        String yearLevel = request.getParam(CISConstants.YEAR_LEVEL_PARAM);

        CISUser user = new CISUser(userID, name, yearLevel);
        boolean userExists = false;
        for (CISUser u : users) {
            if(u.getUserID().equals(userID)){
                user = u;
                userExists = true;
                break;
            }
        }
        if(!userExists){
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
