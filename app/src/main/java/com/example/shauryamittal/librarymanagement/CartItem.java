package com.example.shauryamittal.librarymanagement;

/**
 * Created by shauryamittal on 12/6/17.
 */

public class CartItem {

    private String bookId;
    private String bookName;
    private String authorName;

    public CartItem(String bookId, String bookName, String authorName){
        this.bookId = bookId;
        this.bookName = bookName;
        this.authorName = authorName;
    }

    public String getBookName() {
        return bookName;
    }

    public String getAuthorName() {
        return authorName;
    }
    public String getBookId() {
        return bookId;
    }

}
