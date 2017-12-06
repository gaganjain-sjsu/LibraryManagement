package com.example.shauryamittal.librarymanagement.model;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gaganjain on 12/5/17.
 */

public class BookFactory {

    private static BookFactory sBookFactory;

    private List<Book> mBookList;

    public static BookFactory get(Context context) {
        if (sBookFactory == null) {
            sBookFactory = new BookFactory(context);
        }

        return sBookFactory;
    }

    private BookFactory(Context context) {
        mBookList = new ArrayList<>();
    }

    public void addBook(Book book) {
//        for(Book book: mBookList){
//            if(book.().equals(c.getCityName())){
//                return;
//            }
//        }
        mBookList.add(book);
    }

    public List<Book> getBookList() {
        return mBookList;
    }

    public Book getBook(String title) {
        for(Book book : mBookList){
            if(book.getTitle().equals(title)){
                return book;
            }
        }
        return null;
    }
}
