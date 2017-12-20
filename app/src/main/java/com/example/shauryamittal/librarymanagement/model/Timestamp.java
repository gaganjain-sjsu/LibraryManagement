package com.example.shauryamittal.librarymanagement.model;

/**
 * Created by gaganjain on 12/20/17.
 */

public class Timestamp {

    private String date;
    private String uid;


    public Timestamp(String date, String uid) {
        this.date = date;
        this.uid = uid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUID() {
        return uid;
    }

    public void setCreatedBy(String uid) {
        this.uid = uid;
    }
}
