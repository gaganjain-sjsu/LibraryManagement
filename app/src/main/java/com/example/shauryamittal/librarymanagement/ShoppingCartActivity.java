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
import com.example.shauryamittal.librarymanagement.model.CurrentUser;
import com.example.shauryamittal.librarymanagement.model.MailUtility;
import com.example.shauryamittal.librarymanagement.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
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
    public int currentCheckoutSize = 0;
    public boolean canBeCheckedOut = true;
    String currentUserId;
    User currentUserDetails;
    String[] bookKeyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

        recyclerView = (RecyclerView) findViewById(R.id.cart_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //remove = (TextView) findViewById(R.id.remove);
        checkout = (Button) findViewById(R.id.checkoutCart);

        /*
        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ShoppingCartActivity.this, "Checkout", Toast.LENGTH_SHORT).show();


                //harshit



                SharedPreferences SP;
                SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                String currentCartItems = SP.getString(CurrentUser.UID, null);
                //String currentCartItems = "u5ENASVZU2Cfb4wKKWv8";

                //currentUserId="yiYWVwVAVSeVEsNA593081Xdh2e2";
                currentUserId=CurrentUser.UID;
                bookKeyList = currentCartItems.split(",");
                currentCheckoutSize=bookKeyList.length;
                DocumentReference mRefUser = db.collection("users").document(currentUserId);
                mRefUser.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            User user = doc.toObject(User.class);
                            currentUserDetails=user;
                            if((user.getCheckOutBooks()+currentCheckoutSize)>9){
                                String tostString="Cannot Checkout. " + user.getCheckOutBooks() + " Books Already Taken by Patron";
                                Toast toast = Toast.makeText(getApplicationContext(), tostString, Toast.LENGTH_SHORT);
                                toast.show();
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
                                                                db.collection("books").document(book1.getBookId())
                                                                        .set(book1)
                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {
                                                                                Log.d(TAG, "DocumentSnapshot successfully written!");
                                                                            }
                                                                        })
                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                            @Override
                                                                            public void onFailure(@NonNull Exception e) {
                                                                                Log.w(TAG, "Error writing document", e);
                                                                            }
                                                                        });

                                                            } else {
                                                                Log.w(TAG, "Book Id in null in book object. So cannot update");
                                                            }
                                                        }

                                                        // Updating User Checkedout
                                                        currentUserDetails.setCheckOutBooks(currentUserDetails.getCheckOutBooks()+currentCheckoutSize);
                                                        db.collection("users").document(currentUserId)
                                                                .set(currentUserDetails)
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        Log.d(TAG, "DocumentSnapshot successfully written!");
                                                                    }
                                                                })
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        Log.w(TAG, "Error writing document", e);
                                                                    }
                                                                });


                                                        //end Updating userChecked books
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

                //harshit










            }
        });*/

        //shaurya

        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                db.collection("users").document(CurrentUser.UID)
//                        .set("issuedbooks", cartItems.get(0))
//                        .addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//                                Log.d(TAG, "DocumentSnapshot successfully written!");
//                            }
//                        })
//                        .addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Log.w(TAG, "Error writing document", e);
//                            }
//                        });

                DocumentReference docRef = db.collection("users").document(CurrentUser.UID);

                // Set the "isCapital" field of the city 'DC'
                docRef.update("issuedbooks", cartItems.get(0).getBookId())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully updated!");
                                Toast.makeText(ShoppingCartActivity.this, "Book Checked Out", Toast.LENGTH_SHORT).show();
                                //MailUtility.sendMail(CurrentUser.EMAIL, "Your book "+ cartItems.get(0).getBookName()+ " has been issued");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error updating document", e);
                            }
                        });


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
