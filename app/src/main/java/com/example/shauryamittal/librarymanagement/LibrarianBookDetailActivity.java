package com.example.shauryamittal.librarymanagement;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shauryamittal.librarymanagement.model.Book;
import com.example.shauryamittal.librarymanagement.model.Constants;
import com.example.shauryamittal.librarymanagement.model.CurrentUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;




public class LibrarianBookDetailActivity extends AppCompatActivity {

        private TextView bookTitle;
        private TextView bookYear;
        private TextView bookPublisher;
        private TextView bookCallNum;
        private TextView bookStatus;
        private TextView bookAuthor;
        private TextView bookCopies;
        private String bookId ;
        private Book bookObj;
        int numberOfCopies;
        FirebaseStorage storage;
        int checkedOutCopies;
        private Button addToCart;
        ProgressBar imageLoading;
        ImageView bookImage;
        private boolean waitlist = false;
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_librarian_book_detail);

            setTitle("Book Details");

            bookTitle=findViewById(R.id.book_title);
            bookYear=findViewById(R.id.book_year_published);
            bookPublisher=findViewById(R.id.book_publisher);
            bookCallNum=findViewById(R.id.book_call);
            bookStatus=findViewById(R.id.book_status);
            bookAuthor=findViewById(R.id.book_author_name);
            bookCopies=findViewById(R.id.book_copies);
            addToCart = (Button) findViewById(R.id.add_to_cart);
            Intent intent = getIntent();
            bookId = intent.getStringExtra("bookId");

            imageLoading = (ProgressBar) findViewById(R.id.bookImageload);


            bookImage = (ImageView) findViewById(R.id.bookImage);
            bookImage.setVisibility(View.GONE);

            storage = FirebaseStorage.getInstance();

            StorageReference storageRef = storage.getReference();

            storageRef.child(Constants.IMAGE_FOLDER_PATH + bookId + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {

                    try {
                        new LibrarianBookDetailActivity.DownLoadImageTask(bookImage).execute(uri.toString());

                    } catch (Exception e) {
                        Toast.makeText(LibrarianBookDetailActivity.this, "Unable to load image from URI", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                        Resources resources = LibrarianBookDetailActivity.this.getResources();
                        uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + resources.getResourcePackageName(R.drawable.bookcover) + '/' + resources.getResourceTypeName(R.drawable.bookcover) + '/' + resources.getResourceEntryName(R.drawable.bookcover) );
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                            bookImage.setImageBitmap(bitmap);
                            bookImage.setVisibility(View.VISIBLE);
                            imageLoading.setVisibility(View.GONE);

                        } catch (IOException e1) {
                            Toast.makeText(LibrarianBookDetailActivity.this, "Problem loading image", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    imageLoading.setVisibility(View.GONE);
                    try {
                        Resources resources = LibrarianBookDetailActivity.this.getResources();
                        Uri uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + resources.getResourcePackageName(R.drawable.bookcover) + '/' + resources.getResourceTypeName(R.drawable.bookcover) + '/' + resources.getResourceEntryName(R.drawable.bookcover) );
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        bookImage.setImageBitmap(bitmap);
                        bookImage.setVisibility(View.VISIBLE);

                    } catch (IOException e1) {
                        Toast.makeText(LibrarianBookDetailActivity.this, "Problem loading image", Toast.LENGTH_SHORT).show();
                    }
                }
            });




            FirebaseFirestore database= FirebaseFirestore.getInstance();
            DocumentReference mRef=database.collection("books").document(bookId);
            mRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){

                        DocumentSnapshot doc = task.getResult();
                        bookObj = task.getResult().toObject(Book.class);
                        bookObj.setBookId(doc.getId());
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
                        numberOfCopies = doc.getDouble(Constants.NUMBER_OF_COPIES_KEY).intValue();
                        checkedOutCopies = doc.getDouble(Constants.CHECKED_OUT_COPIES_KEY).intValue();

                    }
                }
            });

            addToCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), LibrarianUpdateBook.class);
//                    EditText editText = (EditText) findViewById(R.id.editText);
//                    String message = editText.getText().toString();
//                    intent.putExtra(EXTRA_MESSAGE, message);
                    intent.putExtra("bookObj",bookObj);
                    getApplicationContext().startActivity(intent);
                }
            });

        }

        private void addToWaitlist() {

            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

            alertDialogBuilder.setMessage("Do you wish to put yourself in waitlist for this book?").setCancelable(false);

            alertDialogBuilder.setPositiveButton("Put To Waitlist",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            final DocumentReference userDoc = db.document(Constants.USER_COLLECTION + "/" + CurrentUser.UID);
                            userDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document != null ) {
                                            Log.d("Doc data", "DocumentSnapshot data: " + task.getResult().getData());
                                            if(document.contains(Constants.USER_WAITLISTED_BOOKS_KEY)){
                                                if(document.getString(Constants.USER_WAITLISTED_BOOKS_KEY).contains(bookId)){
                                                    Toast.makeText( LibrarianBookDetailActivity.this, "You are already on the waitlist", Toast.LENGTH_SHORT).show();
                                                    return;
                                                }

                                                String bookIdExtension;

                                                if(document.getString(Constants.USER_WAITLISTED_BOOKS_KEY).equals("")){
                                                    bookIdExtension = bookId;
                                                }
                                                else {
                                                    bookIdExtension = "," + bookId;
                                                }

                                                userDoc.update(Constants.USER_WAITLISTED_BOOKS_KEY, document.getString(Constants.USER_WAITLISTED_BOOKS_KEY)+ bookIdExtension).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()){
                                                            updateBookWaitList();
                                                        }
                                                        else {
                                                            Toast.makeText(LibrarianBookDetailActivity.this, "Unable to add to waitlist", Toast.LENGTH_SHORT).show();
                                                            Log.v("WAITLIST ERROR ", task.getException().getMessage());
                                                        }
                                                    }
                                                });
                                                Log.v("WAITLIST DATA ", document.getString(Constants.USER_WAITLISTED_BOOKS_KEY));
                                            }
                                            else {
                                                userDoc.update(Constants.USER_WAITLISTED_BOOKS_KEY, bookId).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()){
                                                            updateBookWaitList();
                                                        }
                                                        else {
                                                            Toast.makeText(LibrarianBookDetailActivity.this, "Unable to add to waitlist", Toast.LENGTH_SHORT).show();
                                                            Log.v("WAITLIST ERROR ", task.getException().getMessage());
                                                        }
                                                    }
                                                });
                                            }
                                        } else {
                                            Log.d("USER PROFILE ", "USER DATA NOT FOUND");
                                        }
                                    } else {
                                        Log.d("FAIL", "get failed with ", task.getException());
                                    }
                                }
                            });
                        }
                    });

            alertDialogBuilder.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });

            AlertDialog alert = alertDialogBuilder.create();
            alert.setTitle("Waitlist");
            alert.show();

        }

        public void addToCart(){
            SharedPreferences SP;
            String UID = CurrentUser.UID;
            SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            String bookId_sp = SP.getString(UID, null);
            SharedPreferences.Editor edit = SP.edit();
            //edit.remove(UID);
            if (bookId_sp == null || bookId_sp.trim().equals("")){
                edit.putString (UID, bookId);
                showToast("Added Successfully to the cart");
            }
            else{
                if(bookId_sp.charAt(0)==','){
                    bookId_sp=bookId_sp.substring(1);
                }
                if(bookId_sp.charAt(bookId_sp.length()-1)==','){
                    bookId_sp=bookId_sp.substring(0,bookId_sp.length()-1);
                }
                //bookId_sp=bookId_sp.replaceAll(",,",",");

                StringBuilder sb = new StringBuilder(bookId_sp);
                //sb = new StringBuilder();
                System.out.print("###sb.toString()==="+sb.toString()+"length="+sb.toString().split(",").length);
                if(sb.toString().split(",").length>=3){
                    showToast("You Already have 3 items in your cart");
                }
                else if (!sb.toString().contains(bookId)) {
                    sb.append(",").append(bookId);
                    showToast("Added Successfully to the cart");
                }
                else{
                    showToast("Item Already added to your cart");
                }
                edit.putString(UID, sb.toString());
            }
            edit.commit();
//        Log.v("SHARED PREF", SP)

            Log.d("Test","Book Id: " + SP.getString(UID, null));

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
//            case R.id.backRedirect:
//                startActivity(new Intent(LibrarianViewBooksActivity.this, LibrarianHomepageActivity.class));
//                return true;
            case R.id.homePageRedirect:
                startActivity(new Intent(LibrarianBookDetailActivity.this, LibrarianHomepageActivity.class));
                return true;
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                finish();
                CurrentUser.destroyCurrentUser();
                startActivity(new Intent(LibrarianBookDetailActivity.this, LoginActivity.class));
        }

        return true;
    }

        public void updateBookWaitList(){
            final DocumentReference bookDoc = db.document(Constants.BOOKS_COLLECTION + "/" + bookId);
            bookDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null ) {
                            Log.d("Doc data", "DocumentSnapshot data: " + task.getResult().getData());
                            if(document.contains(Constants.WAITLISTED_USERS_KEY)){
                                if(document.getString(Constants.WAITLISTED_USERS_KEY).contains(CurrentUser.UID)){
                                    Toast.makeText(LibrarianBookDetailActivity.this, "You are already on the waitlist", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                String userIdExtension;

                                if(document.getString(Constants.WAITLISTED_USERS_KEY).equals("")){
                                    userIdExtension = CurrentUser.UID;
                                }
                                else {
                                    userIdExtension = "," + CurrentUser.UID;
                                }

                                bookDoc.update(Constants.WAITLISTED_USERS_KEY, document.getString(Constants.WAITLISTED_USERS_KEY)+ userIdExtension).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){

                                            Toast.makeText(LibrarianBookDetailActivity.this, "Successfully added to waitlist", Toast.LENGTH_SHORT).show();
                                        }
                                        else {
                                            Toast.makeText(LibrarianBookDetailActivity.this, "Unable to add to waitlist", Toast.LENGTH_SHORT).show();
                                            Log.v("BOOK WAITLIST ERROR ", task.getException().getMessage());
                                        }
                                    }
                                });
                                Log.v("WAITLIST DATA ", document.getString(Constants.WAITLISTED_USERS_KEY));
                            }
                            else {
                                bookDoc.update(Constants.WAITLISTED_USERS_KEY, CurrentUser.UID).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(LibrarianBookDetailActivity.this, "Successfully added to waitlist", Toast.LENGTH_SHORT).show();
                                        }
                                        else {
                                            Toast.makeText(LibrarianBookDetailActivity.this, "Unable to add to waitlist", Toast.LENGTH_SHORT).show();
                                            Log.v("BOOK WAITLIST ERROR ", task.getException().getMessage());
                                        }
                                    }
                                });
                            }
                        } else {
                            Log.d("BOOK INFO ", "BOOK DATA NOT FOUND");
                        }
                    } else {
                        Log.d("FAIL", "get failed with ", task.getException());
                    }
                }
            });
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

            protected void onPostExecute(Bitmap result){
                imageView.setImageBitmap(result);
                imageLoading.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
                imageLoading.setVisibility(View.GONE);
            }
        }

    }

