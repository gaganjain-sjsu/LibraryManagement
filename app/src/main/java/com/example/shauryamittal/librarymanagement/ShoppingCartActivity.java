package com.example.shauryamittal.librarymanagement;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.content.ContentValues.TAG;

public class ShoppingCartActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    public static ShoppingCartAdapter adapter;
    public static List<Book> cartItems = new ArrayList<Book>();
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
    //public ArrayList<Book> eligibleToCheckoutBooks = new ArrayList<Book>();
    public int currentCheckoutSize = 0;
    public boolean canBeCheckedOut = true;
    String currentUserId;
    int checkOutBooks=0;
    Date lastCheckedOutDay;
    int lastCheckoutDayCount=0;
    User currentUserDetails;
    String[] bookKeyList;
    SimpleDateFormat dateToString = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

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
                            System.out.println("!!!!!!!lastCheckedOutDay===="+lastCheckedOutDay);
                            //System.out.println("!!!!!!!Email===="+doc.getString("checkOutBooks"));

                            if((checkOutBooks+currentCheckoutSize)>9){
                                String tostString="Cannot Checkout. " + checkOutBooks + " Books Already Issued. Remove "+(checkOutBooks+currentCheckoutSize-9)+" Books from cart";
                                Toast toast = Toast.makeText(getApplicationContext(), tostString, Toast.LENGTH_SHORT);
                                toast.show();
                            }else if(lastCheckedOutDay.getDate()==Constants.todaysDate.getDate() && (lastCheckoutDayCount+currentCheckoutSize)>3){
                                if(lastCheckoutDayCount==3){
                                    Toast toast = Toast.makeText(getApplicationContext(), "Reached Daily Checkout Limit", Toast.LENGTH_SHORT);
                                    toast.show();
                                }else{
                                    Toast toast = Toast.makeText(getApplicationContext(), "Daily Checkout limit is 3. Remove "+(lastCheckoutDayCount+currentCheckoutSize-3)+" books to checkout.", Toast.LENGTH_SHORT);
                                    toast.show();
                                }

                                System.out.println("!!!!!!!!!!!Todays Date=============");
                                return;
                            }else{
                                for (int i = 0; i < currentCheckoutSize; i++) {
                                    DocumentReference mRef = db.collection("books").document(bookKeyList[i]);
                                    mRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot doc = task.getResult();
                                                System.out.println(doc.getId());
                                                Book book = doc.toObject(Book.class);
                                                book.setBookId(doc.getId());
                                                checkedOutBooks.add(book);


                                                if(checkedOutBooks.size()==currentCheckoutSize){
                                                    for(Book book1 :checkedOutBooks){
                                                        if(book1.getNoOfCopy()<=book1.getNoOfCheckedOutCopy()){
                                                            canBeCheckedOut=false;
                                                            String tostStr="Book Unavailable. Book Title="+book1.getTitle()+" Publisher="+book1.getPublisher();
                                                            Toast toast = Toast.makeText(getApplicationContext(), tostStr, Toast.LENGTH_SHORT);
                                                            toast.show();
                                                            break;
                                                        }
                                                    }

                                                    if(canBeCheckedOut){
                                                        for(Book book1 :checkedOutBooks){
                                                            book1.setNoOfCheckedOutCopy(book1.getNoOfCheckedOutCopy()+1);
                                                            if (book.getBookId() != null) {
                                                                DbOperations.updateBook(book1);
                                                            } else {
                                                                Log.w(TAG, "Book Id in null in book object. So cannot update");
                                                            }
                                                        }

                                                        // Updating User Checkedout
                                                        DocumentReference currentUserDocument = FirebaseFirestore.getInstance().document(Constants.USER_COLLECTION+ "/" + currentUserId);
                                                        currentUserDocument.
                                                                update(Constants.CheckedOutBooks, String.valueOf(checkOutBooks+currentCheckoutSize))
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

    //                                                  Updating The Transaction Table.
                                                        for(Book book1 :checkedOutBooks){
                                                          Transaction transaction = new Transaction();
                                                            transaction.setBookId(book1.getBookId());
                                                            Date d = Constants.todaysDate ;
                                                            d.setTime(d.getTime()+ (30 * 1000 * 60 * 60 * 24));
                                                            transaction.setDueDate(dateToString.format(d));
                                                            transaction.setFine(0);
                                                            transaction.setIssueDate(dateToString.format(Constants.todaysDate));
                                                            transaction.setRenewCount(0);
                                                            transaction.setUid(currentUserId);
                                                            DbOperations.addTransaction(transaction);
                                                        }


                                                        Toast toast = Toast.makeText(getApplicationContext(), "Checked Out Succesful", Toast.LENGTH_SHORT);
                                                        toast.show();


                                                    }

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
        cartItems = new ArrayList<Book>();
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

        adapter = new ShoppingCartAdapter(this,cartItems);
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
                           Book b1 = document.toObject(Book.class);
                           b1.setBookId(document.getId());
                            cartItems.add(b1);
                            adapter.notifyDataSetChanged();

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
}
