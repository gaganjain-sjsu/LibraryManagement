package com.example.shauryamittal.librarymanagement;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shauryamittal.librarymanagement.model.Constants;
import com.example.shauryamittal.librarymanagement.model.CurrentUser;
import com.example.shauryamittal.librarymanagement.model.WaitlistArray;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sun.mail.imap.protocol.UID;

import java.util.ArrayList;

public class SearchDetailActivity extends AppCompatActivity {

    private TextView bookTitle;
    private TextView bookYear;
    private TextView bookPublisher;
    private TextView bookCallNum;
    private TextView bookStatus;
    private TextView bookAuthor;
    private TextView bookCopies;
    private String bookId ;
    int numberOfCopies;
    int checkedOutCopies;
    private Button addToCart;
    private boolean waitlist = false;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_detail);
//
//        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
//        String bookId_sp = SP.getString(CurrentUser.UID, null);
//        Toast.makeText(this, bookId_sp, Toast.LENGTH_SHORT).show();
////        SharedPreferences.Editor edit = SP.edit();
////        edit.remove(CurrentUser.UID);
////        edit.commit();
//        Toast.makeText(this, bookId_sp, Toast.LENGTH_SHORT).show();

        bookTitle=findViewById(R.id.book_title);
        bookYear=findViewById(R.id.book_year_published);
        bookPublisher=findViewById(R.id.book_publisher);
        bookCallNum=findViewById(R.id.book_call);
        bookStatus=findViewById(R.id.book_status);
        bookAuthor=findViewById(R.id.book_author_name);
        bookCopies=findViewById(R.id.book_copies);
        addToCart = (Button) findViewById(R.id.add_to_cart);
        Intent intent = getIntent();
        bookId = intent.getStringExtra("bookId");
        //bookId = "LoqMjSzUjT6qWCOXtfnt";
        FirebaseFirestore database= FirebaseFirestore.getInstance();
        DocumentReference mRef=database.collection("books").document(bookId);
        mRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){

                    DocumentSnapshot doc = task.getResult();
                    int year = doc.getDouble("yearOfPub").intValue();
                    int copies = doc.getDouble("noOfCopy").intValue();
                    Log.d("test", doc.getString("author"));
                    bookAuthor.setText(doc.getString("author"));
                    bookCallNum.setText(doc.getString("callNumber"));
                    bookTitle.setText(doc.getString("title"));
                    bookYear.setText((String.valueOf(year)));
                    bookPublisher.setText(doc.getString("publisher"));
                    bookStatus.setText(doc.getString("status"));
                    bookCopies.setText(String.valueOf(copies));
                    numberOfCopies = doc.getDouble(Constants.NUMBER_OF_COPIES_KEY).intValue();
                    checkedOutCopies = doc.getDouble(Constants.CHECKED_OUT_COPIES_KEY).intValue();
                    if(numberOfCopies - checkedOutCopies <=0){
                        addToCart.setText("Put to waitlist");
                        waitlist = true;
                    }
                }
            }
        });

        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(waitlist == false){
                    addToCart();
                }
                else {
                    addToWaitlist();
                }
            }
        });

    }

    private void addToWaitlist() {

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setMessage("Do you wish to put yourself in waitlist for this book?").setCancelable(false);

        alertDialogBuilder.setPositiveButton("Put To Waitlist",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final DocumentReference userDoc = db.document(Constants.USER_COLLECTION + "/" + CurrentUser.UID);
                        userDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document != null ) {
                                        Log.d("Doc data", "DocumentSnapshot data: " + task.getResult().getData());
                                        if(document.contains(Constants.USER_WAITLISTED_BOOKS_KEY)){
                                            if(document.getString(Constants.USER_WAITLISTED_BOOKS_KEY).contains(bookId)){
                                                Toast.makeText(SearchDetailActivity.this, "You are already on the waitlist", Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                            userDoc.update(Constants.USER_WAITLISTED_BOOKS_KEY, document.getString(Constants.USER_WAITLISTED_BOOKS_KEY)+ ","+ bookId).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){

                                                        Toast.makeText(SearchDetailActivity.this, "Successfully added to waitlist", Toast.LENGTH_SHORT).show();
                                                    }
                                                    else {
                                                        Toast.makeText(SearchDetailActivity.this, "Unable to add to waitlist", Toast.LENGTH_SHORT).show();
                                                        Log.v("WAITLIST ERROR ", task.getException().getMessage());
                                                    }
                                                }
                                            });
                                            Log.v("WAITLIST DATA ", document.getString(Constants.USER_WAITLISTED_BOOKS_KEY));
                                        }
                                        else {
                                            userDoc.update(Constants.USER_WAITLISTED_BOOKS_KEY, bookId).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        Toast.makeText(SearchDetailActivity.this, "Successfully added to waitlist", Toast.LENGTH_SHORT).show();
                                                    }
                                                    else {
                                                        Toast.makeText(SearchDetailActivity.this, "Unable to add to waitlist", Toast.LENGTH_SHORT).show();
                                                        Log.v("WAITLIST ERROR ", task.getException().getMessage());
                                                    }
                                                }
                                            });
                                        }
                                    } else {
                                        Log.d("USER PROFILE ", "USER DATA NOT FOUND");
                                    }
                                } else {
                                    Log.d("FAIL", "get failed with ", task.getException());
                                }
                            }
                        });
                    }
                });

        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

        AlertDialog alert = alertDialogBuilder.create();
        alert.setTitle("Waitlist");
        alert.show();

    }

    public void addToCart(){
        SharedPreferences SP;
        String UID = CurrentUser.UID;
        SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String bookId_sp = SP.getString(UID, null);
        SharedPreferences.Editor edit = SP.edit();
        //edit.remove(UID);
        if (bookId_sp == null || bookId_sp.trim().equals("")){
            edit.putString (UID, bookId);
            showToast("Added Successfully to the cart");
        }
        else{
            if(bookId_sp.charAt(0)==','){
                bookId_sp=bookId_sp.substring(1);
            }
            if(bookId_sp.charAt(bookId_sp.length()-1)==','){
                bookId_sp=bookId_sp.substring(0,bookId_sp.length()-1);
            }
            //bookId_sp=bookId_sp.replaceAll(",,",",");

            StringBuilder sb = new StringBuilder(bookId_sp);
            //sb = new StringBuilder();
            System.out.print("###sb.toString()==="+sb.toString()+"length="+sb.toString().split(",").length);
            if(sb.toString().split(",").length>=3){
                showToast("You Already have 3 items in your cart");
            }
            else if (!sb.toString().contains(bookId)) {
                sb.append(",").append(bookId);
                showToast("Added Successfully to the cart");
            }
            else{
                showToast("Item Already added to your cart");
            }
            edit.putString(UID, sb.toString());
        }
        edit.commit();
//        Log.v("SHARED PREF", SP)

        Log.d("Test","Book Id: " + SP.getString(UID, null));

    }

    public void showToast(String text){
        Context context = getApplicationContext();
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.topmenu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id){
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                finish();
                CurrentUser.destroyCurrentUser();
                startActivity(new Intent(SearchDetailActivity.this, LoginActivity.class));
                break;
            case R.id.view_cart_option:
                startActivity(new Intent(SearchDetailActivity.this, ShoppingCartActivity.class));
                break;

            case R.id.del:
                SharedPreferences SP;
                String UID = CurrentUser.UID;
                SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                String bookId_sp = SP.getString(UID, null);
                SharedPreferences.Editor edit = SP.edit();
                edit.remove(UID);
                edit.commit();
                break;
        }

        return super.onOptionsItemSelected(item);

    }
}