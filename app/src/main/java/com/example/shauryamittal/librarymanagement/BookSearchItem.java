package com.example.shauryamittal.librarymanagement;

import android.graphics.Bitmap;

/**
 * Created by shauryamittal on 12/20/17.
 */

public class BookSearchItem {

    private String author="";
    private String title="";
    private String bookId="";
    private int yearOfPub=0;

    public BookSearchItem(String author, String title, String bookId, int yearOfPub) {
        this.author = author;
        this.title = title;
        this.bookId = bookId;
        this.yearOfPub = yearOfPub;
    }

    private String publisher="";
    private Bitmap bookBitmap;

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

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public int getYearOfPub() {
        return yearOfPub;
    }

    public void setYearOfPub(int yearOfPub) {
        this.yearOfPub = yearOfPub;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPublisher() {
        return this.publisher;
    }

    public Bitmap getBookBitmap() {
        return bookBitmap;
    }

    public void setBookBitmap(Bitmap bookBitmap) {
        this.bookBitmap = bookBitmap;
    }

}
