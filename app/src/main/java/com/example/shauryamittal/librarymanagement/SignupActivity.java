package com.example.shauryamittal.librarymanagement;

import android.app.Activity;
import android.content.Intent;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.shauryamittal.librarymanagement.model.DbOperations;
import com.example.shauryamittal.librarymanagement.model.MailUtility;
import com.example.shauryamittal.librarymanagement.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Random;


public class SignupActivity extends AppCompatActivity {

    EditText editText_fullname;
    EditText editText_sjsuId;
    EditText editText_email;
    EditText editText_password;
    Button signup;
    Button toLogin;
    ProgressBar spinner;
    String email;
    String password;

    private FirebaseAuth mAuth;
    FirebaseFirestore db;

    private final String LIBRARIAN = "librarian";
    private final String PATRON = "patron";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getSupportActionBar().hide();
        spinner = (ProgressBar) findViewById(R.id.progressBar2);
        spinner.setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();

        editText_fullname = (EditText) findViewById(R.id.fullname);
        editText_sjsuId = (EditText) findViewById(R.id.sjsu_id);
        editText_email = (EditText) findViewById(R.id.email_signup);
        editText_password = (EditText) findViewById(R.id.signup_password);

        signup = (Button) findViewById(R.id.signup);
        toLogin = (Button) findViewById(R.id.go_to_login);

        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String sjsuId = editText_sjsuId.getText().toString().trim();
                email = editText_email.getText().toString().trim();
                password = editText_password.getText().toString().trim();
                final String fullname = editText_fullname.getText().toString().trim();

                if(fullname.isEmpty()){
                    editText_sjsuId.setError("Enter a name");
                    editText_sjsuId.requestFocus();
                    return;
                }

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

                db = FirebaseFirestore.getInstance();

                db.collection("users")
                        .whereEqualTo("sjsuId", sjsuId)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if(task.getResult().size() == 0){

                                        db.collection("users")
                                                .whereEqualTo("email", email)
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            if(task.getResult().size() == 0){

                                                                Random rn = new Random();
                                                                int n = 99999 - 22222 + 1;
                                                                int i = rn.nextInt() % n;
                                                                int code =  22222 + i;
                                                                //MailUtility.sendMail(email, String.valueOf(code));
                                                                MailUtility.sendMail(email, String.valueOf(code));
                                                                spinner.setVisibility(View.GONE);

                                                                Intent intent = new Intent(SignupActivity.this, EmailVerificationActivity.class);
                                                                intent.putExtra("sjsuId", sjsuId);
                                                                intent.putExtra("fullName", fullname);
                                                                intent.putExtra("email", email);
                                                                intent.putExtra("password", password);
                                                                intent.putExtra("code", code);
                                                                startActivity(intent);

                                                                finish();

                                                            }
                                                            else {
                                                                Toast.makeText(SignupActivity.this, "Email already registered!", Toast.LENGTH_SHORT).show();
                                                                spinner.setVisibility(View.GONE);
                                                            }

                                                        } else {
                                                            Log.d("Docs", "Error getting documents: ", task.getException());
                                                        }
                                                    }
                                                });

                                    }
                                    else {
                                        Toast.makeText(SignupActivity.this, "SJSU ID already registered!", Toast.LENGTH_SHORT).show();
                                        spinner.setVisibility(View.GONE);
                                    }

                                } else {
                                    Log.d("Docs", "Error getting documents: ", task.getException());
                                }
                            }
                        });



            }
        });

        toLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                finish();
            }
        });

    }

    public void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
