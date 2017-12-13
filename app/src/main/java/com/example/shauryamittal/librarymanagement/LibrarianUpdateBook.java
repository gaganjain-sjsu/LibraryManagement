package com.example.shauryamittal.librarymanagement;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.shauryamittal.librarymanagement.model.Book;
import com.example.shauryamittal.librarymanagement.model.CurrentUser;
import com.example.shauryamittal.librarymanagement.model.DbOperations;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.io.IOException;

public class LibrarianUpdateBook extends AppCompatActivity {

    Book book;
    private EditText authorET;
    private EditText titleET;
    private EditText callNumberET;
    private EditText publisherET;
    private EditText yearOfPubET;
    private EditText locationET;
    private EditText noOfCopyET;
    //private EditText statusET;
    private EditText keywordsET;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_update_book);
        Intent intent = getIntent();
        book = (Book)intent.getSerializableExtra("bookObj");
        authorET=findViewById(R.id.Author);
        titleET=findViewById(R.id.Title);
        callNumberET=findViewById(R.id.Callnumber);
        publisherET=findViewById(R.id.Publisher);
        yearOfPubET=findViewById(R.id.Yearofpub);
        locationET=findViewById(R.id.Location);
        noOfCopyET=findViewById(R.id.NumOfCopies);
       // statusET=findViewById(R.id.Status);
        keywordsET=findViewById(R.id.Keywords);
        mAuth = FirebaseAuth.getInstance();

        //showToast(CurrentUser.NAME);

        authorET.setText(book.getAuthor());
        titleET.setText(book.getTitle());
        callNumberET.setText(book.getCallNumber());
        publisherET.setText(book.getPublisher());
        yearOfPubET.setText(String.valueOf(book.getYearOfPub()));
        locationET.setText(book.getLocation());
        noOfCopyET.setText(String.valueOf(book.getNoOfCopy()));
        //statusET.setText(book.getStatus());
        keywordsET.setText(book.getKeywords());

    }



    public void addBook(View view) {
        book.setAuthor(String.valueOf(authorET.getText()).trim());
        if(book.getAuthor()==null || book.getAuthor().trim().equals("")){
            showToast("Enter Author");
            return;
        }
        book.setTitle(String.valueOf(titleET.getText()).trim());
        if(book.getTitle()==null || book.getTitle().trim().equals("")){
            showToast("Enter Title");
            return;
        }
        book.setCallNumber(String.valueOf(callNumberET.getText()).trim());
        if(book.getCallNumber()==null || book.getCallNumber().trim().equals("")){
            showToast("Enter Call Number");
            return;
        }
        book.setPublisher(String.valueOf(publisherET.getText()).trim());
        if(book.getPublisher()==null || book.getPublisher().trim().equals("")){
            showToast("Enter Publisher");
            return;
        }

        String publicationYear=String.valueOf(yearOfPubET.getText()).trim();
        if(publicationYear==null || publicationYear.trim().equals("")){
            showToast("Enter Publication Year");
            return;
        }else{
            publicationYear=publicationYear.trim();
            try {
                book.setYearOfPub(Integer.parseInt(publicationYear));
            }catch (NumberFormatException e){
                showToast("Enter Valid Publication Year");
                return;
            }
        }


        book.setLocation(String.valueOf(locationET.getText()).trim());
        if(book.getLocation()==null || book.getLocation().trim().equals("")){
            showToast("Enter Location in Library");
            return;
        }


        String numberOfCopies=String.valueOf(noOfCopyET.getText()).trim();
        if(numberOfCopies==null || numberOfCopies.trim().equals("")){
            showToast("Enter Number Of Copies");
            return;
        }else{
            numberOfCopies=numberOfCopies.trim();
            try {
                book.setNoOfCopy(Integer.parseInt(numberOfCopies));
            }catch (NumberFormatException e){
                showToast("Enter Valid Number Of Copies");
                return;
            }
        }

//        book.setStatus(String.valueOf(statusET.getText()).trim());
//        if(book.getStatus()==null || book.getStatus().trim().equals("")){
//            showToast("Enter Status");
//            return;
//        }

        book.setKeywords(String.valueOf(keywordsET.getText()).trim());
        book.setLibrarianId(CurrentUser.UID);
        DbOperations dbOperations = new DbOperations();
        dbOperations.updateBook(book);
        Toast toast = Toast.makeText(getApplicationContext(), "Book Update Succesful", Toast.LENGTH_SHORT);
        toast.show();
        //startActivity(new Intent(AddUpdateBook.this, SearchDetailActivity.class));

    }




    public void showToast(String text){
        Context context = getApplicationContext();
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.librarian_menu, menu);

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
                startActivity(new Intent(LibrarianUpdateBook.this, LoginActivity.class));
        }

        return true;

    }
}
