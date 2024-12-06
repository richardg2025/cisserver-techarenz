/*
 * File: CIServer.java
 * ------------------------------
 * When it is finished, this program will implement a basic
 * ecommerce network management server.  Remember to update this comment!
 */

package edu.cis.Controller;

import java.util.ArrayList;

import javax.swing.plaf.metal.MetalBorders.MenuItemBorder;

import acm.program.*;
import edu.cis.Model.CISConstants;
import edu.cis.Model.Request;
import edu.cis.Model.SimpleServerListener;
import edu.cis.Utils.SimpleServer;

import java.util.Objects;


public class CIServer extends ConsoleProgram
        implements SimpleServerListener
{

    /* The internet port to listen to requests on */
    private static final int PORT = 8000;

    /* The server object. All you need to do is start it */
    private SimpleServer server = new SimpleServer(this, PORT);


    private ArrayList<User> users = new ArrayList<User>();
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
        else if(request.getCommand().equals(CISConstants.GET_MENU)){
            return getMenu(request);
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

    public String createUser(request request){
        String userName = request.getParam(CISConstants.USER_NAME_PARAM);
        String yearLevel = request.getParam(CISConstants.YEAR_LEVEL_PARAM);
        String userID = request.getParam(CISConstants.USER_ID_PARAM);

        if(name == null || yearLevel == null || userID == null){
            return CISConstants.PARAM_MISSING_ERR;
        }

        CISUser user = new CISUser(userName, yearLevel, userID);
        users.add(user);
        return CISConstants.SUCCESS;
    }

    public String addMenuItem(request request){
        String menuItemName = request.getParam(CISConstants.ITEM_NAME_PARAM);
        String menuItemType = request.getParam(CISConstants.ITEM_TYPE_PARAM);
        String menuItemID = request.getParam(CISConstants.ITEM_ID_PARAM);
        String price = request.getParam(CISConstants.PRICE_PARAM);
        String description = request.getParam(CISConstants.DESC_PARAM); 

        if(menuItemName == null || menuItemType == null || menuItemID == null || price == null || description == null){
            return CISConstants.PARAM_MISSING_ERR;
        }

        MenuItem menuItem = new MenuItem(menuItemName, menuItemType, menuItemID, price, description);
        menuObj.addMenuItem(menuItem);
        return CISConstants.SUCCESS;
    }

    public String placeOrder(request request){
        String userID = request.getParam(CISConstants.USER_ID_PARAM);
        String orderID = request.getParam(CISConstants.ORDER_ID_PARAM);
        String orderType = request.getParam(CISConstants.ORDER_TYPE_PARAM);
        String menuItemID = request.getParam(CISConstants.ITEM_ID_PARAM);

        if(orderID == null){
            return CISConstants.ORDER_INVALID_ERR;
        }
        if(menuObj.size() == 0){
            return CISConstants.EMPTY_MENU_ERR;
        }

        CISUser user = new CISUser();
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

        MenuItem menuItem = menuObj.getItem(menuItemID);
        boolean menuItemExists = false;
        for (MenuItem m : menu) {
            if(m.getItemID().equals(menuItemID)){
                menuItemExists = true;
                break;
            }
            //check if menu item is sold out or not using get amount avalible
            if(m.getAmountAvailable() < 1 && m.getItemID().equals(menuItemID)){
                return CISConstants.SOLD_OUT_ERR;
            }
        }
        if(!menuItemExists){
            return CISConstants.INVALID_MENU_ITEM_ERR;
        }
        if(item.getPrice() > user.getMoney()){
            return CISConstants.USER_BROKE_ERR;
        }

        Order order = new Order(orderID, orderType, menuItem, user);
        item.setAmountAvailable(item.getAmountAvailable() - 1);
        user.addOrder(order);
        user.spendMoney(item.getPrice());

        return CISConstants.SUCCESS;

    }

    public String deleteOrder(request request){
        String userID = request.getParam(CISConstants.USER_ID_PARAM);
        String orderID = request.getParam(CISConstants.ORDER_ID_PARAM);

        CISUser user = new CISUser();
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

    public String getOrder (request request){
        String userID = request.getParam(CISConstants.USER_ID_PARAM);
        CISUser user = new CISUser();
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
            if(o.getOrderID().equals(orderID)){
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

    public String getUser (request request){
        String userID = request.getParam(CISConstants.USER_ID_PARAM);
        CISUser user = new CISUser();
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

    public String getItem (request request){
        String menuItemID = request.getParam(CISConstants.ITEM_ID_PARAM);
        MenuItem menuItem = new MenuItem();
        boolean menuItemExists = false;
        for (MenuItem m : menu) {
            if(m.getItemID().equals(menuItemID)){
                menuItem = m;
                return menuItem.toString();
            }
        }
        if(!menuItemExists){
            return CISConstants.INVALID_MENU_ITEM_ERR;
        }
    }
        
    public String getCart (request request){ 
        String userID = request.getParam(CISConstants.USER_ID_PARAM);
        CISUser user = new CISUser();
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

        if(user.getOrders().size() == 0){
            return CISConstants.EMPTY_CART_ERR;
        }   
        
        return user.getOrders().toString();
    }

    public static void main(String[] args)
    {
        CIServer f = new CIServer();
        f.start(args);
    }
}
