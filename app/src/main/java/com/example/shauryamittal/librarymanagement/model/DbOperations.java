package com.example.shauryamittal.librarymanagement.model;

import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;

import static android.content.ContentValues.TAG;

/**
 * Created by shauryamittal on 12/5/17.
 */

public class DbOperations {


    private static final String BOOK_TITLE = "title";
    private static final String BOOK_AUTHOR = "author";
    static ArrayList<User> patrons = new ArrayList<User>();
    static SimpleDateFormat dateCompareFormatter = new SimpleDateFormat("MM/dd/yyyy");

    static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static List<User> librarians;
    Book book12;

    public static void createUser(User user){
        SimpleDateFormat dateToString = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        // Create a new user with a first and last name
        Map<String, Object> newUser = new HashMap<>();

        newUser.put(Constants.UID_KEY, user.getUid());
        newUser.put(Constants.FULLNAME_KEY, user.getName());
        newUser.put(Constants.EMAIL_KEY, user.getEmail());
        newUser.put(Constants.SJSU_ID_KEY, user.getSjsuId());
        newUser.put(Constants.ROLE_KEY, user.getRole());
        newUser.put(Constants.EMAIL_VERIFIED, user.getEmailVerified());
        newUser.put(Constants.CheckedOutBooks, "0");
        newUser.put(Constants.LAST_CHECKOUT_DAY_COUNT, "0");
        newUser.put(Constants.LAST_CHECKED_OUT_DAY, dateToString.format(Constants.todaysDate));
        newUser.put(Constants.USER_WAITLISTED_BOOKS_KEY, "");


        // Add a new document with a generated ID
        db.collection(Constants.USER_COLLECTION).document(user.getUid())
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

    public static void addTransaction(Transaction transaction){
        db.collection("transaction").add(transaction);
    }
    public static void addClearedWaitlist(ClearedWaitlist clearedWaitlist){
        db.collection("clearedwaitlist").add(clearedWaitlist);
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

    public static void deleteTransaction(String transactionId){
        db.collection("transaction").document(transactionId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {

            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d(TAG, "Deleted books-----"+ task.getResult());

            }
        });

    }
    public static void dropWaitList(final Book book){
        if(book.getWailistedUsers() == null || book.getWailistedUsers().isEmpty() || book.getWailistedUsers().length() == 0)return;
        String[] users = book.getWailistedUsers().split(",");

        for(String user : users){
            final DocumentReference userRef = db.document(Constants.USER_COLLECTION + "/" + user);
            userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null) {
                            String waitlist = document.getString(Constants.USER_WAITLISTED_BOOKS_KEY);
                            String newWaitList = removeEntry(waitlist, book.getBookId());
                            userRef.update(Constants.USER_WAITLISTED_BOOKS_KEY, newWaitList).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Log.v("WAITLIST ", "WAITLIST UPDATED for user");
                                    }
                                    else {
                                        Log.v("WAITLIST PROBLEM ", task.getException().getMessage());
                                    }
                                }
                            }) ;
                            Log.d("Document Data", "DocumentSnapshot data: " + task.getResult().getData());
                        } else {
                            Log.d("NOT FOUND", "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });

        }

    }


    public static void getLibrarians() {


        librarians = new ArrayList<User>();

        db.collection(Constants.USER_COLLECTION)
                .whereEqualTo(Constants.ROLE_KEY, "librarian")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                User librarian = new User(document.getString(Constants.FULLNAME_KEY),
                                        document.getString(Constants.EMAIL_KEY),
                                        document.getString(Constants.SJSU_ID_KEY),
                                        document.getString(Constants.UID_KEY),
                                        document.getString(Constants.ROLE_KEY));

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

    public static Date addDays(Date date, int days)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
    }
    public static boolean checkEqualDay(Date date1, Date date2)
    {
        System.out.println("Inside checkEqualDay==="+dateCompareFormatter.format(date1).equals(dateCompareFormatter.format(date2)));
        return dateCompareFormatter.format(date1).equals(dateCompareFormatter.format(date2));
    }

    public static String removeEntry(String values, String id){
        if(values.contains(id + ",")) return values.replace(id + ",", "");
        if(values.contains( "," + id )) return values.replace("," + id , "");
        else return values.replace(id , "");
    }
    public static int calculateFine(Date returnDate){
        double diff=(Constants.todaysDate.getTime()-returnDate.getTime())/(24*60*60 * 1000);
        int fine=(int)diff;
        System.out.println("Difference in Date==="+diff+"fine===="+fine);
        if(fine<=0) return 0;
        return fine;
    }

}
