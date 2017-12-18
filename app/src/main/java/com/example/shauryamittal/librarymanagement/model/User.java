package com.example.shauryamittal.librarymanagement.model;

/**
 * Created by shauryamittal on 12/5/17.
 */

public class User {

    String mName;
    String mEmail;
    String mSjsuId;
    String mUid;
    String mRole;
    String checkOutBooks="0";
    boolean emailVerified;
    String lastCheckoutDayCount;
    String lastCheckedOutDay;

//    public int getCheckOutBooks() {
//        return checkOutBooks;
//    }
//
//    public void setCheckOutBooks(int checkOutBooks) {
//        this.checkOutBooks = checkOutBooks;
//    }

    public User(String name, String email, String sjsuId, String uid, String role){
        mName = name;
        mEmail = email;
        mSjsuId = sjsuId;
        mUid = uid;
        mRole = role;
        emailVerified = false;
    }

    public String getRole() {
        return mRole;
    }

    public void setRole(String role) {
        mRole = role;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public String getSjsuId() {
        return mSjsuId;
    }

    public void setSjsuId(String sjsuId) {
        mSjsuId = sjsuId;
    }

    public String getUid() {
        return mUid;
    }

    public void setUid(String uid) {
        mUid = uid;
    }

    public boolean getEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

}
