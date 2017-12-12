package com.example.shauryamittal.librarymanagement;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
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

public class ShoppingCartAdapter extends RecyclerView.Adapter<ShoppingCartAdapter.ShoppingCartViewHolder>  {


    private Context ctx;
    private List<Book> mBookList;
    private String currentBookId="";
    private int currPosition;

    public ShoppingCartAdapter(Context ctx, List<Book> bookList) {
        this.ctx = ctx;
        mBookList = bookList;
    }

    @Override
    public ShoppingCartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View view = inflater.inflate(R.layout.cart_item,null);
        return new ShoppingCartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ShoppingCartViewHolder holder, int position) {

        holder.bind(mBookList.get(position));

    }

    @Override
    public int getItemCount() {
        return mBookList.size();
    }

    class ShoppingCartViewHolder extends RecyclerView.ViewHolder{
        Book mBook;
        ImageView coverImage;
        TextView title,author;
        TextView delete;
        public ShoppingCartViewHolder(View itemView) {
            super(itemView);
            coverImage=itemView.findViewById(R.id.cart_book_img);
            title=itemView.findViewById(R.id.cart_book_name);
            author=itemView.findViewById(R.id.cart_author);
            delete= itemView.findViewById(R.id.remove_cart_item);

        }

        public void bind(Book book){

            mBook = book;
            title.setText(book.getTitle());
            author.setText(book.getAuthor());

            delete.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    mBookList.remove(mBook);
                    SharedPreferences SP;
                    SP = PreferenceManager.getDefaultSharedPreferences(ctx);
                    String currentCartItems = SP.getString(CurrentUser.UID, null);
                    String currrentItem=mBook.getBookId();
                    currentCartItems=currentCartItems.replaceAll(currrentItem,"");
                    currentCartItems=currentCartItems.replaceAll(",,",",");
                    //String newCartItems = currentCartItems.replaceAll(bookToRemove+",","").replaceAll(""+","+bookToRemove,"").replaceAll(bookToRemove, "");


                    SharedPreferences.Editor edit = SP.edit();
                    edit.putString (CurrentUser.UID, currentCartItems);
                    edit.commit();




                    System.out.println("Inside delete button called.  Book Id="+mBook.getTitle());
                    notifyDataSetChanged();
                }
            });

        }
    }
}
