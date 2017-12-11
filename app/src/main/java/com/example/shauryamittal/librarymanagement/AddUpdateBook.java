package com.example.shauryamittal.librarymanagement;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.shauryamittal.librarymanagement.model.Book;
import com.example.shauryamittal.librarymanagement.model.Constants;
import com.example.shauryamittal.librarymanagement.model.CurrentUser;
import com.example.shauryamittal.librarymanagement.model.DbOperations;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class AddUpdateBook extends AppCompatActivity {

    private static final int CHOOSE_BOOK_COVER = 1;
    private EditText authorET;
    private EditText titleET;
    private EditText callNumberET;
    private EditText publisherET;
    private EditText yearOfPubET;
    private EditText locationET;
    private EditText noOfCopyET;
//    private EditText statusET;
    private EditText keywordsET;
    private DatabaseReference mDatabase;
    private StorageReference mStorage;
    private ImageView imageUpload;
    Uri uriBookImage;
    ProgressBar uploadBookProgress;
    Button submit;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private FirebaseAuth mAuth;
    private static final int GALLERY_INTENT =2;
    // ...
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_update_book);

        Intent intent = getIntent();
        Book b = (Book)intent.getSerializableExtra("bookObj");
        System.out.print("b==="+b);


        mStorage = FirebaseStorage.getInstance().getReference();
        authorET=findViewById(R.id.Author);
        titleET=findViewById(R.id.Title);
        callNumberET=findViewById(R.id.Callnumber);
        publisherET=findViewById(R.id.Publisher);
        yearOfPubET=findViewById(R.id.Yearofpub);
        locationET=findViewById(R.id.Location);
        noOfCopyET=findViewById(R.id.NumOfCopies);
//        statusET=findViewById(R.id.Status);
        keywordsET=findViewById(R.id.Keywords);
        mAuth = FirebaseAuth.getInstance();
        uploadBookProgress = (ProgressBar) findViewById(R.id.uploadBookProgress);
        uploadBookProgress.setVisibility(View.GONE);
        submit = (Button) findViewById(R.id.createBookSubmit);

        imageUpload = (ImageView) findViewById(R.id.imageUpload);

        imageUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageChoose();
            }
        });

    }
/*
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_INTENT && resultCode == RESULT_OK){
            Uri uri = data.getData();
            StorageReference filepath = mStorage.child("test");
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(AddUpdateBook.this, "Upload Done", Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddUpdateBook.this, "Failed", Toast.LENGTH_LONG).show();
                }
            });

        }
    }*/

    public void addBook(View view) {
        Book book = new Book();
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
        book.setStatus("available");
        book.setKeywords(String.valueOf(keywordsET.getText()).trim());
        book.setLibrarianId(CurrentUser.UID);
        DbOperations dbOperations = new DbOperations();

        submit.setVisibility(View.GONE);
        uploadBookProgress.setVisibility(View.VISIBLE);

        db.collection(Constants.BOOKS_COLLECTION).add(book).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if(task.isSuccessful()){
                    String bookId = task.getResult().getId();
                    uploadImage(bookId);
                }
                else {
                    Log.v("BOOK UPLOAD ", task.getException().getMessage());
                    Toast.makeText(getApplicationContext(), "Unable to set Book info", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CHOOSE_BOOK_COVER && resultCode == RESULT_OK && data != null && data.getData() != null){

            uriBookImage = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriBookImage);
                imageUpload.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    private void uploadImage(final String bookId) {

        StorageReference bookCoverReference = FirebaseStorage.getInstance().getReference(Constants.BOOK_COVERS + "/" + bookId + ".jpg");

        if(uriBookImage != null){
            bookCoverReference.putFile(uriBookImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getApplicationContext(), "Book Added to Library", Toast.LENGTH_SHORT).show();
                    uploadBookProgress.setVisibility(View.GONE);
                    submit.setVisibility(View.VISIBLE);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.v("BOOK IMAGE ", e.getMessage());
                    uploadBookProgress.setVisibility(View.GONE);
                    submit.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), "Unable to upload book image", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private void showImageChoose() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Book Cover"), CHOOSE_BOOK_COVER);
    }

    public void showToast(String text){
        Context context = getApplicationContext();
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        toast.show();
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
                startActivity(new Intent(AddUpdateBook.this, LoginActivity.class));
        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth == null){
            finish();
            CurrentUser.destroyCurrentUser();
            startActivity(new Intent(AddUpdateBook.this, LoginActivity.class));
        }
    }
}
