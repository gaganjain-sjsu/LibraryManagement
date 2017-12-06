package com.example.shauryamittal.librarymanagement.model;

/**
 * Created by shauryamittal on 12/5/17.
 */

public class User {

    String mName, mEmail, mSjsuId, mUid;

    public User(String name, String email, String sjsuId, String uid){
        mName = name;
        mEmail = email;
        mSjsuId = sjsuId;
        mUid = uid;
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
}
