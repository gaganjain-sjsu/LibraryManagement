package com.example.shauryamittal.librarymanagement.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Harshit on 12/3/17.
 */

public class Book implements Serializable {

    private String author="";
    private String title="";
    private String callNumber="";
    private String publisher="";
    private int yearOfPub=0;
    private String location="";
    private int noOfCopy=0;
    private int noOfCheckedOutCopy=0;
    private String status="";
    private String keywords="";
    private String librarianId="";
    private String bookId="";

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCallNumber() {
        return callNumber;
    }

    public void setCallNumber(String callNumber) {
        this.callNumber = callNumber;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }


    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getYearOfPub() {
        return yearOfPub;
    }

    public void setYearOfPub(int yearOfPub) {
        this.yearOfPub = yearOfPub;
    }

    public int getNoOfCopy() {
        return noOfCopy;
    }

    public void setNoOfCopy(int noOfCopy) {
        this.noOfCopy = noOfCopy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getLibrarianId() {
        return librarianId;
    }

    public void setLibrarianId(String librarianId) {
        this.librarianId = librarianId;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public int getNoOfCheckedOutCopy() {
        return noOfCheckedOutCopy;
    }

    public void setNoOfCheckedOutCopy(int noOfCheckedOutCopy) {
        this.noOfCheckedOutCopy = noOfCheckedOutCopy;
    }
}
