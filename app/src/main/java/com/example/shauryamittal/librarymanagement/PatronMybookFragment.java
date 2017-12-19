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
import android.widget.Button;
import android.widget.Toast;

import com.example.shauryamittal.librarymanagement.model.Book;
import com.example.shauryamittal.librarymanagement.model.Constants;
import com.example.shauryamittal.librarymanagement.model.CurrentUser;
import com.example.shauryamittal.librarymanagement.model.DbOperations;
import com.example.shauryamittal.librarymanagement.model.Transaction;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;


public class PatronMybookFragment extends Fragment {
    RecyclerView recyclerView;
    PatronMybookAdapter csAdapter;
    List<Transaction> transactionList = new ArrayList<>();
    static FirebaseFirestore db = FirebaseFirestore.getInstance();
    String[] returnBookIdArray;
    Button returnBook;
    int checkOutBooks;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_patron_mybook,container,false);

        recyclerView=(RecyclerView)view.findViewById(R.id.patronMyBookRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        csAdapter= new PatronMybookAdapter(getContext(),transactionList);
        recyclerView.setAdapter(csAdapter);
        getPatronMybookListFromTransaction();
        returnBook=(Button)view.findViewById(R.id.patron_mybook_return_book1);

        returnBook.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(csAdapter.checkedBookList==null ||csAdapter.checkedBookList.trim().equals("")){
                    Toast toast = Toast.makeText(getContext(), "Please check atlease 1 book", Toast.LENGTH_SHORT);
                   toast.show();
                }else{
                    if(csAdapter.checkedBookList.charAt(0)==',') csAdapter.checkedBookList=csAdapter.checkedBookList.substring(1);
                    returnBookIdArray=csAdapter.checkedBookList.split(",");




                    //1 count decrease in user.
                    DocumentReference mRefUser = db.collection("users").document(CurrentUser.UID);
                    mRefUser.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                             @Override
                                                             public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                 if (task.isSuccessful()) {
                                                                     DocumentSnapshot doc = task.getResult();
                                                                     checkOutBooks = Integer.parseInt(doc.getString(Constants.CheckedOutBooks));
                                                                     DocumentReference currentUserDocument = FirebaseFirestore.getInstance().document(Constants.USER_COLLECTION+ "/" + CurrentUser.UID);
                                                                     currentUserDocument.
                                                                             update(Constants.CheckedOutBooks,String.valueOf(checkOutBooks-returnBookIdArray.length))
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
                                                                 }
                                                             }
                                                         });

                    //2 delete entry in transaction.
                    for(int i=0;i<returnBookIdArray.length;i++){
                        db.collection("transaction")
                                .whereEqualTo("uid", CurrentUser.UID).whereEqualTo("bookId",returnBookIdArray[i])
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (DocumentSnapshot document : task.getResult()) {
                                               // DbOperations.deleteTransaction(document.getId());
                                                Log.d(TAG, document.getId() + " => " + document.getData());
                                            }
                                        } else {
                                            Log.d(TAG, "Error getting documents: ", task.getException());
                                        }
                                    }
                                });
                    }
                    //3 change count in book or assign to user.

                    





                }
            }
        });



        return view;
    }

    public void getPatronMybookListFromTransaction(){
        db.collection("transaction")
                .whereEqualTo("uid", CurrentUser.UID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                document.getId();
                                Transaction transaction = document.toObject(Transaction.class);
                                transactionList.add(transaction);
                                csAdapter.notifyDataSetChanged();

                            }
                        } else {
                            //Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }




}
