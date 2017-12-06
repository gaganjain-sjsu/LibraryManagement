package com.example.shauryamittal.librarymanagement;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.shauryamittal.librarymanagement.model.Book;
import com.example.shauryamittal.librarymanagement.model.DbOperations;
import com.example.shauryamittal.librarymanagement.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class LibrarianBookSearch extends AppCompatActivity {
    Spinner librarionSpinner;
    RecyclerView recyclerView;
    LibrarianSearchAdapter lsAdapter;
    List<Book> books = new ArrayList<>();
    private final String UID_KEY = "uid";
    private final String FULLNAME_KEY = "fullname";
    private final String EMAIL_KEY = "email";
    private final String SJSU_ID_KEY = "sjsuId";
    private final String ROLE_KEY = "role";
    List<String> spinnerList = new ArrayList<String>();
    List<String> spinnerKey = new ArrayList<String>();
    private EditText searchKeywork;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_librarian_book_search);
        librarionSpinner = (Spinner) findViewById(R.id.librarionSpinner);
//        List<String> list = new ArrayList<String>();
        spinnerList.add("Select Librarian");
        spinnerKey.add("0");
        getLibrarians();
//        list.add("Harshit");
//        list.add("Shaurya");
//        list.add("Gagan");
//        list.add("Anshul");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, spinnerList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        librarionSpinner.setAdapter(dataAdapter);



    }

    public void searchResult(View view){
        searchKeywork=(EditText)findViewById(R.id.librarionSearchtxt);
        String searchKey=searchKeywork.getText().toString();
        Spinner dropDown=(Spinner)findViewById(R.id.librarionSpinner);
        String librarianId=spinnerKey.get(dropDown.getSelectedItemPosition());
        getSearchedBooks(searchKey,librarianId);


        recyclerView=(RecyclerView)findViewById(R.id.librarianSearchRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

//        Book b1= new Book();
//        b1.setTitle("java");
//        b1.setAuthor("kathy searra");
//        b1.setYearOfPub(2010);
//
//        Book b2= new Book();
//        b2.setTitle("Science");
//        b2.setAuthor(" searra");
//        b2.setYearOfPub(2005);
//        books.add(b1);
//        books.add(b2);

        lsAdapter= new LibrarianSearchAdapter(this,books);
        recyclerView.setAdapter(lsAdapter);
    }

    public void getLibrarians() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .whereEqualTo(ROLE_KEY, "librarian")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                User librarian = new User(document.getString(FULLNAME_KEY),
                                        document.getString(EMAIL_KEY),
                                        document.getString(SJSU_ID_KEY),
                                        document.getString(UID_KEY),
                                        document.getString(ROLE_KEY));

                              //  Log.d(TAG, document.getId() + " => " + document.getData());
                                spinnerList.add(librarian.getName());
                                spinnerKey.add(librarian.getUid());

                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }


    public void getSearchedBooks(String searchKey,String librarianId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if(!searchKey.trim().equals("") && !librarianId.equals("0")){
//            db.collection("books")
//                    .whereEqualTo(ROLE_KEY, "librarian")
//                    .get()
//                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                            if (task.isSuccessful()) {
//                                for (DocumentSnapshot document : task.getResult()) {
//                                    User librarian = new User(document.getString(FULLNAME_KEY),
//                                            document.getString(EMAIL_KEY),
//                                            document.getString(SJSU_ID_KEY),
//                                            document.getString(UID_KEY),
//                                            document.getString(ROLE_KEY));
//
//                                    //  Log.d(TAG, document.getId() + " => " + document.getData());
//                                    spinnerList.add(librarian.getName());
//                                    spinnerKey.add(librarian.getUid());
//
//                                }
//                            } else {
//                                Log.d(TAG, "Error getting documents: ", task.getException());
//                            }
//                        }
//                    });
        }else if(!searchKey.trim().equals("")){

        }else if(!librarianId.equals("0")){

        }else{
            db.collection("books")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot document : task.getResult()) {
                                    Book b1= new Book();
                                    b1.setTitle(document.getString("title"));
                                    b1.setAuthor(document.getString("author"));
                                    //b1.setYearOfPub(Integer.parseInt(document.getDouble("yearOfPub"));
                                    System.out.println("!!!!!!!!!!!!!!!!!!!!!!!==="+document.getDouble("yearOfPub"));
                                    books.add(b1);

                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }

                        }
                    });
        }

    }


}
