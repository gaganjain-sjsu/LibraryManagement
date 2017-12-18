package com.example.shauryamittal.librarymanagement;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shauryamittal.librarymanagement.model.Book;
import com.example.shauryamittal.librarymanagement.model.CurrentUser;
import com.example.shauryamittal.librarymanagement.model.DbOperations;

import java.util.List;

/**
 * Created by Harshit on 12/5/17.
 */

public class CheckoutStatusAdapter extends RecyclerView.Adapter<CheckoutStatusAdapter.CheckoutStatusViewHolder>  {


    private Context ctx;
    private List<Book> mBookList;
    private String currentBookId="";
    private int currPosition;

    public CheckoutStatusAdapter(Context ctx, List<Book> bookList) {
        this.ctx = ctx;
        mBookList = bookList;
    }

    @Override
    public CheckoutStatusViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View view = inflater.inflate(R.layout.checkout_status_item,null);
        return new CheckoutStatusViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CheckoutStatusViewHolder holder, int position) {

        holder.bind(mBookList.get(position));

    }

    @Override
    public int getItemCount() {
        return mBookList.size();
    }

    class CheckoutStatusViewHolder extends RecyclerView.ViewHolder{
        Book mBook;
        ImageView coverImage;
        TextView title,author,status;
        Button delete, addToWaitList;
        public CheckoutStatusViewHolder(View itemView) {
            super(itemView);
            coverImage=itemView.findViewById(R.id.checkout_book_img);
            title=itemView.findViewById(R.id.checkout_book_name);
            author=itemView.findViewById(R.id.checkout_author);
            status=itemView.findViewById(R.id.checkout_author);
            delete= itemView.findViewById(R.id.checkout_cart_delete);
            addToWaitList=itemView.findViewById(R.id.checkout_add_waitlist);

        }

        public void bind(Book book){

            mBook = book;
            title.setText(book.getTitle());
            author.setText(book.getAuthor());
            if(book.getStatus()!=null && book.getStatus().toLowerCase().contains("available")){
                status.setText(book.getStatus());
            }else{
              status.setText(book.getStatus());
              delete.setVisibility(View.GONE);
                addToWaitList.setVisibility(View.GONE);
            }

            delete.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    SharedPreferences SP;
                    SP = PreferenceManager.getDefaultSharedPreferences(ctx);
                    String currentCartItems = SP.getString(CurrentUser.UID, null);
                    String bookToRemove=mBook.getBookId();
                    if(bookToRemove!=null && currentCartItems!=null){
                        String newCartItems = currentCartItems.replaceAll(bookToRemove+",","").replaceAll(""+","+bookToRemove,"").replaceAll(bookToRemove, "");
                        SharedPreferences.Editor edit = SP.edit();
                        edit.putString (CurrentUser.UID, newCartItems);
                        Log.v("SHARED PREFERENCE:", newCartItems);
                        edit.commit();
                    }
                    mBookList.remove(mBook);
                    notifyDataSetChanged();
                }
            });

            addToWaitList.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    SharedPreferences SP;
                    SP = PreferenceManager.getDefaultSharedPreferences(ctx);
                    String currentCartItems = SP.getString(CurrentUser.UID, null);
                    String bookToRemove=mBook.getBookId();
                    if(bookToRemove!=null && currentCartItems!=null){
                        String newCartItems = currentCartItems.replaceAll(bookToRemove+",","").replaceAll(""+","+bookToRemove,"").replaceAll(bookToRemove, "");
                        SharedPreferences.Editor edit = SP.edit();
                        edit.putString (CurrentUser.UID, newCartItems);
                        Log.v("SHARED PREFERENCE:", newCartItems);
                        edit.commit();
                    }

                    notifyDataSetChanged();

                }
            });
        }
    }
}
