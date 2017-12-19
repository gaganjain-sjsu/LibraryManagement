package com.example.shauryamittal.librarymanagement.model;

import java.io.Serializable;

/**
 * Created by Harshit on 12/19/17.
 */

public class ClearedWaitlist implements Serializable {
private String uid;
private String bookId;
private String clearDate;
private String lastDateToAcceptBook;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getClearDate() {
        return clearDate;
    }

    public void setClearDate(String clearDate) {
        this.clearDate = clearDate;
    }

    public String getLastDateToAcceptBook() {
        return lastDateToAcceptBook;
    }

    public void setLastDateToAcceptBook(String lastDateToAcceptBook) {
        this.lastDateToAcceptBook = lastDateToAcceptBook;
    }

}
