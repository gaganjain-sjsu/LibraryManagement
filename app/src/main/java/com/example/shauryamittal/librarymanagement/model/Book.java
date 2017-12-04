package com.example.shauryamittal.librarymanagement.model;

/**
 * Created by Harshit on 12/3/17.
 */

public class Book {
    private String author;
    private String title;
    private String callNumber;
    private String publisher;
    private String yearOfPub;
    private String location;
    private String noOfCopy;
    private String status;
    private String keywords;


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

    public String getYearOfPub() {
        return yearOfPub;
    }

    public void setYearOfPub(String yearOfPub) {
        this.yearOfPub = yearOfPub;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getNoOfCopy() {
        return noOfCopy;
    }

    public void setNoOfCopy(String noOfCopy) {
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
}
