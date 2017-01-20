package com.webservices;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Matt
 */
public class PartsBean {
    
    private int id,quantity;
    private double price;
    private String partsname;

    public PartsBean(int id, int quantity, String partsname ,double price ) {
        this.id = id;
        this.quantity = quantity;
        this.partsname = partsname;
        this.price = price;
    }

    public double getPrice() {
        return price;
    }

    public int getId() {
        return id;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getPartsname() {
        return partsname;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setPartsname(String partsname) {
        this.partsname = partsname;
    }

    public void setPrice(double price) {
        this.price = price;
    }
    
    
    
}
