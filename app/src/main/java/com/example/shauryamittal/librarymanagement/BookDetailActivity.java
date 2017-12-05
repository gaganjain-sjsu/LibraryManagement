package com.example.shauryamittal.librarymanagement;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;

public class BookDetailActivity extends AppCompatActivity {
    private TextView bookTitle;
    private TextView bookYear;
    private TextView bookPublisher;
    private TextView bookCallNum;
    private TextView bookStatus;
    private TextView bookAuthor;
    private TextView bookCopies;
    private DatabaseReference mDatabase;
    private int bookId ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);
        bookTitle=findViewById(R.id.book_title);
        bookYear=findViewById(R.id.book_year_published);
        bookPublisher=findViewById(R.id.book_publisher);
        bookCallNum=findViewById(R.id.book_call);
        bookStatus=findViewById(R.id.book_status);
        bookAuthor=findViewById(R.id.book_author_name);
        bookCallNum=findViewById(R.id.book_call);
        Intent intent = getIntent();
        bookId = Integer.parseInt(intent.getStringExtra("bookId"));

    }


}
