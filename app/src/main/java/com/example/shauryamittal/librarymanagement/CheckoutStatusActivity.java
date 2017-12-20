package com.example.shauryamittal.librarymanagement;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.example.shauryamittal.librarymanagement.model.Book;
import com.example.shauryamittal.librarymanagement.model.Constants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CheckoutStatusActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    CheckoutStatusAdapter csAdapter;
    List<BookSearchItem> books = new ArrayList<>();
    FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout_status);
        Intent intent = getIntent();

        try {
            Book b = (Book) intent.getSerializableExtra("book1");
            BookSearchItem b1 = new BookSearchItem(b.getAuthor(), b.getTitle(), b.getBookId(), b.getYearOfPub());
            b1.setBook(b);
            loadImage(b.getBookId(), b1);
        }
        catch(Exception e){

        }

        try{
            Book b = (Book)intent.getSerializableExtra("book2");
            BookSearchItem b2 = new BookSearchItem(b.getAuthor(), b.getTitle(), b.getBookId(), b.getYearOfPub());
            b2.setBook(b);
            loadImage(b.getBookId(), b2);;
        }
        catch(Exception e){

        }

        try{
            Book b = (Book)intent.getSerializableExtra("book3");
            BookSearchItem b3 = new BookSearchItem(b.getAuthor(), b.getTitle(), b.getBookId(), b.getYearOfPub());
            b3.setBook(b);
            loadImage(b.getBookId(), b3);
        }
        catch(Exception e){

        }

        recyclerView=(RecyclerView)findViewById(R.id.checkoutStatusRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        csAdapter= new CheckoutStatusAdapter(this,books);
        recyclerView.setAdapter(csAdapter);
    }

    public void loadImage(String bookId, final BookSearchItem b1){

        storage = FirebaseStorage.getInstance();

        StorageReference storageRef = storage.getReference();

        storageRef.child(Constants.IMAGE_FOLDER_PATH + bookId + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                try {

                    new CheckoutStatusActivity.DownLoadImageTask(b1).execute(uri.toString());

                } catch (Exception e) {
                    Toast.makeText(CheckoutStatusActivity.this, "Unable to load image from URI", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    Resources resources = CheckoutStatusActivity.this.getResources();
                    uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + resources.getResourcePackageName(R.drawable.bookcover) + '/' + resources.getResourceTypeName(R.drawable.bookcover) + '/' + resources.getResourceEntryName(R.drawable.bookcover) );
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        b1.setBookBitmap(bitmap);
                        books.add(b1);
                        csAdapter.notifyDataSetChanged();

                    } catch (IOException e1) {
                        Toast.makeText(CheckoutStatusActivity.this, "Problem loading image", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                try {
                    Resources resources = CheckoutStatusActivity.this.getResources();
                    Uri uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + resources.getResourcePackageName(R.drawable.bookcover) + '/' + resources.getResourceTypeName(R.drawable.bookcover) + '/' + resources.getResourceEntryName(R.drawable.bookcover) );
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    b1.setBookBitmap(bitmap);
                    books.add(b1);
                    csAdapter.notifyDataSetChanged();

                } catch (IOException e1) {
                    Toast.makeText(CheckoutStatusActivity.this, "Problem loading image", Toast.LENGTH_SHORT).show();
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
            csAdapter.notifyDataSetChanged();
        }
    }

}
