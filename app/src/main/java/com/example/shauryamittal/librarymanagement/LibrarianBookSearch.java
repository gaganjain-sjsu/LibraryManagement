package com.example.shauryamittal.librarymanagement;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.shauryamittal.librarymanagement.model.Book;
import com.example.shauryamittal.librarymanagement.model.Constants;
import com.example.shauryamittal.librarymanagement.model.CurrentUser;
import com.example.shauryamittal.librarymanagement.model.DbOperations;
import com.example.shauryamittal.librarymanagement.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class LibrarianBookSearch extends AppCompatActivity {
    Spinner librarionSpinner;
    RecyclerView recyclerView;
    LibrarianSearchAdapter lsAdapter;
    List<BookSearchItem> books = new ArrayList<>();
    private final String UID_KEY = "uid";
    private final String FULLNAME_KEY = "fullname";
    private final String EMAIL_KEY = "email";
    private final String SJSU_ID_KEY = "sjsuId";
    private final String ROLE_KEY = "role";
    List<String> spinnerList = new ArrayList<String>();
    List<String> spinnerKey = new ArrayList<String>();
    String searchKey="";
    private EditText searchKeywork;
    FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_librarian_book_search);
        librarionSpinner = (Spinner) findViewById(R.id.librarionSpinner);
        spinnerList.add("Select Librarian");
        spinnerKey.add("0");
        getLibrarians();
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, spinnerList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        librarionSpinner.setAdapter(dataAdapter);



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
            case R.id.backRedirect:
                startActivity(new Intent(LibrarianBookSearch.this, LibrarianHomepageActivity.class));
                return true;
            case R.id.homePageRedirect:
                startActivity(new Intent(LibrarianBookSearch.this, LibrarianHomepageActivity.class));
                return true;

            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                finish();
                CurrentUser.destroyCurrentUser();
                startActivity(new Intent(LibrarianBookSearch.this, LoginActivity.class));
        }

        return true;


    }

    public void searchResult(View view){
        searchKeywork=(EditText)findViewById(R.id.librarionSearchtxt);
        searchKey=searchKeywork.getText().toString();
        Spinner dropDown=(Spinner)findViewById(R.id.librarionSpinner);
        String librarianId=spinnerKey.get(dropDown.getSelectedItemPosition());
        books.clear();
        recyclerView=(RecyclerView)findViewById(R.id.librarianSearchRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        lsAdapter= new LibrarianSearchAdapter(this,books);
        recyclerView.setAdapter(lsAdapter);
        getSearchedBooks(librarianId);

    }

    public void getLibrarians() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .whereEqualTo(ROLE_KEY, "librarian")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                User librarian = new User(document.getString(FULLNAME_KEY),
                                        document.getString(EMAIL_KEY),
                                        document.getString(SJSU_ID_KEY),
                                        document.getString(UID_KEY),
                                        document.getString(ROLE_KEY));
                                spinnerList.add(librarian.getName());
                                spinnerKey.add(librarian.getUid());

                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void getSearchedBooks(String librarianId) {
        System.out.println("librarianId==="+librarianId);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if(!librarianId.equals("0")){
            db.collection("books").whereEqualTo("librarianId", librarianId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {

                                for (DocumentSnapshot document : task.getResult()) {
                                    if(searchKey!=null && !searchKey.equals("")){
                                        String keyWord=document.getString("keywords");
                                        if(keyWord!=null && searchKey!=null && keyWord.toLowerCase().contains(searchKey.toLowerCase())){

                                            BookSearchItem b1= new BookSearchItem(
                                                    document.getString("author")
                                                    , document.getString("title")
                                                    , document.getString("bookId")
                                                    , document.getDouble("yearOfPub").intValue());

                                            b1.setBook(document.toObject(Book.class));
                                            b1.setBookId(document.getId());
                                            loadImage(document.getId(), b1);
                                        }
                                    }else{
                                        BookSearchItem b1= new BookSearchItem(
                                                document.getString("author")
                                                , document.getString("title")
                                                , document.getString("bookId")
                                                , document.getDouble("yearOfPub").intValue());
                                        b1.setBook(document.toObject(Book.class));
                                        b1.setBookId(document.getId());
                                        loadImage(document.getId(), b1);
                                    }
                                    lsAdapter.notifyDataSetChanged();
                                }


                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }

                        }
                    });



            db.collection("books").whereEqualTo("updatedBy", librarianId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {

                                for (DocumentSnapshot document : task.getResult()) {
                                    if(searchKey!=null && !searchKey.equals("")){
                                        String keyWord=document.getString("keywords");
                                        if(keyWord!=null && searchKey!=null && keyWord.toLowerCase().contains(searchKey.toLowerCase())){

                                            BookSearchItem b1= new BookSearchItem(
                                                    document.getString("author")
                                                    , document.getString("title")
                                                    , document.getString("bookId")
                                                    , document.getDouble("yearOfPub").intValue());

                                            b1.setBook(document.toObject(Book.class));
                                            b1.setBookId(document.getId());
                                            loadImage(document.getId(), b1);
                                        }
                                    }else{
                                        BookSearchItem b1= new BookSearchItem(
                                                document.getString("author")
                                                , document.getString("title")
                                                , document.getString("bookId")
                                                , document.getDouble("yearOfPub").intValue());
                                        b1.setBook(document.toObject(Book.class));
                                        b1.setBookId(document.getId());
                                        loadImage(document.getId(), b1);
                                    }
                                    lsAdapter.notifyDataSetChanged();
                                }


                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }

                        }
                    });

        }else{
            db.collection("books")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot document : task.getResult()) {
                                    if(searchKey!=null && !searchKey.equals("")){
                                        String keyWord=document.getString("keywords");
                                        if(keyWord!=null && searchKey!=null && keyWord.toLowerCase().contains(searchKey.toLowerCase())){
                                            BookSearchItem b1= new BookSearchItem(
                                                    document.getString("author")
                                                    , document.getString("title")
                                                    , document.getString("bookId")
                                                    , document.getDouble("yearOfPub").intValue());

                                            b1.setBook(document.toObject(Book.class));
                                            b1.setBookId(document.getId());
                                            loadImage(document.getId(), b1);
                                        }
                                    }else{
                                        BookSearchItem b1= new BookSearchItem(
                                                document.getString("author")
                                                , document.getString("title")
                                                , document.getString("bookId")
                                                , document.getDouble("yearOfPub").intValue());
                                        b1.setBook(document.toObject(Book.class));
                                        b1.setBookId(document.getId());
                                        loadImage(document.getId(), b1);
                                    }
                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }

                        }
                    });
        }



    }

    public void loadImage(String bookId, final BookSearchItem b1){

        storage = FirebaseStorage.getInstance();

        StorageReference storageRef = storage.getReference();

        storageRef.child(Constants.IMAGE_FOLDER_PATH + bookId + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                try {

                    new LibrarianBookSearch.DownLoadImageTask(b1).execute(uri.toString());

                } catch (Exception e) {
                    Toast.makeText(LibrarianBookSearch.this, "Unable to load image from URI", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    Resources resources = LibrarianBookSearch.this.getResources();
                    uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + resources.getResourcePackageName(R.drawable.bookcover) + '/' + resources.getResourceTypeName(R.drawable.bookcover) + '/' + resources.getResourceEntryName(R.drawable.bookcover) );
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        b1.setBookBitmap(bitmap);
                        books.add(b1);
                        lsAdapter.notifyDataSetChanged();

                    } catch (IOException e1) {
                        Toast.makeText(LibrarianBookSearch.this, "Problem loading image", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                try {
                    Resources resources = LibrarianBookSearch.this.getResources();
                    Uri uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + resources.getResourcePackageName(R.drawable.bookcover) + '/' + resources.getResourceTypeName(R.drawable.bookcover) + '/' + resources.getResourceEntryName(R.drawable.bookcover) );
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    b1.setBookBitmap(bitmap);
                    books.add(b1);
                    lsAdapter.notifyDataSetChanged();

                } catch (IOException e1) {
                    Toast.makeText(LibrarianBookSearch.this, "Problem loading image", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private class DownLoadImageTask extends AsyncTask<String,Void,Bitmap> {
        BookSearchItem b1;

        public DownLoadImageTask(BookSearchItem b1){
            this.b1 = b1;
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
                logo = BitmapFactory.decodeStream(is);

            }catch(Exception e){ // Catch the download exception
                e.printStackTrace();
            }
            return logo;
        }

        protected void onPostExecute(Bitmap result){
            b1.setBookBitmap(result);
            books.add(b1);
            lsAdapter.notifyDataSetChanged();
        }
    }

}
