/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webservices;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.sql.DataSource;

/**
 *
 * @author Matt
 */
@WebService(serviceName = "OrderWebservice")
public class OrderWebservice {

    @Resource(name = "databaseConnection")
    private DataSource databaseConnection;

    /**
     * This is a sample web service operation
     */
    @WebMethod(operationName = "hello")
    public String hello(@WebParam(name = "name") String txt) {
        return "Hello " + txt + " !";
    }

    /**
     * Web service operation
     */
    HashMap<Integer, OrderBean> PartsOrder = new HashMap();

    @WebMethod(operationName = "PartsList")
    public String PartsList() {
        HashMap<Integer, PartsBean> hash = new HashMap();

        try {
            Connection connect = databaseConnection.getConnection();
            PreparedStatement stmt = connect.prepareStatement("SELECT * FROM ROOT.PARTS2");
            ResultSet set = stmt.executeQuery();
            int i = 0;
            while (set.next()) {
                hash.put(i, new PartsBean(set.getInt("ID"), set.getInt("QUANTITY"), set.getString("ITEM"), set.getDouble("PRICE")));
                i++;
            }
            connect.close();

        } catch (SQLException ex) {
            Logger.getLogger(OrderWebservice.class.getName()).log(Level.SEVERE, null, ex);
        }

        String result = "Parts list : ";
        for (int i = 0; i < hash.size(); i++) {
            PartsBean bean2 = hash.get(i);
            result += "Parts Name: " + bean2.getPartsname() + ". Parts Price: " + bean2.getPrice() + ". Quantity: " + bean2.getQuantity();
        }
        return result;
    }

    public int add(String partsName, int partsQuantity) {
        HashMap<Integer, PartsBean> hashList = new HashMap();
        try {
            Connection connect = databaseConnection.getConnection();
            PreparedStatement stmt = connect.prepareStatement("SELECT * FROM ROOT.PARTS2 WHERE ITEM=?");
            stmt.setString(1, partsName);
            ResultSet set = stmt.executeQuery();
            int i = 0;
            while (set.next()) {
                hashList.put(i, new PartsBean(set.getInt("ID"), set.getInt("QUANTITY"), set.getString("ITEM"), set.getDouble("PRICE")));
                i++;
            }
            connect.close();

        } catch (SQLException ex) {
            Logger.getLogger(OrderWebservice.class.getName()).log(Level.SEVERE, null, ex);
        }

        double partsPrice = 0.0;
        for (int i = 0; i < hashList.size(); i++) {
            PartsBean bean = hashList.get(i);
            partsPrice = bean.getPrice();
        }
        PartsOrder.put(PartsOrder.size() + 1, new OrderBean(partsName, partsQuantity, partsPrice));
        return PartsOrder.size();
        //Testing 
        //OrderBean test = PartsOrder.get(1);
        //String firstInList = "" + test.getName() + " : " + test.getQuantity() + " X " + test.getPrice();
        //return firstInList;
    }

    public void remove(int index) {
        String result = "";
        OrderBean orderNo = PartsOrder.get(index);
        orderNo.setQuantity(0);
        result += "" + orderNo.getName() + " : " + orderNo.getQuantity() + " X " + orderNo.getPrice();
    }

    public String check(int index) {
        String result = "";
        OrderBean orderNo = PartsOrder.get(index);
        result += "" + orderNo.getName() + " : " + orderNo.getQuantity() + " X " + orderNo.getPrice() + "\n";
        return result;
    }
    
    public boolean checkDatabase(String dataName){
        boolean check = false;
        try {
            
            Connection connect = databaseConnection.getConnection();
            PreparedStatement stmt = connect.prepareStatement("SELECT * FROM ROOT.PARTS2 WHERE ITEM=?");
            stmt.setString(1, dataName);
            ResultSet set = stmt.executeQuery();
            check  = set.next();
            
            connect.close();

        } catch (SQLException ex) {
            Logger.getLogger(OrderWebservice.class.getName()).log(Level.SEVERE, null, ex);
        }
        return check;
}

    public String checkOrder() {
        String list = "";
        for (int i = 1; i <= PartsOrder.size(); i++) {
            OrderBean orderNo = PartsOrder.get(i);
            if(orderNo.getQuantity() != 0)
            list += "" + orderNo.getName() + " : " + orderNo.getQuantity() + " X " + orderNo.getPrice() + ", ";
        }
        return list;
    }
}
