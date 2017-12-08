package com.example.shauryamittal.librarymanagement;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shauryamittal.librarymanagement.model.DbOperations;
import com.example.shauryamittal.librarymanagement.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class EmailVerificationActivity extends AppCompatActivity {

    private int verificationCode;
    private Button verifyAccount;
    private EditText codeInput;
    private FirebaseAuth mAuth;
    private String email, password, sjsuId, fullname;
    String toastMessage = "";
    boolean userCreated;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);
        verifyAccount = (Button)findViewById(R.id.verify_account);
        codeInput = (EditText) findViewById(R.id.verification_code);
        Intent intent = getIntent();
        verificationCode = intent.getIntExtra("code", 0);
        email = intent.getStringExtra("email");
        password = intent.getStringExtra("password");
        sjsuId = intent.getStringExtra("sjsuId");
        fullname = intent.getStringExtra("fullname");
        userCreated = false;
        mAuth = FirebaseAuth.getInstance();

        verifyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int enteredCode =Integer.parseInt(codeInput.getText().toString());
                if( enteredCode == verificationCode){

                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override

                        public void onComplete(@NonNull Task<AuthResult> task) {

                            //spinner.setVisibility(View.GONE);


                            if(task.isSuccessful()){

                                String uid = task.getResult().getUser().getUid();
                                String email = task.getResult().getUser().getEmail();
                                String role;

                                if(email.split("@")[1].equals("sjsu.edu")){
                                    role = "Librarian";
                                }
                                else {
                                    role = "Patron";
                                }

                                DbOperations.createUser(new User(fullname, email, sjsuId, uid, role));
                                userCreated = true;
                                toastMessage = "Signup Successful as a " + role;
                                //Toast.makeText(, "Signup Successful as a " + role, Toast.LENGTH_SHORT).show();
                            }
                            else {
                                if(task.getException() instanceof FirebaseAuthUserCollisionException){
                                    toastMessage = "Email Already Registered";
                                    //Toast.makeText(SignupActivity.this, "Email Already Registered", Toast.LENGTH_SHORT).show();
                                }

                                else{
                                    //Toast.makeText(SignupActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    toastMessage = task.getException().getMessage();
                                }
                            }
                            navigateNextActivity();

                        }
                    });
                }
                else{
                    toastMessage = "Incorrect Verification Code";
                }
            }
        });
        Toast.makeText(EmailVerificationActivity.this, toastMessage, Toast.LENGTH_SHORT).show();
    }

    public void navigateNextActivity(){
        //T
        if(userCreated){
            startActivity(new Intent(this, ViewBooksActivity.class));
        }

    }


}
