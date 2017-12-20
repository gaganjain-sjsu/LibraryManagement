package com.example.shauryamittal.librarymanagement;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shauryamittal.librarymanagement.model.Book;
import com.example.shauryamittal.librarymanagement.model.ClearedWaitlist;
import com.example.shauryamittal.librarymanagement.model.Constants;
import com.example.shauryamittal.librarymanagement.model.DbOperations;
import com.example.shauryamittal.librarymanagement.model.Transaction;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by Harshit on 12/5/17.
 */

public class LibrarianSearchAdapter extends RecyclerView.Adapter<LibrarianSearchAdapter.LibrarianSearchViewHolder>  {


    private Context ctx;
    private List<BookSearchItem> mBookList;
    private String currentBookId="";
    private int currPosition;
    private ArrayList<ClearedWaitlist> clearedListTrans= new ArrayList<ClearedWaitlist>();
    private BookSearchItem tempSearchItem;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String clearBookId="";

    public LibrarianSearchAdapter(Context ctx, List<BookSearchItem> bookList) {
        this.ctx = ctx;
        mBookList = bookList;
    }

    @Override
    public LibrarianSearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View view = inflater.inflate(R.layout.librarian_search_layout,null);
        return new LibrarianSearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LibrarianSearchViewHolder holder, int position) {

        holder.bind(mBookList.get(position));

    }

    @Override
    public int getItemCount() {
        return mBookList.size();
    }

    class LibrarianSearchViewHolder extends RecyclerView.ViewHolder{

        Book mBook;
        ImageView coverImage;
        TextView title,author,yearOfPub;
        Button delete, update;
        public LibrarianSearchViewHolder(View itemView) {
            super(itemView);
            coverImage=itemView.findViewById(R.id.bookCoverImage);
            title=itemView.findViewById(R.id.textViewTitle);
            author=itemView.findViewById(R.id.textViewAuthor);
            yearOfPub=itemView.findViewById(R.id.textViewYearOfPub);
            delete= itemView.findViewById(R.id.deleteBook);
            update=itemView.findViewById(R.id.updateSeatchBook);

        }

        public void bind(BookSearchItem book){
            tempSearchItem=book;
            mBook = book.getBook();
            title.setText(book.getTitle());
            author.setText(book.getAuthor());
            yearOfPub.setText(String.valueOf(book.getYearOfPub()));
            coverImage.setImageBitmap(book.getBookBitmap());

            delete.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {


                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctx);

                    alertDialogBuilder.setMessage("Are you sure you want to delete the book?").setCancelable(false);

                    alertDialogBuilder.setPositiveButton("Delete",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {


                                    db.collection("clearedwaitlist")
                                            .whereEqualTo("bookId", mBook.getBookId()).limit(1)
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        for (DocumentSnapshot document : task.getResult()) {
                                                            ClearedWaitlist clearedWaitlist = document.toObject(ClearedWaitlist.class);
                                                            clearedWaitlist.setClearDate(document.getId());
                                                            clearedListTrans.add(clearedWaitlist);
                                                        }
                                                    } else {

                                                    }
                                                    if ((mBook.getNoOfCheckedOutCopy() -clearedListTrans.size())> 0) {
                                                        Toast.makeText(ctx, "Cannot be deleted as the book has been issued by patrons", Toast.LENGTH_LONG).show();
                                                        return;
                                                    }

                                                    DbOperations.dropWaitList(mBook);
                                                    DbOperations.deleteBook(mBook.getBookId());
                                                    if(clearedListTrans.size()>0){
                                                        DbOperations.deleteClearedwaitList(clearedListTrans.get(0).getClearDate());
                                                        //this.dropWaitList(clearedListTrans.get(0).getBookId(),clearedListTrans.get(0).getUid());
                                                        clearBookId=clearedListTrans.get(0).getBookId();


                                                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                                                        final DocumentReference userRef = db.document(Constants.USER_COLLECTION + "/" + clearedListTrans.get(0).getUid());
                                                        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                if (task.isSuccessful()) {
                                                                    DocumentSnapshot document = task.getResult();
                                                                    if (document != null) {
                                                                        String waitlist = document.getString(Constants.USER_WAITLISTED_BOOKS_KEY);
                                                                        if(waitlist.contains(clearBookId)){
                                                                            waitlist=waitlist.replace(","+clearBookId,"").replace(clearBookId+",","").replace(clearBookId,"");
                                                                            userRef.update(Constants.USER_WAITLISTED_BOOKS_KEY, waitlist).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if(task.isSuccessful()){
                                                                                        Log.v("WAITLIST ", "WAITLIST UPDATED for user");
                                                                                    }
                                                                                    else {
                                                                                        Log.v("WAITLIST PROBLEM ", task.getException().getMessage());
                                                                                    }
                                                                                }
                                                                            }) ;

                                                                        }


                                                                        Log.d("Document Data", "DocumentSnapshot data: " + task.getResult().getData());
                                                                    } else {
                                                                        Log.d("NOT FOUND", "No such document");
                                                                    }
                                                                } else {
                                                                    Log.d(TAG, "get failed with ", task.getException());
                                                                }
                                                            }
                                                        });




                                                    }

                                                    mBookList.remove(tempSearchItem);
                                                    System.out.println("Inside delete button called.  Book Id=" + mBook.getTitle());
                                                    notifyDataSetChanged();


                                                }
                                            });





//                                    if (mBook.getNoOfCheckedOutCopy() != 0) {
//                                        Toast.makeText(ctx, "Cannot be deleted as the book has been issued by patrons", Toast.LENGTH_LONG).show();
//                                        return;
//                                    }
//
//                                    DbOperations.dropWaitList(mBook);
//                                    DbOperations.deleteBook(mBook.getBookId());
//
//
//                                    mBookList.remove(mBook);
//                                    System.out.println("Inside delete button called.  Book Id=" + mBook.getTitle());
//                                    notifyDataSetChanged();
                                }
                                });










                    alertDialogBuilder.setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();
                                    }
                                });

                                AlertDialog alert = alertDialogBuilder.create();
        alert.setTitle("Delete Book");
        alert.show();


                }
            });

            update.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    Intent intent = new Intent(ctx, LibrarianUpdateBook.class);
//                    EditText editText = (EditText) findViewById(R.id.editText);
//                    String message = editText.getText().toString();
//                    intent.putExtra(EXTRA_MESSAGE, message);
                    intent.putExtra("bookObj",mBook);
                    ctx.startActivity(intent);

                }
            });
        }
    }

    public static void dropWaitList(final String bookId, final String userId){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
            final DocumentReference userRef = db.document(Constants.USER_COLLECTION + "/" + userId);
            userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null) {
                            String waitlist = document.getString(Constants.USER_WAITLISTED_BOOKS_KEY);
                            if(waitlist.contains(bookId)){
                                waitlist=waitlist.replace(","+bookId,"").replace(bookId+",","").replace(bookId,"");
                                userRef.update(Constants.USER_WAITLISTED_BOOKS_KEY, waitlist).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Log.v("WAITLIST ", "WAITLIST UPDATED for user");
                                        }
                                        else {
                                            Log.v("WAITLIST PROBLEM ", task.getException().getMessage());
                                        }
                                    }
                                }) ;

                            }


                            Log.d("Document Data", "DocumentSnapshot data: " + task.getResult().getData());
                        } else {
                            Log.d("NOT FOUND", "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });



    }

}
