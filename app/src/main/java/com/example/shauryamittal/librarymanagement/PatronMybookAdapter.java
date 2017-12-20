package com.example.shauryamittal.librarymanagement;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shauryamittal.librarymanagement.model.Book;
import com.example.shauryamittal.librarymanagement.model.Constants;
import com.example.shauryamittal.librarymanagement.model.CurrentUser;
import com.example.shauryamittal.librarymanagement.model.DbOperations;
import com.example.shauryamittal.librarymanagement.model.Transaction;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Date;

/**
 * Created by Harshit on 12/5/17.
 */

public class PatronMybookAdapter extends RecyclerView.Adapter<PatronMybookAdapter.PatronMybookViewHolder>  {


    private Context ctx;
    private List<Transaction> transactionList;
    private String currentBookId="";
    private int currPosition;
    private String tempBookId="";
    private Transaction tempTransaction;
    static FirebaseFirestore db = FirebaseFirestore.getInstance();
    SimpleDateFormat dateToString = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    //private Book tempBook;
    public static String checkedBookList="";

    public PatronMybookAdapter(Context ctx, List<Transaction> transactionlist) {
        this.ctx = ctx;
        transactionList = transactionlist;
        //checkedBookList="";
    }

    @Override
    public PatronMybookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View view = inflater.inflate(R.layout.patron_mybook_item,null);
        return new PatronMybookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PatronMybookViewHolder holder, int position) {

        holder.bind(transactionList.get(position));

    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    class PatronMybookViewHolder extends RecyclerView.ViewHolder{
        Transaction transaction;
        ImageView coverImage;
        TextView title,author,dueDate, fine;
        CheckBox checkBook;
        Button reissueBook;
        public PatronMybookViewHolder(View itemView) {
            super(itemView);
            coverImage=itemView.findViewById(R.id.patron_mybook_book_img);
            title=itemView.findViewById(R.id.patron_mybook_book_name);
            author=itemView.findViewById(R.id.patron_mybook_author);
            dueDate=itemView.findViewById(R.id.patron_mybook_duedate);
            //fine=itemView.findViewById(R.id.patron_mybook_fine);

            checkBook= itemView.findViewById(R.id.patron_mybook_checkBox);
            reissueBook=itemView.findViewById(R.id.patron_mybook_reissue_book);
        }

        public void bind(Transaction transact){

            transaction = transact;
            Book book=transaction.getBook();
            //tempBook=book;
            title.setText(book.getTitle());
            author.setText(book.getAuthor());
            dueDate.setText("Due Date is "+transaction.getDueDate());
            //fine.setText("Fine on book is $"+transaction.getFine());

            checkBook.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                {
                    if ( isChecked )
                    {
                        if(checkedBookList==null ||checkedBookList.trim().equals("")){
                            checkedBookList=transaction.getBook().getBookId();
                        }else{
                            checkedBookList=checkedBookList+","+transaction.getBook().getBookId();
                        }
                    }else{
                        if(checkedBookList==null){
                            checkedBookList="";
                        }else{
                            checkedBookList=checkedBookList.replace(","+transaction.getBook().getBookId(),"").replace(transaction.getBook().getBookId()+",","").replace(transaction.getBook().getBookId(),"");
                        }
                    }

                    System.out.println("checkedBookList===="+checkedBookList);
                }
            });

            reissueBook.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if(transaction.getRenewCount()>=2){
                        Toast toast = Toast.makeText(ctx, "Cannot Renew More than 2 Times.", Toast.LENGTH_SHORT);
                        toast.show();
                    }else{
                        tempBookId=transaction.getBookId();
                        tempTransaction=transaction;
                        DocumentReference docRef = db.collection("books").document(tempBookId);
                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document != null) {
                                        Book b1 = document.toObject(Book.class);
                                        b1.setBookId(document.getId());
                                        if(b1.getWailistedUsers()==null || b1.getWailistedUsers().trim().equals("")){
                                            tempTransaction.setRenewCount(tempTransaction.getRenewCount()+1);
                                            Date dueDate= new Date();
                                            try {
                                                dueDate= dateToString.parse(tempTransaction.getDueDate());
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                            Date currentDate= Constants.todaysDate;
                                            if(currentDate.getTime()<dueDate.getTime()){
                                                tempTransaction.setDueDate(dateToString.format(DbOperations.addDays(currentDate,30)));
                                            }else{
                                                tempTransaction.setDueDate(dateToString.format(DbOperations.addDays(dueDate,30)));
                                            }
                                            DbOperations.updateTransaction(tempTransaction);
                                            Toast toast = Toast.makeText(ctx, "Book Renewed.", Toast.LENGTH_SHORT);
                                            toast.show();
                                            notifyDataSetChanged();

                                        }else{
                                            Toast toast = Toast.makeText(ctx, "Cannot Renew. Book has waitlist", Toast.LENGTH_SHORT);
                                            toast.show();
                                        }

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
            });
//
//            addToWaitList.setOnClickListener(new View.OnClickListener() {
//                public void onClick(View v) {
//                    SharedPreferences SP;
//                    SP = PreferenceManager.getDefaultSharedPreferences(ctx);
//                    String currentCartItems = SP.getString(CurrentUser.UID, null);
//                    String bookToRemove=mBook.getBookId();
//                    if(bookToRemove!=null && currentCartItems!=null){
//                        String newCartItems = currentCartItems.replaceAll(bookToRemove+",","").replaceAll(""+","+bookToRemove,"").replaceAll(bookToRemove, "");
//                        SharedPreferences.Editor edit = SP.edit();
//                        edit.putString (CurrentUser.UID, newCartItems);
//                        Log.v("SHARED PREFERENCE:", newCartItems);
//                        edit.commit();
//                    }
//
//                    notifyDataSetChanged();
//
//                }
//            });
        }
    }
}
