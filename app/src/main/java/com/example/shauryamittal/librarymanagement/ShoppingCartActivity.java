package com.example.shauryamittal.librarymanagement;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.example.shauryamittal.librarymanagement.model.CurrentUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCartActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    public static RecyclerView.Adapter adapter;
    public static List<CartItem> cartItems = new ArrayList<CartItem>();
    String bookIds[];
    static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String BOOKS_COLLECTION = "books";
    private static final String USER_COLLECTION = "users";
    private static final String BOOK_TITLE = "title";
    private static final String BOOK_AUTHOR = "author";
    TextView remove;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

        recyclerView = (RecyclerView) findViewById(R.id.cart_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        remove = (TextView) findViewById(R.id.remove);

        SharedPreferences SP;
        SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String currentCartItems = SP.getString(CurrentUser.UID, null);
//        Log.d("current Items ", currentCartItems);

        if(currentCartItems != null){
            bookIds = currentCartItems.split(",");
        }

        else{
            ((TextView) findViewById(R.id.emptyCartText)).setText("Your Cart is Empty!");
            return;
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
            adapter.notifyDataSetChanged();

        }

        //cartItems = new ArrayList<>();

    }
}
