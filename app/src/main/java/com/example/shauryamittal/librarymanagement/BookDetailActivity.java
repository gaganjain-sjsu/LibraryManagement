package com.example.shauryamittal.librarymanagement;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.shauryamittal.librarymanagement.model.CurrentUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class BookDetailActivity extends AppCompatActivity {
    private TextView bookTitle;
    private TextView bookYear;
    private TextView bookPublisher;
    private TextView bookCallNum;
    private TextView bookStatus;
    private TextView bookAuthor;
    private TextView bookCopies;
    private DatabaseReference mDatabase;
    private String bookId ;

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
        bookCopies=findViewById(R.id.book_copies);
        Intent intent = getIntent();
        //bookId = Integer.parseInt(intent.getStringExtra("bookId"));
        bookId = intent.getStringExtra("bookId");
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
                }
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

        switch (id){
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                finish();
                CurrentUser.destroyCurrentUser();
                startActivity(new Intent(BookDetailActivity.this, LoginActivity.class));
        }

        return super.onOptionsItemSelected(item);

    }


}
