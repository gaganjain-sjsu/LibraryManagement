package com.example.shauryamittal.librarymanagement;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class LibrarianBookSearch extends AppCompatActivity {
    Spinner librarionSpinner;
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
    }
}
