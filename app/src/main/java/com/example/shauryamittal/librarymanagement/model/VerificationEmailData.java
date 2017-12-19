package com.example.shauryamittal.librarymanagement.model;

/**
 * Created by shauryamittal on 12/19/17.
 */

public class VerificationEmailData {

    String mBookName;
    String mDueDate;
    String mCheckoutDate;
    String mCheckoutTime;

    public VerificationEmailData(String bookName, String dueDate, String checkoutDate, String checkoutTime) {
        mBookName = bookName;
        mDueDate = dueDate;
        mCheckoutDate = checkoutDate;
        mCheckoutTime = checkoutTime;
    }

    public String getBookName() {
        return mBookName;
    }

    public void setBookName(String bookName) {
        mBookName = bookName;
    }

    public String getDueDate() {
        return mDueDate;
    }

    public void setDueDate(String dueDate) {
        mDueDate = dueDate;
    }

    public String getCheckoutDate() {
        return mCheckoutDate;
    }

    public void setCheckoutDate(String checkoutDate) {
        mCheckoutDate = checkoutDate;
    }

    public String getCheckoutTime() {
        return mCheckoutTime;
    }

    public void setCheckoutTime(String checkoutTime) {
        mCheckoutTime = checkoutTime;
    }
}
