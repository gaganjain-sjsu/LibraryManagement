package com.example.shauryamittal.librarymanagement;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.example.shauryamittal.librarymanagement.model.Book;
import com.example.shauryamittal.librarymanagement.model.Constants;
import com.example.shauryamittal.librarymanagement.model.CurrentUser;
import com.example.shauryamittal.librarymanagement.model.DbOperations;
import com.example.shauryamittal.librarymanagement.model.MailUtility;
import com.example.shauryamittal.librarymanagement.model.Timestamp;
import com.example.shauryamittal.librarymanagement.model.User;
import com.example.shauryamittal.librarymanagement.model.Transaction;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.content.ContentValues.TAG;

public class SetDateActivity extends AppCompatActivity implements View.OnClickListener{

    Button btnDatePicker, btnTimePicker;
    EditText txtDate, txtTime;
    private int mYear, mMonth, mDay, mHour, mMinute;
    String date, time, uid;
    static FirebaseFirestore db = FirebaseFirestore.getInstance();
    SimpleDateFormat dateToString = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    StringBuilder dueDateAlartMessage= new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_date);

        btnDatePicker=(Button)findViewById(R.id.btn_date);
        btnTimePicker=(Button)findViewById(R.id.btn_time);
        txtDate=(EditText)findViewById(R.id.in_date);
        txtTime=(EditText)findViewById(R.id.in_time);

        btnDatePicker.setOnClickListener(this);
        btnTimePicker.setOnClickListener(this);
        uid = CurrentUser.UID;

    }

    @Override
    public void onClick(View v) {



        if (v == btnDatePicker) {

            // Get Current Date
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            date = (monthOfYear + 1) + "/" + (dayOfMonth) + "/" + year+ " ";
                            txtDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }
        if (v == btnTimePicker) {

            // Get Current Time
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {
                            date += hourOfDay + ":" + minute + ":00";
                            txtTime.setText(hourOfDay + ":" + minute);

                            Timestamp timestamp = new Timestamp(date, uid);
                            DbOperations.addTimestamp(timestamp);
                            try {
                                Constants.todaysDate = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").parse(date);
                                checkDueUsers();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();

        }
    }
    public void checkDueUsers(){
        db.collection("transaction")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Transaction transaction = document.toObject(Transaction.class);
                                Date currentDate=Constants.todaysDate;
                                try {
                                    Date dueDate=dateToString.parse(transaction.getDueDate());
                                   long diff = Math.round((dueDate.getTime() - currentDate.getTime()) / (double) 86400000);
                                   int dif=(int)diff;
                                   if(dif<6){
                                       dueDateAlartMessage=new StringBuilder(" Book: "+transaction.getBook().getTitle()+" is due in " + dif + "days . Please return/renew to avoid fine."+"\n\n\n");
                                       DocumentReference mRefUser = db.collection("users").document(transaction.getUid());
                                       mRefUser.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                           @Override
                                           public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                               if (task.isSuccessful()) {
                                                   DocumentSnapshot doc = task.getResult();
                                                   String sendToMailId=doc.getString("email");
                                                   SetDateActivity.AsyncTaskRunner emailSender = new SetDateActivity.AsyncTaskRunner();
                                                   emailSender.execute(sendToMailId, dueDateAlartMessage.toString());
                                                   System.out.println("Mail Sent...");
                                               }
                                           }
                                       });




                                   }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.librarian_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.homePageRedirect:
                startActivity(new Intent(SetDateActivity.this, LibrarianHomepageActivity.class));
                return true;
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                finish();
                CurrentUser.destroyCurrentUser();
                startActivity(new Intent(SetDateActivity.this, LoginActivity.class));
        }

        return true;
    }

    private class AsyncTaskRunner extends AsyncTask<String, String, String> {


        @Override
        protected String doInBackground(String... params) {
            try {
                Log.v("EMAIL SENT TO:", params[0]);
                MailUtility.sendMail(params[0], params[1]);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return "Successful";
        }
    }
}
