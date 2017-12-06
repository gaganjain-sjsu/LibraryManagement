package com.example.shauryamittal.librarymanagement.model;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

/**
 * Created by shauryamittal on 12/5/17.
 */

public class DbOperations {

    private static final String UID_KEY = "uid";
    private static final String FULLNAME_KEY = "fullname";
    private static final String EMAIL_KEY = "email";
    private static final String SJSU_ID_KEY = "sjsuId";
    private static final String ROLE_KEY = "role";

    static FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static void createUser(User user){

        // Create a new user with a first and last name
        Map<String, Object> newUser = new HashMap<>();

        newUser.put(UID_KEY, user.getUid());
        newUser.put(FULLNAME_KEY, user.getName());
        newUser.put(EMAIL_KEY, user.getEmail());
        newUser.put(SJSU_ID_KEY, user.getSjsuId());
        newUser.put(ROLE_KEY, user.getRole());

        // Add a new document with a generated ID
        db.collection("users").document(user.getUid())
                .set(newUser)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });

    }

}
