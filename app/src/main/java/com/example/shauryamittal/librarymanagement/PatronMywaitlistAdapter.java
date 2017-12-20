package com.example.shauryamittal.librarymanagement;

import android.content.Context;
import android.content.Intent;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
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
    SimpleDateFormat dateToString = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

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
           // waitlistClearRemove= itemView.findViewById(R.id.patron_mywaitlist_waitlist_clear_remove_book);
            checkout= itemView.findViewById(R.id.patron_mywaitlist_checkout_book);
        }

        public void bind(Book book1) {

            book = book1;
            title.setText(book.getTitle());
            author.setText(book.getAuthor());
            yearOfPub.setText("Publication Year: " + book.getYearOfPub());

            if (book.getStatus()!=null && book.getStatus().trim().contains("clearedwaitlist")) {
                //book.setStatus("");
                remove.setVisibility(View.INVISIBLE);
            } else {
               // waitlistClearRemove.setVisibility(View.INVISIBLE);
                checkout.setVisibility(View.INVISIBLE);
            }


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
                                    waitlist = waitlist.replace(book.getBookId() + ",", "").replace("," + book.getBookId(), "").replace(book.getBookId(), "");
                                    //String newWaitList = removeEntry(waitlist, book.getBookId());
                                    userRef.update(Constants.USER_WAITLISTED_BOOKS_KEY, waitlist).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                bookList.remove(book);
                                                notifyDataSetChanged();
                                                Log.v("WAITLIST ", "WAITLIST UPDATED for user");
                                            } else {
                                                Log.v("WAITLIST PROBLEM ", task.getException().getMessage());
                                            }
                                        }
                                    });
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
                                    waitlist = waitlist.replace(CurrentUser.UID + ",", "").replace("," + CurrentUser.UID, "").replace(CurrentUser.UID, "");
                                    userRef1.update("wailistedUsers", waitlist).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.v("WAITLIST ", "WAITLIST UPDATED for user");
                                            } else {
                                                Log.v("WAITLIST PROBLEM ", task.getException().getMessage());
                                            }
                                        }
                                    });
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
            });

            checkout.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    final String clearedwaitlistId= book.getStatus().substring(book.getStatus().lastIndexOf("#")+1);


                    DocumentReference mRefUser = db.collection("users").document(CurrentUser.UID);
                    mRefUser.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                             @Override
                                                             public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                 if (task.isSuccessful()) {
                                                                     DocumentSnapshot doc = task.getResult();
                                                                     int checkOutBooks=Integer.parseInt(doc.getString(Constants.CheckedOutBooks));
                                                                     String lastCountStr=doc.getString(Constants.LAST_CHECKOUT_DAY_COUNT);


                                                                     if(lastCountStr==null|| lastCountStr.trim().equals("")){
                                                                         Log.w(TAG, "lastCheckoutDayCount is Null");
                                                                         return;
                                                                     }

                                                                     int lastCheckoutDayCount=Integer.parseInt(lastCountStr);


                                                                     String lastCheckDay=doc.getString(Constants.LAST_CHECKED_OUT_DAY);
                                                                     Date lastCheckedOutDay= new Date();
                                                                     try {
                                                                         lastCheckedOutDay=dateToString.parse(lastCheckDay);
                                                                     } catch (ParseException e) {
                                                                         Log.w(TAG, "Error In parsing Date"+e.getMessage());
                                                                     }


                                                                     if(checkOutBooks>8){
                                                                         Toast toast = Toast.makeText(ctx, "9 books already issued. Please drop a book.", Toast.LENGTH_SHORT);
                                                                         toast.show();
                                                                     }else if(DbOperations.checkEqualDay(lastCheckedOutDay,Constants.todaysDate) && lastCheckoutDayCount>2) {
                                                                             Toast toast = Toast.makeText(ctx, "Reached Daily Checkout Limit", Toast.LENGTH_SHORT);
                                                                             toast.show();
                                                                     }else{
                                                                         Transaction transaction = new Transaction();
                                                                         transaction.setBookId(book.getBookId());
                                                                         Date d = Constants.todaysDate ;
                                                                         d= DbOperations.addDays(d,30);
                                                                         transaction.setDueDate(dateToString.format(d));
                                                                         transaction.setFine(0);
                                                                         transaction.setIssueDate(dateToString.format(Constants.todaysDate));
                                                                         transaction.setRenewCount(0);
                                                                         transaction.setUid( CurrentUser.UID);
                                                                         book.setStatus("");
                                                                         transaction.setBook(book);
                                                                         DbOperations.addTransaction(transaction);

                                                                         HashMap<String, Object> hm = new HashMap<String,Object>();
                                                                         hm.put(Constants.CheckedOutBooks,String.valueOf(checkOutBooks+1));
                                                                         if(DbOperations.checkEqualDay(lastCheckedOutDay,Constants.todaysDate)){
                                                                             hm.put(Constants.LAST_CHECKOUT_DAY_COUNT,String.valueOf(lastCheckoutDayCount+1));
                                                                         }else{
                                                                             hm.put(Constants.LAST_CHECKOUT_DAY_COUNT,String.valueOf(1));
                                                                             hm.put(Constants.LAST_CHECKED_OUT_DAY,dateToString.format(Constants.todaysDate));
                                                                         }



                                                                         DocumentReference currentUserDocument = FirebaseFirestore.getInstance().document(Constants.USER_COLLECTION+ "/" + CurrentUser.UID);
                                                                         currentUserDocument.
                                                                                 update(hm)
                                                                                 .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                     @Override
                                                                                     public void onSuccess(Void aVoid) {
                                                                                         Log.v("User No Of Books Update", "Successful");
//
//                                                                                         Intent intent = new Intent(PatronMywaitlistAdapter.this, ViewBooksActivity.class);
//                                                                                         this.startActivity(intent);
                                                                                     }
                                                                                 }).addOnFailureListener(new OnFailureListener() {
                                                                             @Override
                                                                             public void onFailure(@NonNull Exception e) {
                                                                                 Log.w(TAG, "Error writing document No Of Books", e);
                                                                             }
                                                                         });

                                                                      //Deleting entry from clearedwaitlist
                                                                         DbOperations.deleteClearedwaitList(clearedwaitlistId);
                                                                     }
                                                                 }
                                                             }
                                                         });
                }
            });


//            waitlistClearRemove.setOnClickListener(new View.OnClickListener() {
//                public void onClick(View v) {
//
//
//
//                }
//            });

        }
    }
}
