package com.example.shauryamittal.librarymanagement;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class EmailVerificationActivity extends AppCompatActivity {

    private String verificationCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);
        Intent intent = getIntent();
        verificationCode = intent.getStringExtra("verificationcode");


    }
}
