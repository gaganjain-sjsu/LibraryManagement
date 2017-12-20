package com.example.shauryamittal.librarymanagement;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
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
import com.example.shauryamittal.librarymanagement.model.ClearedWaitlist;
import com.example.shauryamittal.librarymanagement.model.Constants;
import com.example.shauryamittal.librarymanagement.model.CurrentUser;
import com.example.shauryamittal.librarymanagement.model.DbOperations;
import com.example.shauryamittal.librarymanagement.model.MailUtility;
import com.example.shauryamittal.librarymanagement.model.Transaction;
import com.example.shauryamittal.librarymanagement.model.VerificationEmailData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    SimpleDateFormat dateToString = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

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
                                                DbOperations.deleteTransaction(document.getId());
                                                Log.d(TAG, document.getId() + " => " + document.getData());
                                            }
                                        } else {
                                            Log.d(TAG, "Error getting documents: ", task.getException());
                                        }
                                    }
                                });
                        //3 change count in book or assign to user.

                        DocumentReference mRef = db.collection("books").document(returnBookIdArray[i]);
                        mRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot doc = task.getResult();
                                    Book book = doc.toObject(Book.class);
                                    book.setBookId(doc.getId());
                                    if(book.getWailistedUsers()==null||book.getWailistedUsers().trim().equals("")){
                                       book.setNoOfCheckedOutCopy(book.getNoOfCheckedOutCopy()-1);
                                        DbOperations.updateBook(book);
                                    }else{
                                        String allWaitlistedUsers=book.getWailistedUsers();
                                        String assignedUser="";
                                        if(!allWaitlistedUsers.contains(",")){
                                            assignedUser=allWaitlistedUsers;
                                        }else{
                                            assignedUser=allWaitlistedUsers.substring(0,allWaitlistedUsers.indexOf(","));
                                        }
                                        allWaitlistedUsers=allWaitlistedUsers.replaceAll(assignedUser+",","").replaceAll(","+assignedUser,"").replaceAll(assignedUser,"");
                                        book.setWailistedUsers(allWaitlistedUsers);
                                        DbOperations.updateBook(book);
                                        ClearedWaitlist clearedWaitlist= new ClearedWaitlist();
                                        clearedWaitlist.setBookId(book.getBookId());
                                        clearedWaitlist.setUid(assignedUser);
                                        clearedWaitlist.setClearDate(dateToString.format(Constants.todaysDate));
                                        clearedWaitlist.setLastDateToAcceptBook(dateToString.format(DbOperations.addDays(Constants.todaysDate,3)));
                                        DbOperations.addClearedWaitlist(clearedWaitlist);
                                    }
                                }
                            }
                        });



                    }

            //Updating current View
                    String currCheckedBook=csAdapter.checkedBookList;
                    StringBuilder emailMessage=new StringBuilder("Books return successful. Details of transactions are:"+"\n\n\n");
                    List<Transaction> temp = new ArrayList<>();
                    for(Transaction trans:transactionList){
                        if(!currCheckedBook.contains(trans.getBookId())){
                            temp.add(trans);
                        }else{
                            emailMessage.append("Book Name: " + trans.getBook().getTitle() + "\n");
                            emailMessage.append("Book Author: " + trans.getBook().getAuthor() + "\n");
                            Date dueDate= new Date();
                            try {
                                dueDate=dateToString.parse(trans.getDueDate());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            emailMessage.append("Fine on this book is: " + DbOperations.calculateFine(dueDate) + "\n\n");
                        }
                    }

                    //Sending EmailStatus
                    emailMessage.append("\n\n   Thank You!! ");
                    PatronMybookFragment.AsyncTaskRunner emailSender = new PatronMybookFragment.AsyncTaskRunner();
                    emailSender.execute(CurrentUser.EMAIL, emailMessage.toString());


                    //transactionList=temp;
                    transactionList.clear();
                    transactionList.addAll(temp);
                    csAdapter.notifyDataSetChanged();
                    csAdapter.checkedBookList="";
                    Toast toast = Toast.makeText(getContext(), "Return successful", Toast.LENGTH_SHORT);
                    toast.show();



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
    }



}
