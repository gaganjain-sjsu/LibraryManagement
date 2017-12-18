package com.example.shauryamittal.librarymanagement;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.shauryamittal.librarymanagement.model.Book;

import java.util.ArrayList;
import java.util.List;

public class CheckoutStatusActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    CheckoutStatusAdapter csAdapter;
    List<Book> books = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout_status);
        Intent intent = getIntent();

        //bookId = intent.getStringExtra("bookId");



        Book b1= new Book();
        b1.setTitle("123");
        b1.setStatus("abs success");
        books.add(b1);
        Book b2= new Book();
        b2.setTitle("123");
        b2.setStatus("abs waitlist");
        books.add(b2);
        recyclerView=(RecyclerView)findViewById(R.id.checkoutStatusRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        csAdapter= new CheckoutStatusAdapter(this,books);
        recyclerView.setAdapter(csAdapter);
    }

}
