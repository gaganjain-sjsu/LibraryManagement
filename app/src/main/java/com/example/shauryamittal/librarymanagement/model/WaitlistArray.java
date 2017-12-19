package com.example.shauryamittal.librarymanagement.model;

import java.util.ArrayList;

/**
 * Created by shauryamittal on 12/18/17.
 */

public class WaitlistArray {

    public String title = "waitListedBooks";
    public ArrayList<String> bookIds;

    public WaitlistArray(ArrayList<String> bookIds){
        this.bookIds = bookIds;
    }

}
