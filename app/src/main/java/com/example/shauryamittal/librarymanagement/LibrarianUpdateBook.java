package com.example.shauryamittal.librarymanagement;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.shauryamittal.librarymanagement.model.Book;
import com.example.shauryamittal.librarymanagement.model.Constants;
import com.example.shauryamittal.librarymanagement.model.CurrentUser;
import com.example.shauryamittal.librarymanagement.model.DbOperations;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class LibrarianUpdateBook extends AppCompatActivity {

    Book book;
    private static final int CHOOSE_BOOK_COVER = 1;
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
    ProgressBar uploadBookProgress, imageLoading;
    Button updateBook;
    private ImageView imageUpload;
    Uri uriBookImage;
    FirebaseStorage storage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_update_book);
        Intent intent = getIntent();
        book = (Book)intent.getSerializableExtra("bookObj");
        uploadBookProgress = (ProgressBar) findViewById(R.id.uploadBookProgress);
        uploadBookProgress.setVisibility(View.GONE);
        authorET=findViewById(R.id.Author);
        titleET=findViewById(R.id.Title);
        callNumberET=findViewById(R.id.Callnumber);
        publisherET=findViewById(R.id.Publisher);
        yearOfPubET=findViewById(R.id.Yearofpub);
        locationET=findViewById(R.id.Location);
        noOfCopyET=findViewById(R.id.NumOfCopies);
        imageLoading = (ProgressBar) findViewById(R.id.imageLoadingProgress);
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
        updateBook = (Button) findViewById(R.id.createBookSubmit);
        updateBook.setText("Update Book");


        imageUpload = (ImageView) findViewById(R.id.imageUpload);
        imageUpload.setVisibility(View.GONE);

        Log.v("BOOK REFERENCE ", Constants.IMAGE_FOLDER_PATH + book.getBookId() + "a.jpg");

        storage = FirebaseStorage.getInstance();

        StorageReference storageRef = storage.getReference();

        storageRef.child(Constants.IMAGE_FOLDER_PATH + book.getBookId() + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
                public void onSuccess(Uri uri) {


                    try {
                        new DownLoadImageTask(imageUpload).execute(uri.toString());

                    } catch (Exception e) {
                        Toast.makeText(LibrarianUpdateBook.this, "Unable to load image from URI", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                        Resources resources = LibrarianUpdateBook.this.getResources();
                        uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + resources.getResourcePackageName(R.drawable.bookcover) + '/' + resources.getResourceTypeName(R.drawable.bookcover) + '/' + resources.getResourceEntryName(R.drawable.bookcover) );
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                            imageUpload.setImageBitmap(bitmap);
                            imageUpload.setVisibility(View.VISIBLE);

                        } catch (IOException e1) {
                            Toast.makeText(LibrarianUpdateBook.this, "Problem loading image", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    imageLoading.setVisibility(View.GONE);
                    try {
                        Resources resources = LibrarianUpdateBook.this.getResources();
                        Uri uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + resources.getResourcePackageName(R.drawable.bookcover) + '/' + resources.getResourceTypeName(R.drawable.bookcover) + '/' + resources.getResourceEntryName(R.drawable.bookcover) );
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        imageUpload.setImageBitmap(bitmap);
                        imageUpload.setVisibility(View.VISIBLE);

                    } catch (IOException e1) {
                        Toast.makeText(LibrarianUpdateBook.this, "Problem loading image", Toast.LENGTH_SHORT).show();
                    }
                }
        });


        imageUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showImageChoose();
            }
        });

        storage = FirebaseStorage.getInstance();


        updateBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addBook();
            }
        });

    }


    public void addBook() {
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
                if(Integer.parseInt(numberOfCopies) < 1){
                    book.setStatus("Unavailable");
                }
                else{
                    book.setStatus("Available");
                }
            }catch (NumberFormatException e){
                showToast("Enter Valid Number Of Copies");
                return;
            }
        }

        book.setKeywords(String.valueOf(keywordsET.getText()).trim());
        book.setLibrarianId(CurrentUser.UID);
        DbOperations dbOperations = new DbOperations();
        dbOperations.updateBook(book);

        if(uriBookImage != null){
            uploadImage(book.getBookId());
        }

        Toast toast = Toast.makeText(getApplicationContext(), "Book Update Succesful", Toast.LENGTH_SHORT);
        toast.show();
        //startActivity(new Intent(AddUpdateBook.this, SearchDetailActivity.class));

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

        final StorageReference bookCoverReference = FirebaseStorage.getInstance().getReference(Constants.BOOK_COVERS + "/" + bookId + ".jpg");

        if(uriBookImage == null){

            return;

//            uploadBookProgress.setVisibility(View.GONE);
//
//            StorageReference storageRef = storage.getReference();
//
//            storageRef.child(Constants.IMAGE_FOLDER_PATH + bookId + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                @Override
//                public void onSuccess(Uri uri) {
//                    bookCoverReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                            Toast.makeText(getApplicationContext(), "Book Added to Library", Toast.LENGTH_SHORT).show();
//                            updateBook.setVisibility(View.VISIBLE);
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Log.v("BOOK IMAGE ", e.getMessage());
//                            uploadBookProgress.setVisibility(View.GONE);
//                            updateBook.setVisibility(View.VISIBLE);
//                            Resources resources = LibrarianUpdateBook.this.getResources();
//                            uriBookImage = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + resources.getResourcePackageName(R.drawable.bookcover) + '/' + resources.getResourceTypeName(R.drawable.bookcover) + '/' + resources.getResourceEntryName(R.drawable.bookcover) );
//                        }
//                    });
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception exception) {
//                    // Handle any errors
//                }
//            });

        }
        else {
            bookCoverReference.putFile(uriBookImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getApplicationContext(), "Book Cover Updated", Toast.LENGTH_SHORT).show();
                    uploadBookProgress.setVisibility(View.GONE);
                    updateBook.setVisibility(View.VISIBLE);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.v("BOOK IMAGE ", e.getMessage());
                    uploadBookProgress.setVisibility(View.GONE);
                    updateBook.setVisibility(View.VISIBLE);
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

    private class DownLoadImageTask extends AsyncTask<String,Void,Bitmap> {
        ImageView imageView;

        public DownLoadImageTask(ImageView imageView){
            this.imageView = imageView;
        }

        /*
            doInBackground(Params... params)
                Override this method to perform a computation on a background thread.
         */
        protected Bitmap doInBackground(String...urls){
            String urlOfImage = urls[0];
            Bitmap logo = null;
            try{
                InputStream is = new URL(urlOfImage).openStream();
                /*
                    decodeStream(InputStream is)
                        Decode an input stream into a bitmap.
                 */
                logo = BitmapFactory.decodeStream(is);
            }catch(Exception e){ // Catch the download exception
                e.printStackTrace();
            }
            return logo;
        }

        /*
            onPostExecute(Result result)
                Runs on the UI thread after doInBackground(Params...).
         */
        protected void onPostExecute(Bitmap result){
            imageView.setImageBitmap(result);
            imageLoading.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
        }
    }
}
