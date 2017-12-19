package com.example.shauryamittal.librarymanagement;

import android.content.Context;
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

import java.util.List;

/**
 * Created by Harshit on 12/5/17.
 */

public class PatronMybookAdapter extends RecyclerView.Adapter<PatronMybookAdapter.PatronMybookViewHolder>  {


    private Context ctx;
    private List<Book> mBookList;
    private String currentBookId="";
    private int currPosition;

    public PatronMybookAdapter(Context ctx, List<Book> bookList) {
        this.ctx = ctx;
        mBookList = bookList;
    }

    @Override
    public PatronMybookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View view = inflater.inflate(R.layout.patron_mybook_item,null);
        return new PatronMybookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PatronMybookViewHolder holder, int position) {

        holder.bind(mBookList.get(position));

    }

    @Override
    public int getItemCount() {
        return mBookList.size();
    }

    class PatronMybookViewHolder extends RecyclerView.ViewHolder{
        Book mBook;
        ImageView coverImage;
        TextView title,author,publisher;
        Button returnBook, reissueBook;
        public PatronMybookViewHolder(View itemView) {
            super(itemView);
            coverImage=itemView.findViewById(R.id.patron_mybook_book_img);
            title=itemView.findViewById(R.id.patron_mybook_book_name);
            author=itemView.findViewById(R.id.patron_mybook_author);
            publisher=itemView.findViewById(R.id.patron_mybook_Publisher);
            returnBook= itemView.findViewById(R.id.patron_mybook_return_book);
            reissueBook=itemView.findViewById(R.id.patron_mybook_reissue_book);
        }

        public void bind(Book book){

            mBook = book;
            title.setText(book.getTitle());
            author.setText(book.getAuthor());
            publisher.setText(book.getPublisher());
            publisher.setText(book.getPublisher());

//            delete.setOnClickListener(new View.OnClickListener() {
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
//                    mBookList.remove(mBook);
//                    notifyDataSetChanged();
//                }
//            });
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
