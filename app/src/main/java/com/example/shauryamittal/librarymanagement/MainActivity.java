package com.example.shauryamittal.librarymanagement;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.shauryamittal.librarymanagement.model.Book;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        System.out.println("Main Activity---------------");
        List<Book> books = new ArrayList<>();
        Book b1= new Book();
        b1.setTitle("123");
        b1.setStatus("abs success");
        books.add(b1);
        Book b2= new Book();
        b2.setTitle("123");
        b2.setStatus("abs waitlist");
        books.add(b2);




        Intent intent = new Intent(MainActivity.this, CheckoutStatusActivity.class);
        intent.putExtra("book1",b1);
        intent.putExtra("book2",b2);
        intent.putExtra("book3","");
        startActivity(intent);

    }
}
