package com.example.shauryamittal.librarymanagement;

import android.graphics.Bitmap;

import com.example.shauryamittal.librarymanagement.model.Transaction;

/**
 * Created by shauryamittal on 12/20/17.
 */

public class TransactionItem {

    Transaction transaction;
    Bitmap bookBitmap;

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public Bitmap getBookBitmap() {
        return bookBitmap;
    }

    public void setBookBitmap(Bitmap bookBitmap) {
        this.bookBitmap = bookBitmap;
    }
}
