package com.example.shauryamittal.librarymanagement;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shauryamittal.librarymanagement.model.Book;
import com.example.shauryamittal.librarymanagement.model.DbOperations;

import java.util.List;

/**
 * Created by Harshit on 12/5/17.
 */

public class PatronSearchAdapter extends RecyclerView.Adapter<PatronSearchAdapter.PatronSearchViewHolder>  {


    private Context ctx;
    private List<Book> mBookList;
    private String currentBookId="";
    private int currPosition;

    public PatronSearchAdapter(Context ctx, List<Book> bookList) {
        this.ctx = ctx;
        mBookList = bookList;
    }

    @Override
    public PatronSearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View view = inflater.inflate(R.layout.patron_search_layout,null);
        return new PatronSearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PatronSearchViewHolder holder, int position) {

        holder.bind(mBookList.get(position));

    }

    @Override
    public int getItemCount() {
        return mBookList.size();
    }

    class PatronSearchViewHolder extends RecyclerView.ViewHolder{
        Book mBook;
        ImageView coverImage;
        TextView title,author,yearOfPub;
        //Button delete, update;
        public PatronSearchViewHolder(View itemView) {
            super(itemView);

            coverImage=itemView.findViewById(R.id.PatronBookCoverImage);
            title=itemView.findViewById(R.id.PatronTitle);
            author=itemView.findViewById(R.id.PatronAuthor);
            yearOfPub=itemView.findViewById(R.id.PatronYearOfPub);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ctx, SearchDetailActivity.class);
                    System.out.println("Inside bind-----------------"+mBook.getBookId());
                    intent.putExtra("bookId",mBook.getBookId());
                    ctx.startActivity(intent);
                }
            });

        }

        public void bind(Book book){

            mBook = book;
            title.setText(book.getTitle());
            author.setText(book.getAuthor());
            yearOfPub.setText(String.valueOf(book.getYearOfPub()));




        }
    }
}
