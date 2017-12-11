package com.example.shauryamittal.librarymanagement.model;

import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Created by shauryamittal on 12/5/17.
 */

public class CurrentUser {

    public static String NAME, SJSU_ID, EMAIL, ROLE, UID;
    static DocumentReference user;
    static final String USER_COLLECTION = "users";
    static DocumentSnapshot user_document;
    private static final String UID_KEY = "uid";
    private static final String FULLNAME_KEY = "fullname";
    private static final String EMAIL_KEY = "email";
    private static final String SJSU_ID_KEY = "sjsuId";
    private static final String ROLE_KEY = "role";

    public static void setCurrentUser(DocumentSnapshot documentSnapshot){

        user_document = documentSnapshot;
        UID = documentSnapshot.getString(UID_KEY);
        NAME = documentSnapshot.getString(FULLNAME_KEY);
        EMAIL = documentSnapshot.getString(EMAIL_KEY);
        SJSU_ID = documentSnapshot.getString(SJSU_ID_KEY);
        ROLE = documentSnapshot.getString(ROLE_KEY);
        Log.v("GOT THE DATA", NAME);

    }

    public static void destroyCurrentUser(){
        NAME = null;
        EMAIL = null;
        SJSU_ID = null;
        ROLE = null;
        UID = null;
    }

}
