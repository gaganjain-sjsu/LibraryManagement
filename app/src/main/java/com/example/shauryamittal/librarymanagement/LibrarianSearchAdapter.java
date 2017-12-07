package com.example.shauryamittal.librarymanagement;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shauryamittal.librarymanagement.model.Book;

import java.util.List;

/**
 * Created by Harshit on 12/5/17.
 */

public class LibrarianSearchAdapter extends RecyclerView.Adapter<LibrarianSearchAdapter.LibrarianSearchViewHolder>  {

    private Context ctx;
    private List<Book> bookList;

    public LibrarianSearchAdapter(Context ctx, List<Book> bookList) {
        this.ctx = ctx;
        this.bookList = bookList;
    }

    @Override
    public LibrarianSearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View view = inflater.inflate(R.layout.librarian_search_layout,null);
        return new LibrarianSearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LibrarianSearchViewHolder holder, int position) {
            Book book=bookList.get(position);
        holder.title.setText(book.getTitle());
        holder.author.setText(book.getAuthor());
        holder.yearOfPub.setText(String.valueOf(book.getYearOfPub()));
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    class LibrarianSearchViewHolder extends RecyclerView.ViewHolder{
        ImageView coverImage;
        TextView title,author,yearOfPub;
        public LibrarianSearchViewHolder(View itemView) {
            super(itemView);
            coverImage=itemView.findViewById(R.id.bookCoverImage);
            title=itemView.findViewById(R.id.textViewTitle);
            author=itemView.findViewById(R.id.textViewAuthor);
            yearOfPub=itemView.findViewById(R.id.textViewYearOfPub);
        }
    }
}
