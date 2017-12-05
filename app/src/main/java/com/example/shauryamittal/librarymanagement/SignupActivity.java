package com.example.shauryamittal.librarymanagement;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;


public class SignupActivity extends AppCompatActivity {

    EditText editText_sjsuId;
    EditText editText_email;
    EditText editText_password;
    Button signup;
    ProgressBar spinner;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getSupportActionBar().hide();
        spinner = (ProgressBar) findViewById(R.id.progressBar2);
        spinner.setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();

        editText_sjsuId = (EditText) findViewById(R.id.sjsu_id);
        editText_email = (EditText) findViewById(R.id.email_signup);
        editText_password = (EditText) findViewById(R.id.signup_password);

        signup = (Button) findViewById(R.id.signup);




        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String sjsuId = editText_sjsuId.getText().toString().trim();
                String email = editText_email.getText().toString().trim();
                String password = editText_password.getText().toString().trim();

                if(sjsuId.isEmpty()){
                    editText_sjsuId.setError("SJSU ID can't be empty");
                    editText_sjsuId.requestFocus();
                    return;
                }

                if(email.isEmpty()){
                    editText_email.setError("Email can't be empty");
                    editText_email.requestFocus();
                    return;
                }

                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    editText_email.setError("Enter a valid email address");
                    editText_email.requestFocus();
                    return;
                }

                if(password.isEmpty() || password.length() < 6){
                    editText_password.setError("Password Must be at least 6 characters");
                    editText_password.requestFocus();
                    return;
                }

                spinner.setVisibility(View.VISIBLE);

                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override

                    public void onComplete(@NonNull Task<AuthResult> task) {


                        if(task.isSuccessful()){
                            Toast.makeText(SignupActivity.this, "Signup Successful", Toast.LENGTH_SHORT).show();
                            spinner.setVisibility(View.GONE);
                        }
                        else {
                            spinner.setVisibility(View.GONE);
                            if(task.getException() instanceof FirebaseAuthUserCollisionException){
                                Toast.makeText(SignupActivity.this, "Email Already Registered", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                });

            }
        });

    }
}
