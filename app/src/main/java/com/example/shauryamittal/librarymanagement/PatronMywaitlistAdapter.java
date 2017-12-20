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
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shauryamittal.librarymanagement.model.Book;
import com.example.shauryamittal.librarymanagement.model.Constants;
import com.example.shauryamittal.librarymanagement.model.CurrentUser;
import com.example.shauryamittal.librarymanagement.model.Transaction;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by Harshit on 12/5/17.
 */

public class PatronMywaitlistAdapter extends RecyclerView.Adapter<PatronMywaitlistAdapter.PatronMywaitlistViewHolder>  {


    private Context ctx;
    private List<Book> bookList;
    private String currentBookId="";
    private int currPosition;
    static FirebaseFirestore db = FirebaseFirestore.getInstance();

    public PatronMywaitlistAdapter(Context ctx, List<Book> booklist) {
        this.ctx = ctx;
        bookList = booklist;
    }

    @Override
    public PatronMywaitlistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View view = inflater.inflate(R.layout.patron_mywaitlist_item,null);
        return new PatronMywaitlistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PatronMywaitlistViewHolder holder, int position) {

        holder.bind(bookList.get(position));

    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    class PatronMywaitlistViewHolder extends RecyclerView.ViewHolder{
        Book book;
        ImageView coverImage;
        TextView title,author,yearOfPub;
        Button remove,waitlistClearRemove, checkout;
        public PatronMywaitlistViewHolder(View itemView) {
            super(itemView);
            coverImage=itemView.findViewById(R.id.patron_mywaitlist_book_img);
            title=itemView.findViewById(R.id.patron_mywaitlist_book_name);
            author=itemView.findViewById(R.id.patron_mywaitlist_author);
            yearOfPub=itemView.findViewById(R.id.patron_mywaitlist_yearOfPub);
            remove= itemView.findViewById(R.id.patron_mywaitlist_remove_book);
//            waitlistClearRemove= itemView.findViewById(R.id.patron_mywaitlist_waitlist_clear_remove_book);
//            checkout= itemView.findViewById(R.id.patron_mywaitlist_checkout_book);
        }

        public void bind(Book book1){

            book = book1;
            title.setText(book.getTitle());
            author.setText(book.getAuthor());
            yearOfPub.setText("Publication Year: "+book.getYearOfPub());

            remove.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //Remove from userList
                    final DocumentReference userRef = db.document(Constants.USER_COLLECTION + "/" + CurrentUser.UID);
                    userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document != null) {
                                    String waitlist = document.getString(Constants.USER_WAITLISTED_BOOKS_KEY);
                                    waitlist=waitlist.replace(book.getBookId()+",","").replace(","+book.getBookId(),"").replace(book.getBookId(),"");
                                    //String newWaitList = removeEntry(waitlist, book.getBookId());
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
                                    Log.d("Document Data", "DocumentSnapshot data: " + task.getResult().getData());
                                } else {
                                    Log.d("NOT FOUND", "No such document");
                                }
                            } else {
                                Log.d(TAG, "get failed with ", task.getException());
                            }
                        }
                    });

//removing bookid from user object


                    final DocumentReference userRef1 = db.document(Constants.BOOKS_COLLECTION + "/" + book.getBookId());
                    userRef1.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document != null) {

                                    Book b1 = document.toObject(Book.class);
                                    b1.setBookId(document.getId());
                                    String waitlist = b1.getWailistedUsers();
                                    waitlist=waitlist.replace(CurrentUser.UID+",","").replace(","+CurrentUser.UID,"").replace(CurrentUser.UID,"");
                                    userRef1.update("wailistedUsers", waitlist).addOnCompleteListener(new OnCompleteListener<Void>() {
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
                                    Log.d("Document Data", "DocumentSnapshot data: " + task.getResult().getData());
                                } else {
                                    Log.d("NOT FOUND", "No such document");
                                }
                            } else {
                                Log.d(TAG, "get failed with ", task.getException());
                            }
                        }
                    });







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
//                    mBookList.remove(mBook);
//                    notifyDataSetChanged();
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
