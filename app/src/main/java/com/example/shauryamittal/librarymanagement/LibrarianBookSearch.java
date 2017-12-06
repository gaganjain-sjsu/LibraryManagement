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
    String searchKey="";
    private EditText searchKeywork;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_librarian_book_search);
        librarionSpinner = (Spinner) findViewById(R.id.librarionSpinner);
        spinnerList.add("Select Librarian");
        spinnerKey.add("0");
        getLibrarians();
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, spinnerList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        librarionSpinner.setAdapter(dataAdapter);



    }

    public void searchResult(View view){
        searchKeywork=(EditText)findViewById(R.id.librarionSearchtxt);
        searchKey=searchKeywork.getText().toString();
        Spinner dropDown=(Spinner)findViewById(R.id.librarionSpinner);
        String librarianId=spinnerKey.get(dropDown.getSelectedItemPosition());
        books.clear();
        recyclerView=(RecyclerView)findViewById(R.id.librarianSearchRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        lsAdapter= new LibrarianSearchAdapter(this,books);
        recyclerView.setAdapter(lsAdapter);
        getSearchedBooks(librarianId);

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
                                spinnerList.add(librarian.getName());
                                spinnerKey.add(librarian.getUid());

                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }


    public void getSearchedBooks(String librarianId) {
        System.out.println("librarianId==="+librarianId);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if(!librarianId.equals("0")){
            db.collection("books").whereEqualTo("librarianId", librarianId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {

                                for (DocumentSnapshot document : task.getResult()) {




                                    Book b1= new Book();
                                    if(searchKey!=null && !searchKey.equals("")){
                                        String keyWord=document.getString("keywords");
                                        if(keyWord!=null && searchKey!=null && keyWord.toLowerCase().contains(searchKey.toLowerCase())){
                                            b1.setTitle(document.getString("title"));
                                            b1.setAuthor(document.getString("author"));
                                            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!==="+document.get("yearOfPub"));
                                            books.add(b1);
                                        }
                                    }else{
                                        b1.setTitle(document.getString("title"));
                                        b1.setAuthor(document.getString("author"));
                                        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!==="+document.get("yearOfPub"));
                                        books.add(b1);
                                    }

                                    createSearchList();





                                }


                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }

                        }
                    });

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
                                    System.out.println("!!!!!!!!!!!!!!!!!!!!!!!==="+document.get("yearOfPub"));
                                    books.add(b1);
                                    createSearchList();
                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }

                        }
                    });
        }

    }
public void createSearchList(){
    recyclerView=(RecyclerView)findViewById(R.id.librarianSearchRecyclerView);
    recyclerView.setHasFixedSize(true);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    lsAdapter= new LibrarianSearchAdapter(this,books);
    recyclerView.setAdapter(lsAdapter);
    }

}
