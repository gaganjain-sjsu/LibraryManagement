package com.example.shauryamittal.librarymanagement;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shauryamittal.librarymanagement.model.Book;
import com.example.shauryamittal.librarymanagement.model.Transaction;

import java.util.List;

/**
 * Created by Harshit on 12/5/17.
 */

public class PatronMywaitlistAdapter extends RecyclerView.Adapter<PatronMywaitlistAdapter.PatronMywaitlistViewHolder>  {


    private Context ctx;
    private List<Book> bookList;
    private String currentBookId="";
    private int currPosition;

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
        Button remove;
        public PatronMywaitlistViewHolder(View itemView) {
            super(itemView);
            coverImage=itemView.findViewById(R.id.patron_mywaitlist_book_img);
            title=itemView.findViewById(R.id.patron_mywaitlist_book_name);
            author=itemView.findViewById(R.id.patron_mywaitlist_author);
            yearOfPub=itemView.findViewById(R.id.patron_mywaitlist_yearOfPub);
            remove= itemView.findViewById(R.id.patron_mywaitlist_return_book);
        }

        public void bind(Book book1){

            book = book1;
            title.setText(book.getTitle());
            author.setText(book.getAuthor());
            yearOfPub.setText("Publication Year: "+book.getYearOfPub());

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
