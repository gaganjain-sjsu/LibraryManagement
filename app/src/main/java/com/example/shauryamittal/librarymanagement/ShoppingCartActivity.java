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
    public static RecyclerView.Adapter adapter;
    public static List<CartItem> cartItems = new ArrayList<CartItem>();
    String bookIds[];
    private String BOOKS_COLLECTION = "books";
    private static final String USER_COLLECTION = "users";
    private static final String BOOK_TITLE = "title";
    private static final String BOOK_AUTHOR = "author";
    private static final String BOOKS_ISSUED_COLLECTION = "booksIssued";
    TextView remove;
    Button checkout;
    static FirebaseFirestore db = FirebaseFirestore.getInstance();
    public ArrayList<Book> checkedOutBooks= new ArrayList<Book>();
    public int currentCheckoutSize=0;
    public boolean canBeCheckedOut=true;
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
        remove = (TextView) findViewById(R.id.remove);
        checkout = (Button) findViewById(R.id.checkout);


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
                docRef
                        .update("issuedbooks", cartItems.get(0).getBookId())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully updated!");
                                Toast.makeText(ShoppingCartActivity.this, "Book Checked Out", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onResume() {
        super.onResume();

        adapter = null;
        bookIds = null;
        cartItems = new ArrayList<CartItem>();

        SharedPreferences SP;
        SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String currentCartItems = SP.getString(CurrentUser.UID, null);
//        Log.d("current Items ", currentCartItems);

        if(currentCartItems == null || currentCartItems.equals("")){
            ((TextView) findViewById(R.id.emptyCartText)).setText("Your Cart is Empty!");
            return;
        }

        else{
            bookIds = currentCartItems.split(",");
        }

        adapter = new CartAdapter(cartItems, this);
        recyclerView.setAdapter(adapter);


        for(int i=0; i<bookIds.length; i++){
            DocumentReference docRef = db.collection(BOOKS_COLLECTION).document(bookIds[i]);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null) {
                            cartItems.add(new CartItem(document.getId(), document.getString(BOOK_TITLE), document.getString(BOOK_AUTHOR)));
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
