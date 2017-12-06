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

    static FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static void createUser(User user){

        // Create a new user with a first and last name
        Map<String, Object> newUser = new HashMap<>();

        newUser.put("uid", user.getUid());
        newUser.put("fullname", user.getName());
        newUser.put("email", user.getEmail());
        newUser.put("sjsuId", user.getSjsuId());

        // Add a new document with a generated ID
        db.collection("users")
                .add(newUser)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });

    }

}
