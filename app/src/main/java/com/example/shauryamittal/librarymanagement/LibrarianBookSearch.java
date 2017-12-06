package com.example.shauryamittal.librarymanagement;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.shauryamittal.librarymanagement.model.Book;

import java.util.ArrayList;
import java.util.List;

public class LibrarianBookSearch extends AppCompatActivity {
    Spinner librarionSpinner;
    RecyclerView recyclerView;
    LibrarianSearchAdapter lsAdapter;
    List<Book> books;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_librarian_book_search);

        librarionSpinner = (Spinner) findViewById(R.id.librarionSpinner);
        List<String> list = new ArrayList<String>();
        list.add("Select Librarian");
        list.add("Harshit");
        list.add("Shaurya");
        list.add("Gagan");
        list.add("Anshul");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        librarionSpinner.setAdapter(dataAdapter);




//        recyclerView=(RecyclerView)findViewById(R.id.librarianSearchRecyclerView);
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//        books= new ArrayList<>();
//        Book b1= new Book();
//        b1.setTitle("java");
//        b1.setAuthor("kathy searra");
//        b1.setYearOfPub(2010);
//
//        Book b2= new Book();
//        b2.setTitle("Science");
//        b2.setAuthor(" searra");
//        b2.setYearOfPub(2005);
//        books.add(b1);
//        books.add(b2);
//
//        lsAdapter= new LibrarianSearchAdapter(this,books);
//        recyclerView.setAdapter(lsAdapter);

    }

    public void searchResult(View view){
        recyclerView=(RecyclerView)findViewById(R.id.librarianSearchRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        books= new ArrayList<>();
        Book b1= new Book();
        b1.setTitle("java");
        b1.setAuthor("kathy searra");
        b1.setYearOfPub(2010);

        Book b2= new Book();
        b2.setTitle("Science");
        b2.setAuthor(" searra");
        b2.setYearOfPub(2005);
        books.add(b1);
        books.add(b2);

        lsAdapter= new LibrarianSearchAdapter(this,books);
        recyclerView.setAdapter(lsAdapter);
    }
}
