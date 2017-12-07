package com.example.shauryamittal.librarymanagement;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.shauryamittal.librarymanagement.model.CurrentUser;
import com.google.firebase.auth.FirebaseAuth;

public class LibrarianHomepageActivity extends AppCompatActivity {

    Button updateBook;
    Button addBook;
    TextView welcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_librarian_homepage);

        welcome = (TextView) findViewById(R.id.welcome);
        welcome.setText("Welcome " + CurrentUser.NAME );

        addBook = (Button) findViewById(R.id.librarian_addNewBook);
        updateBook = (Button) findViewById(R.id.librarian_updateBook);

        addBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LibrarianHomepageActivity.this, AddUpdateBook.class));
            }
        });

        updateBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LibrarianHomepageActivity.this, AddUpdateBook.class));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.topmenu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                finish();
                CurrentUser.destroyCurrentUser();
                startActivity(new Intent(LibrarianHomepageActivity.this, LoginActivity.class));
        }

        return true;


    }
}