package com.example.shauryamittal.librarymanagement;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.example.shauryamittal.librarymanagement.model.Book;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddUpdateBook extends AppCompatActivity {
    private EditText authorET;
    private EditText titleET;
    private EditText callNumberET;
    private EditText publisherET;
    private EditText yearOfPubET;
    private EditText locationET;
    private EditText noOfCopyET;
    private EditText statusET;
    private EditText keywordsET;
    private DatabaseReference mDatabase;
    // ...
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_update_book);

        authorET=findViewById(R.id.Author);
        titleET=findViewById(R.id.Title);
        callNumberET=findViewById(R.id.Callnumber);
        publisherET=findViewById(R.id.Publisher);
        yearOfPubET=findViewById(R.id.Yearofpub);
        locationET=findViewById(R.id.Location);
        noOfCopyET=findViewById(R.id.NumOfCopies);
        statusET=findViewById(R.id.Status);
        keywordsET=findViewById(R.id.Keywords);
    }

    public void addBook(View view) {
//        Book book = new Book();
//        book.setAuthor(String.valueOf(authorET.getText()));
//        book.setTitle(String.valueOf(titleET.getText()));
//        book.setCallNumber(String.valueOf(callNumberET.getText()));
//        book.setPublisher(String.valueOf(publisherET.getText()));
//        book.setYearOfPub(String.valueOf(yearOfPubET.getText()));
//        book.setLocation(String.valueOf(locationET.getText()));
//        book.setNoOfCopy(String.valueOf(noOfCopyET.getText()));
//        book.setStatus(String.valueOf(statusET.getText()));
//        book.setKeywords(String.valueOf(keywordsET.getText()));

        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference mRef=database.getReference().child("book");
        mRef.child("book").child("2").setValue("man");
//        mDatabase = FirebaseDatabase.getInstance();
//        mDatabase.getReference().child("Users").push();
//        //mDatabase.setValue("11");
        //mDatabase.child("1").setValue("Agarwal");
        System.out.println("AddBook Called"+authorET.getText());
    }

}
