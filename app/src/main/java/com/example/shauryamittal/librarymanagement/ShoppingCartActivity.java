package com.example.shauryamittal.librarymanagement;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shauryamittal.librarymanagement.model.Book;
import com.example.shauryamittal.librarymanagement.model.Constants;
import com.example.shauryamittal.librarymanagement.model.CurrentUser;
import com.example.shauryamittal.librarymanagement.model.DbOperations;
import com.example.shauryamittal.librarymanagement.model.MailUtility;
import com.example.shauryamittal.librarymanagement.model.User;
import com.example.shauryamittal.librarymanagement.model.Transaction;
import com.example.shauryamittal.librarymanagement.model.VerificationEmailData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.function.DoubleBinaryOperator;

import static android.content.ContentValues.TAG;

public class ShoppingCartActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    public static ShoppingCartAdapter adapter;
    public static List<BookSearchItem> cartItems = new ArrayList<BookSearchItem>();
    String bookIds[];
    private String BOOKS_COLLECTION = "books";
    private static final String USER_COLLECTION = "users";
    private static final String BOOK_TITLE = "title";
    private static final String BOOK_AUTHOR = "author";
    private static final String BOOKS_ISSUED_COLLECTION = "booksIssued";
    TextView remove;
    Button checkout;
    static FirebaseFirestore db = FirebaseFirestore.getInstance();
    public ArrayList<Book> checkedOutBooks = new ArrayList<Book>();
    public ArrayList<Book> checkedOutStatus = new ArrayList<Book>();
    public int successfulCheckedCount=0;
    //public ArrayList<Book> eligibleToCheckoutBooks = new ArrayList<Book>();
    public int currentCheckoutSize = 0;
    public boolean canBeCheckedOut = true;
    String currentUserId;
    int checkOutBooks=0;
    Date lastCheckedOutDay;
    int lastCheckoutDayCount=0;
    User currentUserDetails;
    ArrayList<VerificationEmailData> emailData = new ArrayList<VerificationEmailData>();
    FirebaseStorage storage;

    String[] bookKeyList;
    SimpleDateFormat dateToString = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);
        successfulCheckedCount=0;
        recyclerView = (RecyclerView) findViewById(R.id.cart_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        checkout = (Button) findViewById(R.id.checkoutCart);

        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences SP;
                SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                String currentCartItems = SP.getString(CurrentUser.UID, null);
                if(currentCartItems==null|| currentCartItems.trim().equals("")){
                    Toast toast = Toast.makeText(getApplicationContext(), "Cart Empty", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
                currentUserId=CurrentUser.UID;
                System.out.println("currentCartItems==="+currentCartItems);
                if(currentCartItems.charAt(0)==','){
                    currentCartItems=currentCartItems.substring(1);
                    SharedPreferences.Editor edit = SP.edit();
                    edit.putString (CurrentUser.UID, currentCartItems);
                    edit.commit();
                }
                bookKeyList = currentCartItems.split(",");
                currentCheckoutSize=bookKeyList.length;
                DocumentReference mRefUser = db.collection("users").document(currentUserId);
                mRefUser.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            //System.out.println("Book Id in CheckOut Book Is:-"+doc.getId());

                            checkOutBooks=Integer.parseInt(doc.getString(Constants.CheckedOutBooks));
                            String lastCountStr=doc.getString(Constants.LAST_CHECKOUT_DAY_COUNT);
                            if(lastCountStr==null|| lastCountStr.trim().equals("")){
                                Log.w(TAG, "lastCheckoutDayCount is Null");
                                return;
                            }
                            lastCheckoutDayCount=Integer.parseInt(lastCountStr);
                            System.out.println("!!!!!!!lastCheckoutDayCount===="+lastCheckoutDayCount);

                            String lastCheckDay=doc.getString(Constants.LAST_CHECKED_OUT_DAY);
                            if(lastCheckDay==null|| lastCheckDay.trim().equals("")){
                                Log.w(TAG, "lastCheckoutDay is Null");
                                return;
                            }
                            try {
                                lastCheckedOutDay=dateToString.parse(lastCheckDay);
                            } catch (ParseException e) {
                                Log.w(TAG, "Error In parsing Date"+e.getMessage());
                            }

                            if((checkOutBooks+currentCheckoutSize)>9){
                                String tostString="Cannot Checkout. " + checkOutBooks + " Books Already Issued. Remove "+(checkOutBooks+currentCheckoutSize-9)+" Books from cart";
                                Toast toast = Toast.makeText(getApplicationContext(), tostString, Toast.LENGTH_SHORT);
                                toast.show();
                            }else if(DbOperations.checkEqualDay(lastCheckedOutDay,Constants.todaysDate) && (lastCheckoutDayCount+currentCheckoutSize)>3){
                                if(lastCheckoutDayCount==3){
                                    Toast toast = Toast.makeText(getApplicationContext(), "Reached Daily Checkout Limit", Toast.LENGTH_SHORT);
                                    toast.show();
                                }else{
                                    System.out.println("lastCheckoutDayCount="+lastCheckoutDayCount+" currentCheckoutSize=="+currentCheckoutSize);
                                    Toast toast = Toast.makeText(getApplicationContext(), "Daily Checkout limit is 3. Remove "+(lastCheckoutDayCount+currentCheckoutSize-3)+" books to checkout.", Toast.LENGTH_SHORT);
                                    toast.show();
                                }

                            }else{
                                for (int i = 0; i < currentCheckoutSize; i++) {
                                    DocumentReference mRef = db.collection("books").document(bookKeyList[i]);
                                    mRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot doc = task.getResult();
                                                Book book = doc.toObject(Book.class);
                                                book.setBookId(doc.getId());
                                                checkedOutBooks.add(book);
                                                if(checkedOutBooks.size()==currentCheckoutSize){
//
                                                        for(Book book1 :checkedOutBooks){
                                                            if(book1.getNoOfCopy()<=book1.getNoOfCheckedOutCopy()){
                                                                book1.setStatus("Book not available");
                                                                checkedOutStatus.add(book1);
                                                                continue;
                                                            }

                                                            book1.setNoOfCheckedOutCopy(book1.getNoOfCheckedOutCopy()+1);
                                                            if (book.getBookId() != null) {
                                                                DbOperations.updateBook(book1);
                                                            } else {
                                                                Log.w(TAG, "Book Id in null in book object. So cannot update");
                                                            }

                                                            if(book1.getNoOfCopy() - book1.getNoOfCheckedOutCopy()<=0){
                                                                book1.setStatus("Unavailable");
                                                            }
                                                            if (book.getBookId() != null) {
                                                                DbOperations.updateBook(book1);
                                                            } else {
                                                                Log.w(TAG, "Book Id in null in book object. So cannot update");
                                                            }

                                                          //  Updating The Transaction Table.
                                                                Transaction transaction = new Transaction();
                                                                transaction.setBookId(book1.getBookId());
                                                                Date d = Constants.todaysDate ;
                                                                d= DbOperations.addDays(d,30);
                                                                transaction.setDueDate(dateToString.format(d));
                                                                transaction.setFine(0);
                                                                transaction.setIssueDate(dateToString.format(Constants.todaysDate));
                                                                transaction.setRenewCount(0);
                                                                transaction.setUid(currentUserId);
                                                                transaction.setBook(book1);
                                                                DbOperations.addTransaction(transaction);

                                                            book1.setStatus("Due Date is "+ new SimpleDateFormat("MM/dd/yyyy").format(d));
                                                            checkedOutStatus.add(book1);
                                                            successfulCheckedCount++;

                                                            //Email code
                                                            emailData.add(new VerificationEmailData(book1.getTitle()
                                                                            ,new SimpleDateFormat("MM/dd/yyyy").format(d)
                                                                            ,new SimpleDateFormat("MM/dd/yyyy").format(Constants.todaysDate)
                                                                            ,new SimpleDateFormat("HH:mm").format(Constants.todaysDate) ));

                                                            SharedPreferences SP;
                                                            SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                                                            String currentCartItems = SP.getString(CurrentUser.UID, null);
                                                            String bookToRemove=book1.getBookId();
                                                            if(bookToRemove!=null && currentCartItems!=null){
                                                                String newCartItems = currentCartItems.replaceAll(bookToRemove+",","").replaceAll(""+","+bookToRemove,"").replaceAll(bookToRemove, "");
                                                                SharedPreferences.Editor edit = SP.edit();
                                                                edit.putString (CurrentUser.UID, newCartItems);
                                                                Log.v("SHARED PREFERENCE:", newCartItems);
                                                                edit.commit();
                                                            }

                                                        }

                                                        if(emailData != null){
                                                            String emailMessage = emailContentBuider(emailData);
                                                            ShoppingCartActivity.AsyncTaskRunner emailSender = new ShoppingCartActivity.AsyncTaskRunner();
                                                            emailSender.execute(CurrentUser.EMAIL, emailMessage);
                                                        }

                                                        // Updating User Checkedout
                                                        HashMap<String, Object> hm = new HashMap<String,Object>();
                                                        hm.put(Constants.CheckedOutBooks,String.valueOf(checkOutBooks+successfulCheckedCount));
                                                        if(DbOperations.checkEqualDay(lastCheckedOutDay,Constants.todaysDate)){
                                                            hm.put(Constants.LAST_CHECKOUT_DAY_COUNT,String.valueOf(lastCheckoutDayCount+successfulCheckedCount));
                                                        }else{
                                                            hm.put(Constants.LAST_CHECKOUT_DAY_COUNT,String.valueOf(successfulCheckedCount));
                                                            hm.put(Constants.LAST_CHECKED_OUT_DAY,dateToString.format(Constants.todaysDate));
                                                            System.out.println("Updated Date======="+dateToString.format(Constants.todaysDate));
                                                        }
                                                        DocumentReference currentUserDocument = FirebaseFirestore.getInstance().document(Constants.USER_COLLECTION+ "/" + currentUserId);
                                                        currentUserDocument.
                                                                update(hm)
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        Log.v("User No Of Books Update", "Successful");
                                                                    }
                                                                }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Log.w(TAG, "Error writing document No Of Books", e);
                                                            }
                                                        });

                                                    Intent intent = new Intent(ShoppingCartActivity.this, CheckoutStatusActivity.class);
                                                    if(checkedOutStatus.size()>=1){
                                                        intent.putExtra("book1",checkedOutStatus.get(0));
                                                    }else{
                                                        intent.putExtra("book1","");
                                                    }

                                                    if(checkedOutStatus.size()>=2){
                                                        intent.putExtra("book2",checkedOutStatus.get(1));
                                                    }else{
                                                        intent.putExtra("book2","");
                                                    }

                                                    if(checkedOutStatus.size()>=3){
                                                        intent.putExtra("book3",checkedOutStatus.get(2));
                                                    }else{
                                                        intent.putExtra("book3","");
                                                    }
                                                    startActivity(intent);
                                                }
                                            }
                                        }
                                    });
                                }

                            }
                        }
                    }
                });
                // End Harshit



            }
        });

    }


    //shaurya



    @Override
    public void onResume() {
        super.onResume();

        adapter = null;
        bookIds = null;
        cartItems = new ArrayList<BookSearchItem>();
        if(CurrentUser.UID==null) return;

        SharedPreferences SP;
        SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String currentCartItems = SP.getString(CurrentUser.UID, null);

        if(currentCartItems == null || currentCartItems.equals("")){
            ((TextView) findViewById(R.id.emptyCartText)).setText("Your Cart is Empty!");
            return;
        }

        else{
            bookIds = currentCartItems.split(",");
        }

        adapter = new ShoppingCartAdapter(ShoppingCartActivity.this, cartItems);
        recyclerView.setAdapter(adapter);


        for(int i=0; i<bookIds.length; i++){
            if(bookIds==null || bookIds[i]==null||bookIds[i].trim().equals("")) continue;
            DocumentReference docRef = db.collection(BOOKS_COLLECTION).document(bookIds[i]);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null) {
                            BookSearchItem b1= new BookSearchItem(
                                    document.getString("author")
                                    , document.getString("title")
                                    , document.getString("bookId")
                                    , document.getDouble("yearOfPub").intValue());

                            b1.setBookId(document.getId());
                            b1.setBook(document.toObject(Book.class));
                            b1.setBookId(document.getId());
                            loadImage(document.getId(), b1);

                            Log.d("DOCUMENT SNAPSHOT", "DocumentSnapshot data: " + task.getResult().getData());
                        } else {
                            Log.d("DOCUMENT SNAPSHOT", "No such document");
                        }
                    } else {
                        Log.d("DOCUMENT SNAPSHOT", "get failed with ", task.getException());
                    }
                }
            });
        }

    }

    private String emailContentBuider(ArrayList<VerificationEmailData> emailData){
        StringBuilder message = new StringBuilder("Here are the the details of your checked out books at " + emailData.get(0).getCheckoutDate() + ", " + emailData.get(0).getCheckoutTime() + ":\n\n");

        for(VerificationEmailData data :  emailData){
            message.append("Book Name: " + data.getBookName() + "\n");
            message.append("Due Date: " + data.getDueDate() + "\n\n");
        }

        message.append("Thank you!");

        return message.toString();
    }

    private class AsyncTaskRunner extends AsyncTask<String, String, String> {


        @Override
        protected String doInBackground(String... params) {
            try {
                Log.v("EMAIL SENT TO:", params[0]);
                MailUtility.sendMail(params[0], params[1]);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return "Successful";
        }


        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(ShoppingCartActivity.this, "Verification Email Sent", Toast.LENGTH_SHORT).show();
        }

    }

    public void loadImage(String bookId, final BookSearchItem b1){

        storage = FirebaseStorage.getInstance();

        StorageReference storageRef = storage.getReference();

        storageRef.child(Constants.IMAGE_FOLDER_PATH + bookId + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                try {

                    new ShoppingCartActivity.DownLoadImageTask(b1).execute(uri.toString());

                } catch (Exception e) {
                    Toast.makeText(ShoppingCartActivity.this, "Unable to load image from URI", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    Resources resources = ShoppingCartActivity.this.getResources();
                    uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + resources.getResourcePackageName(R.drawable.bookcover) + '/' + resources.getResourceTypeName(R.drawable.bookcover) + '/' + resources.getResourceEntryName(R.drawable.bookcover) );
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        b1.setBookBitmap(bitmap);
                        cartItems.add(b1);
                        adapter.notifyDataSetChanged();

                    } catch (IOException e1) {
                        Toast.makeText(ShoppingCartActivity.this, "Problem loading image", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                try {
                    Resources resources = ShoppingCartActivity.this.getResources();
                    Uri uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + resources.getResourcePackageName(R.drawable.bookcover) + '/' + resources.getResourceTypeName(R.drawable.bookcover) + '/' + resources.getResourceEntryName(R.drawable.bookcover) );
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    b1.setBookBitmap(bitmap);
                    cartItems.add(b1);
                    adapter.notifyDataSetChanged();

                } catch (IOException e1) {
                    Toast.makeText(ShoppingCartActivity.this, "Problem loading image", Toast.LENGTH_SHORT).show();
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
                startActivity(new Intent(ShoppingCartActivity.this, LoginActivity.class));
                break;
            case R.id.view_cart_option:
                startActivity(new Intent(ShoppingCartActivity.this, ShoppingCartActivity.class));
                break;

            case R.id.homePageRedirect:
                startActivity(new Intent(ShoppingCartActivity.this, ViewBooksActivity.class));
                return true;


        }

        return super.onOptionsItemSelected(item);

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
            cartItems.add(b1);
            adapter.notifyDataSetChanged();
        }
    }

}
