package com.example.clay.blackjack;

import android.graphics.drawable.Drawable;

/**
 * Created by Clay on 3/7/2015.
 */
public class Card {
    private int cardVal;
    private Drawable image;

    // default constructor
    public Card(){}

    // constructor with params
    public Card(int cardVal, Drawable image){
        this.cardVal = cardVal;
        this.image = image;
    }

    public int getCardVal() {
        return cardVal;
    }

    public Drawable getImage(){
        return image;
    }

}
