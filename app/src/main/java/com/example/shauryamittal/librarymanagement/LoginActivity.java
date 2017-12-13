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

import com.example.shauryamittal.librarymanagement.model.Constants;
import com.example.shauryamittal.librarymanagement.model.CurrentUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    EditText editText_email;
    EditText editText_password;
    Button login;
    Button toSignup;
    ProgressBar spinner;

    private FirebaseAuth mAuth;

    static final String USER_COLLECTION = "users";

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

                final String email = editText_email.getText().toString().trim();
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



                        if(task.isSuccessful()){

                            final FirebaseUser loggedInUser = FirebaseAuth.getInstance().getCurrentUser();

                            final DocumentReference currentUserDocument = FirebaseFirestore.getInstance().document(USER_COLLECTION + "/" + loggedInUser.getUid());

                            currentUserDocument.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {

                                    Intent intent;
                                    spinner.setVisibility(View.GONE);


                                    if(!documentSnapshot.contains(Constants.EMAIL_VERIFIED) || !documentSnapshot.getBoolean(Constants.EMAIL_VERIFIED)){
                                        intent = new Intent(LoginActivity.this, OtpVerificationActivity.class);
                                        intent.putExtra("email", email);
                                        Toast.makeText(LoginActivity.this, "Your email is not verified", Toast.LENGTH_SHORT).show();
                                        startActivity(intent);

                                        finish();
                                        return;
                                    }

                                    CurrentUser.setCurrentUser(documentSnapshot);

                                    if(CurrentUser.ROLE.equals(Constants.KEY_ROLE_LIBRARIAN)){
                                        intent = new Intent(LoginActivity.this, LibrarianHomepageActivity.class);
                                    }

                                    else if(CurrentUser.ROLE.equals(Constants.KEY_ROLE_PATRON)){
                                        intent = new Intent(LoginActivity.this, ViewBooksActivity.class);
                                    }

                                    else{
                                        intent = new Intent(LoginActivity.this, ErrorActivity.class);
                                    }

                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    finish();
                                    startActivity(intent);

                                }
                            });

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

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){

            FirebaseUser loggedInUser = FirebaseAuth.getInstance().getCurrentUser();

            DocumentReference currentUserDocument = FirebaseFirestore.getInstance().document(USER_COLLECTION + "/" + loggedInUser.getUid());

            currentUserDocument.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {

                    CurrentUser.setCurrentUser(documentSnapshot);

                    Intent intent;

                    if(CurrentUser.ROLE.equals(Constants.KEY_ROLE_LIBRARIAN)){
                        intent = new Intent(LoginActivity.this, LibrarianHomepageActivity.class);
                    }

                    else if(CurrentUser.ROLE.equals(Constants.KEY_ROLE_PATRON)){
                        intent = new Intent(LoginActivity.this, ViewBooksActivity.class);
                    }

                    else{
                        intent = new Intent(LoginActivity.this, ErrorActivity.class);
                    }
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    finish();
                    startActivity(intent);

                }
            });


        }
    }
}
