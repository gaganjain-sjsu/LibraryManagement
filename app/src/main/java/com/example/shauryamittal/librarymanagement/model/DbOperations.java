package com.example.shauryamittal.librarymanagement.model;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.List;
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
    private static final String USER_COLLECTION = "users";
    private static final String BOOK_TITLE = "title";
    private static final String BOOK_AUTHOR = "author";
    static ArrayList<User> patrons = new ArrayList<User>();

    static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static List<User> librarians;

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

    public static void addBook(Book book){
        db.collection("books").add(book);

    }
    public static void updateBook(Book book) {
       // db.collection("books").document(book.getBookId()).set(book);
        if (book.getBookId() != null) {
            db.collection("books").document(book.getBookId())
                    .set(book)
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

        } else {
            Log.w(TAG, "Book Id in null in book object. So cannot update");
        }
    }


    public static void deleteBook(String bookId){
        db.collection("books").document(bookId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {

            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d(TAG, "Deleted books-----"+ task.getResult());

            }
        });

    }

    public static void getLibrarians() {


        librarians = new ArrayList<User>();

        db.collection("users")
                .whereEqualTo(ROLE_KEY, "librarian")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                User librarian = new User(document.getString(FULLNAME_KEY),
                                        document.getString(EMAIL_KEY),
                                        document.getString(SJSU_ID_KEY),
                                        document.getString(UID_KEY),
                                        document.getString(ROLE_KEY));

                                Log.d(TAG, document.getId() + " => " + document.getData());

                                librarians.add(librarian);

                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        //return patrons;
    }

}
