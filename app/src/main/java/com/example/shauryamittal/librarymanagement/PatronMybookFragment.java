package com.example.shauryamittal.librarymanagement;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.shauryamittal.librarymanagement.model.Book;
import com.example.shauryamittal.librarymanagement.model.CurrentUser;
import com.example.shauryamittal.librarymanagement.model.Transaction;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class PatronMybookFragment extends Fragment {
    RecyclerView recyclerView;
    PatronMybookAdapter csAdapter;
    List<Book> books = new ArrayList<>();
    static FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_patron_mybook,container,false);

        Book b1= new Book();
        b1.setTitle("aaaa");
        b1.setAuthor("hjj");
        books.add(b1);
        recyclerView=(RecyclerView)view.findViewById(R.id.patronMyBookRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        csAdapter= new PatronMybookAdapter(getContext(),books);
        recyclerView.setAdapter(csAdapter);

        return view;
    }
    //adapter.notifyDataSetChanged();
//    public void getPatronMybookListFromTransaction(){
//        db.collection("transaction")
//                .whereEqualTo("uid", CurrentUser.UID)
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (DocumentSnapshot document : task.getResult()) {
//                                document.getId();
//                                Transaction transaction = document.toObject(Transaction.class);
//                               // Log.d(TAG, document.getId() + " => " + document.getData());
//                            }
//                        } else {
//                            //Log.d(TAG, "Error getting documents: ", task.getException());
//                        }
//                    }
//                });
//    }
}
