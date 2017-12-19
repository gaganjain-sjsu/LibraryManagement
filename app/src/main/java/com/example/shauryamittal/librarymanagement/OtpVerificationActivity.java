package com.example.shauryamittal.librarymanagement;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shauryamittal.librarymanagement.model.Constants;
import com.example.shauryamittal.librarymanagement.model.CurrentUser;
import com.example.shauryamittal.librarymanagement.model.MailUtility;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Random;

public class OtpVerificationActivity extends AppCompatActivity {

    Button verifyOtp;
    Button resendEmail;
    Button cancel;
    TextView otp;
    int generatedOtp;
    ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);
        getSupportActionBar().hide();

        spinner = (ProgressBar) findViewById(R.id.otp_progressBar);

        spinner.setVisibility(View.GONE);

        final Intent intent = getIntent();

        final String email = intent.getStringExtra("email");

        AsyncTaskRunner emailSender = new AsyncTaskRunner();
        emailSender.execute(email);

        otp = (TextView) findViewById(R.id.otp);
        verifyOtp = (Button) findViewById(R.id.otp_submit);
        resendEmail = (Button) findViewById(R.id.resend_otp);
        cancel = (Button) findViewById(R.id.cancelVerfication);

        verifyOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                spinner.setVisibility(View.VISIBLE);
                if(otp.getText().length() == 0) {
                    Toast.makeText(OtpVerificationActivity.this, "Incorrect OTP", Toast.LENGTH_SHORT).show();
                    spinner.setVisibility(View.GONE);
                    return;
                }

                int enteredOtp = 0;
                try {
                    enteredOtp = Integer.parseInt(otp.getText().toString());
                } catch (NumberFormatException e) {
                    Toast.makeText(OtpVerificationActivity.this, "Incorrect OTP", Toast.LENGTH_SHORT).show();
                    spinner.setVisibility(View.GONE);
                    e.printStackTrace();
                }

                if(enteredOtp == generatedOtp){

                    FirebaseUser loggedInUser = FirebaseAuth.getInstance().getCurrentUser();

                    DocumentReference currentUserDocument = FirebaseFirestore.getInstance().document(Constants.USER_COLLECTION+ "/" + loggedInUser.getUid());

                    currentUserDocument.
                            update(Constants.EMAIL_VERIFIED, true)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.v("EMAIL VERIFICATION ", "Successful");
                                    VerificationConfirmation emailSender = new VerificationConfirmation();
                                    emailSender.execute(email);

                                }
                            });

                    currentUserDocument.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                            Intent intent;
                            CurrentUser.setCurrentUser(documentSnapshot);
                            if(CurrentUser.ROLE.equals(Constants.KEY_ROLE_LIBRARIAN)){
                                intent = new Intent(OtpVerificationActivity.this, LibrarianHomepageActivity.class);
                                Toast.makeText(OtpVerificationActivity.this, "Successfully Signed up as a " + Constants.KEY_ROLE_LIBRARIAN, Toast.LENGTH_SHORT).show();
                            }

                            else if(CurrentUser.ROLE.equals(Constants.KEY_ROLE_PATRON)){
                                intent = new Intent(OtpVerificationActivity.this, ViewBooksActivity.class);
                                Toast.makeText(OtpVerificationActivity.this, "Successfully signed up as a " + Constants.KEY_ROLE_PATRON, Toast.LENGTH_SHORT).show();
                            }

                            else{
                                intent = new Intent(OtpVerificationActivity.this, ErrorActivity.class);
                            }
                            spinner.setVisibility(View.GONE);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            finish();
                            startActivity(intent);

                        }
                    });

                }
                else {
                    Toast.makeText(OtpVerificationActivity.this, "Incorrect OTP", Toast.LENGTH_SHORT).show();
                    spinner.setVisibility(View.GONE);
                }
            }
        });

        resendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AsyncTaskRunner emailSender = new AsyncTaskRunner();
                emailSender.execute(email);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth currentUserInstance =  FirebaseAuth.getInstance();

                currentUserInstance.signOut();

                Intent intent = new Intent(OtpVerificationActivity.this, LoginActivity.class);

                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

    }

    private class AsyncTaskRunner extends AsyncTask<String, String, String> {


        @Override
        protected String doInBackground(String... params) {
            try {
                Random rand = new Random();

                generatedOtp = rand.nextInt(8999) + 1000;
                Log.v("GENERATED OTP:", generatedOtp + "");
                Log.v("EMAIL SENT TO:", params[0]);

                String emailMessage = "Your One Time Password (OTP) for the Spartan Bookstore Account is " + generatedOtp;
                MailUtility.sendMail(params[0], emailMessage);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return otp.toString();
        }


        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(OtpVerificationActivity.this, "Verification Email Sent", Toast.LENGTH_SHORT).show();
        }

    }

    private class VerificationConfirmation extends AsyncTask<String, String, String> {


        @Override
        protected String doInBackground(String... params) {
            try {
                Random rand = new Random();

                Log.v("EMAIL SENT TO:", params[0]);

                String emailMessage = "Your email has been successfully verified";
                MailUtility.sendMail(params[0], emailMessage);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return otp.toString();
        }


        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(OtpVerificationActivity.this, "Verification Email Sent", Toast.LENGTH_SHORT).show();
        }

    }


}


