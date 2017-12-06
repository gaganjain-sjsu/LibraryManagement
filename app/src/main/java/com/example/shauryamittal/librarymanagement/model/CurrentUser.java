package com.example.shauryamittal.librarymanagement.model;

import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by shauryamittal on 12/5/17.
 */

public class CurrentUser {

    static String NAME, SJSU_ID, EMAIL, ROLE, UID;

    public static void setCurrentUser(){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null){
            EMAIL = user.getEmail();
            UID = user.getUid();
        }
        else {
            Log.e("USER PROFILE: ", "Unable to load user profile");
        }

    }

}
