package com.example.shauryamittal.librarymanagement;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class LoginActivity extends AppCompatActivity {

    EditText editText_email;
    EditText editText_password;
    Button login;
    Button toSignup;
    ProgressBar spinner;


    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();
        spinner = (ProgressBar) findViewById(R.id.login_progressBar);
        spinner.setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();

        editText_email = (EditText) findViewById(R.id.email_login);
        editText_password = (EditText) findViewById(R.id.login_password);

        login = (Button) findViewById(R.id.login);
        toSignup = (Button)findViewById(R.id.go_to_signup);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = editText_email.getText().toString().trim();
                String password = editText_password.getText().toString().trim();

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

                if(password.isEmpty()){
                    editText_password.setError("Password cannot be empty");
                    editText_password.requestFocus();
                    return;
                }

                spinner.setVisibility(View.VISIBLE);

                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override

                    public void onComplete(@NonNull Task<AuthResult> task) {

                        spinner.setVisibility(View.GONE);

                        if(task.isSuccessful()){
                            Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, AddUpdateBook.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }
                        else {

                            if(task.getException() instanceof FirebaseAuthInvalidUserException
                            || task.getException() instanceof FirebaseAuthInvalidUserException){
                                Log.w("signInWithEmail:failure", task.getException());
                                Toast.makeText(LoginActivity.this, "Invalid Username or Password",
                                        Toast.LENGTH_SHORT).show();
                            }

                            else{
                                Log.w("signInWithEmail:failure", task.getException());
                                Toast.makeText(LoginActivity.this, "Authentication Failed",
                                        Toast.LENGTH_SHORT).show();
                            }


                        }


                    }
                });

            }
        });

        toSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });

    }
}
