package com.example.shauryamittal.librarymanagement;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.shauryamittal.librarymanagement.model.CurrentUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SearchDetailActivity extends AppCompatActivity {

    private TextView bookTitle;
    private TextView bookYear;
    private TextView bookPublisher;
    private TextView bookCallNum;
    private TextView bookStatus;
    private TextView bookAuthor;
    private TextView bookCopies;
    private String bookId ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_detail);
        bookTitle=findViewById(R.id.book_title);
        bookYear=findViewById(R.id.book_year_published);
        bookPublisher=findViewById(R.id.book_publisher);
        bookCallNum=findViewById(R.id.book_call);
        bookStatus=findViewById(R.id.book_status);
        bookAuthor=findViewById(R.id.book_author_name);
        bookCopies=findViewById(R.id.book_copies);
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
                    Log.d("test", doc.getString("author"));
                    bookAuthor.setText(doc.getString("author"));
                    bookCallNum.setText(doc.getString("callNumber"));
                    bookTitle.setText(doc.getString("title"));
//                    bookYear.setText(doc.getString("yearOfPub").toString());
                    bookPublisher.setText(doc.getString("publisher"));
                    bookStatus.setText(doc.getString("status"));
                    //bookCopies.setText(doc.getString(""));

                }
            }
        });

    }

    public void addToCart(View view){
        SharedPreferences SP;
        String UID = CurrentUser.UID;
        SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String bookId_sp = SP.getString(UID, null);
        SharedPreferences.Editor edit = SP.edit();
        if (bookId_sp == null){
            edit.putString (UID, bookId);
        }
        else{
            StringBuilder sb = new StringBuilder(bookId_sp);
            sb.append(",").append(bookId);
            edit.putString(UID, sb.toString());
        }
        edit.commit();
        Log.d("Test","Book Id: " + SP.getString(UID, null));

    }
}
