package com.example.shauryamittal.librarymanagement;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shauryamittal.librarymanagement.model.Book;
import com.example.shauryamittal.librarymanagement.model.DbOperations;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Harshit on 12/5/17.
 */

public class LibrarianSearchAdapter extends RecyclerView.Adapter<LibrarianSearchAdapter.LibrarianSearchViewHolder>  {


    private Context ctx;
    private List<Book> mBookList;
    private String currentBookId="";
    private int currPosition;

    public LibrarianSearchAdapter(Context ctx, List<Book> bookList) {
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

        public void bind(Book book){

            mBook = book;
            title.setText(book.getTitle());
            author.setText(book.getAuthor());
            yearOfPub.setText(String.valueOf(book.getYearOfPub()));

            delete.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    DbOperations.deleteBook(mBook.getBookId());
                    mBookList.remove(mBook);
                    System.out.println("Inside delete button called.  Book Id="+mBook.getTitle());
                    notifyDataSetChanged();
                }
            });

            update.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    Intent intent = new Intent(ctx, AddUpdateBook.class);
//                    EditText editText = (EditText) findViewById(R.id.editText);
//                    String message = editText.getText().toString();
//                    intent.putExtra(EXTRA_MESSAGE, message);
                    intent.putExtra("bookObj",mBook);
                    ctx.startActivity(intent);

                }
            });
        }
    }
}
