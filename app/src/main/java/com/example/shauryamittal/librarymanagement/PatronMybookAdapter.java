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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shauryamittal.librarymanagement.model.Book;
import com.example.shauryamittal.librarymanagement.model.CurrentUser;
import com.example.shauryamittal.librarymanagement.model.Transaction;

import java.util.List;

/**
 * Created by Harshit on 12/5/17.
 */

public class PatronMybookAdapter extends RecyclerView.Adapter<PatronMybookAdapter.PatronMybookViewHolder>  {


    private Context ctx;
    private List<Transaction> transactionList;
    private String currentBookId="";
    private int currPosition;
    private Book tempBook;
    public static String checkedBookList="";

    public PatronMybookAdapter(Context ctx, List<Transaction> transactionlist) {
        this.ctx = ctx;
        transactionList = transactionlist;
        checkedBookList="";
    }

    @Override
    public PatronMybookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View view = inflater.inflate(R.layout.patron_mybook_item,null);
        return new PatronMybookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PatronMybookViewHolder holder, int position) {

        holder.bind(transactionList.get(position));

    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    class PatronMybookViewHolder extends RecyclerView.ViewHolder{
        Transaction transaction;
        ImageView coverImage;
        TextView title,author,dueDate, fine;
        CheckBox checkBook;
        Button reissueBook;
        public PatronMybookViewHolder(View itemView) {
            super(itemView);
            coverImage=itemView.findViewById(R.id.patron_mybook_book_img);
            title=itemView.findViewById(R.id.patron_mybook_book_name);
            author=itemView.findViewById(R.id.patron_mybook_author);
            dueDate=itemView.findViewById(R.id.patron_mybook_duedate);
            fine=itemView.findViewById(R.id.patron_mybook_fine);

            checkBook= itemView.findViewById(R.id.patron_mybook_checkBox);
            reissueBook=itemView.findViewById(R.id.patron_mybook_reissue_book);
        }

        public void bind(Transaction transact){

            transaction = transact;
            Book book=transaction.getBook();
            tempBook=book;
            title.setText(book.getTitle());
            author.setText(book.getAuthor());
            dueDate.setText("Due Date is "+transaction.getDueDate());
            fine.setText("Fine on book is $"+transaction.getFine());

            checkBook.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                {
                    if ( isChecked )
                    {
                        if(checkedBookList==null ||checkedBookList.trim().equals("")){
                            checkedBookList=tempBook.getBookId();
                        }else{
                            checkedBookList=checkedBookList+","+tempBook.getBookId();
                        }
                    }else{
                        if(checkedBookList==null){
                            checkedBookList="";
                        }else{
                            checkedBookList=checkedBookList.replace(","+tempBook.getBookId(),"").replace(tempBook.getBookId()+",","").replace(tempBook.getBookId(),"");
                        }
                    }

                    System.out.println("checkedBookList===="+checkedBookList);
                }
            });





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
