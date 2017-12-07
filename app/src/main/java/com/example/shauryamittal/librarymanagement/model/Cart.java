package com.example.shauryamittal.librarymanagement.model;

import java.util.ArrayList;

/**
 * Created by shauryamittal on 12/7/17.
 */

public class Cart {

    ArrayList <String> cartItems = new ArrayList<String>();

    public void City() {}

    public Cart(ArrayList <String> cartItems) {
        this.cartItems = cartItems;
    }

    public ArrayList<String> getCartItems(){
        return cartItems;
    }

}
