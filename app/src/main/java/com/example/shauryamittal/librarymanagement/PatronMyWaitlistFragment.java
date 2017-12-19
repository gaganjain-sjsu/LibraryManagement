package com.example.shauryamittal.librarymanagement;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.shauryamittal.librarymanagement.model.Book;
import com.example.shauryamittal.librarymanagement.model.Constants;
import com.example.shauryamittal.librarymanagement.model.CurrentUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class PatronMyWaitlistFragment extends Fragment {
    RecyclerView recyclerView;
    PatronMywaitlistAdapter csAdapter;
    List<Book> bookList = new ArrayList<>();
    static FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_patron_my_waitlist,container,false);

        recyclerView=(RecyclerView)view.findViewById(R.id.patronMyWaitlistRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        csAdapter= new PatronMywaitlistAdapter(getContext(),bookList);
        recyclerView.setAdapter(csAdapter);
        getPatronMywaitlistFromDb();

        return view;
    }

    public void getPatronMywaitlistFromDb(){
        db.collection("users")
                .whereEqualTo("uid", CurrentUser.UID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                document.getId();
                                String waitlistedbook=document.getString(Constants.USER_WAITLISTED_BOOKS_KEY);
                                if(waitlistedbook==null||waitlistedbook.trim().equals("")){
                                    Toast toast = Toast.makeText(getContext(), "No books in Waitlist", Toast.LENGTH_SHORT);
                                    toast.show();
                                }else{
                                    waitlistedbook=waitlistedbook.replaceAll(",,",",");
                                    String[] waitlistBookStrArr=waitlistedbook.split(",");

                                    for(int i=0;i<waitlistBookStrArr.length;i++){
                                        DocumentReference mRef = db.collection("books").document(waitlistBookStrArr[i]);
                                        mRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot doc = task.getResult();
                                                    Book book = doc.toObject(Book.class);
                                                    book.setBookId(doc.getId());
                                                    bookList.add(book);
                                                    csAdapter.notifyDataSetChanged();
                                                }


                                            }

                                        });

                                    }
                                }

                            }
                        } else {
                            //Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}
