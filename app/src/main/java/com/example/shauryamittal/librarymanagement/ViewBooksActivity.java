package com.example.shauryamittal.librarymanagement;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shauryamittal.librarymanagement.model.Book;
import com.example.shauryamittal.librarymanagement.model.BookFactory;
import com.example.shauryamittal.librarymanagement.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.example.shauryamittal.librarymanagement.model.CurrentUser;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import static android.content.ContentValues.TAG;

public class ViewBooksActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private TextView returns;
    private ViewPager mViewPager;
    List<Fragment> mFragmentList=new ArrayList<Fragment>();
    List<String> mFragmentPageTitle=new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_books);

        PatronMybookFragment f1 = new PatronMybookFragment();
        mFragmentList.add(f1);
        PatronMyWaitlistFragment f2= new PatronMyWaitlistFragment();
        mFragmentList.add(f2);
        mFragmentPageTitle.add("My Books");
        mFragmentPageTitle.add("My Waitlist Books");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);


        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);



//        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
//        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_books, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.search:
                Intent intent = new Intent(this, PatronBookSearch.class);
                startActivity(intent);
                return true;


            case  R.id.view_cart:

                intent = new Intent(this, ShoppingCartActivity.class);
                startActivity(intent);
                return true;

            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                finish();
                CurrentUser.destroyCurrentUser();
                intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
            }
        }

        public static class PlaceholderFragment extends Fragment {


            private static final String TAG = "PlaceholderFragment";
            private BookFactory factory;
            //private Book book;
            private RecyclerView mBookRecyclerView;
            private BookAdapter mAdapter;
            private DatabaseReference mDatabase;
            private static final String ARG_SECTION_NUMBER = "section_number";
            private List<Book> mBookList;
            private List<Book> returnsBookList;
            private CheckBox mCheckBox;
            private TextView mReturns;
            private Boolean returnsClicked;

            public PlaceholderFragment() {
            }

            /**
             * Returns a new instance of this fragment for the given section
             * number.
             */
            public static PlaceholderFragment newInstance(int sectionNumber) {
                PlaceholderFragment fragment = new PlaceholderFragment();
                Bundle args = new Bundle();
                args.putInt(ARG_SECTION_NUMBER, sectionNumber);
                fragment.setArguments(args);
                return fragment;
            }


            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                     Bundle savedInstanceState) {
                mBookList = new ArrayList<>();
                returnsBookList = new ArrayList<>();
                View rootView = inflater.inflate(R.layout.fragment_view_books, container, false);
                mBookRecyclerView = rootView
                        .findViewById(R.id.book_recycler_view);
                mReturns = rootView.findViewById(R.id.return_book);
                mBookRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                returnsClicked = false;
                Log.d(TAG, "inside onCreateView");

                mReturns.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        returnsClicked = true;
                        for (Book returns : returnsBookList) {
                            int position = mAdapter.mBookList.indexOf(returns);
                            CheckBox chBox = mBookRecyclerView.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.return_user_input);
                            chBox.setChecked(false);
                            mAdapter.mBookList.remove(returns);
                        }
                        returnsBookList.clear();
                        mAdapter.notifyDataSetChanged();

                        returnsClicked = false;
                        // updateCheckBox();
                    }
                });

                return rootView;
            }

            public void fetchUser(){

                FirebaseFirestore database = FirebaseFirestore.getInstance();
                DocumentReference docRef = database.collection("users").document(CurrentUser.UID);


                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null) {
                                User user = document.toObject(User.class);
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });
            }

            public void updateCheckBox() {

                for (Book books : mAdapter.mBookList) {
                    int position = mAdapter.mBookList.indexOf(books);
                    CheckBox chBox = mBookRecyclerView.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.return_user_input);
                    chBox.setChecked(false);
                }
            }


            @Override
            public void onResume() {
                super.onResume();
                Log.d(TAG, "inside onResume()");
                updateUI();
            }

            public void pullBooks(String bookId){

                if(bookId != null){
                    FirebaseFirestore database = FirebaseFirestore.getInstance();
                    database.collection("books")
                            .whereEqualTo("bookId", bookId)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        mBookList.clear();
                                        for (DocumentSnapshot document : task.getResult()) {
                                            Book book = document.toObject(Book.class);
                                            book.setBookId(document.getId());
                                            mBookList.add(book);
                                        }
                                        mAdapter.notifyDataSetChanged();
                                    } else {
                                        //TODO
                                    }
                                }
                            });
                }

            }

            private void updateUI() {

                Log.d(TAG, "inside updateUI()");
                //factory = BookFactory.get(getActivity());
                //List<Book> bookList = factory.getBookList();

                if (mAdapter == null) {
                    mAdapter = new BookAdapter(mBookList);
                }
                mBookRecyclerView.setAdapter(mAdapter);
                FirebaseFirestore database = FirebaseFirestore.getInstance();
                String id = CurrentUser.UID;
                System.out.println("id==================="+id);
                DocumentReference docRef = database.collection("users").document(CurrentUser.UID);


                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null) {
                                String bookId = document.getString("issuedbooks");
                                pullBooks(bookId);
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });


                /*
                database.collection("books")

                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    mBookList.clear();
                                    for (DocumentSnapshot document : task.getResult()) {
                                        Book book = document.toObject(Book.class);
                                        book.setBookId(document.getId());
                                        mBookList.add(book);
                                    }
                                    mAdapter.notifyDataSetChanged();
                                } else {
                                    //TODO
                                }
                            }
                        });
                        */
            }



            private class BookHolder extends RecyclerView.ViewHolder

                    implements View.OnClickListener, View.OnLongClickListener {

                private Book mBook;
                private ImageView mBookCover;
                private TextView mBookTitle;
                private TextView mBookAuthor;
                //private CheckBox mCheckBox;
                private TextView mReturns;


                public BookHolder(LayoutInflater inflater, ViewGroup parent) {
                    super(inflater.inflate(R.layout.list_item_book, parent, false));
                    itemView.setOnClickListener(this);
                    itemView.setOnLongClickListener(this);

                    mBookCover = (ImageView) itemView.findViewById(R.id.book_cover);
                    mBookTitle = (TextView) itemView.findViewById(R.id.book_title);
                    mBookAuthor = (TextView) itemView.findViewById(R.id.book_author_name);
                    mCheckBox = (CheckBox) itemView.findViewById(R.id.return_user_input);
                   // mReturns = (TextView) itemView.findViewById(R.id.return_book);
                    //mReturns.setOnClickListener(this);


                }

                public void bind(Book book) {

                /*
                Set up the widgets text here using DB call
                */
                    mBook = book;
                    mBookTitle.setText(book.getTitle());
                    mBookAuthor.setText(book.getAuthor());

                    mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            if(compoundButton.isChecked()){
                                //compoundButton.setChecked();
                                returnsBookList.add(mBook);
                            }
                            else{
                                if(!returnsClicked){
                                    if(returnsBookList.contains(mBook)){
                                        returnsBookList.remove(mBook);
                                    }
                                }

                                /*if(returnsBookList.size() > 0 && returnsBookList.contains(mBook)){
                                    returnsBookList.remove(mBook);
                                }*/
                            }
                        }
                    });


                }

                @Override
                public void onClick(View view) {
                    //TODO Shaurya's activity (CityViewActivity)
                    Intent intent = new Intent(getActivity(), SearchDetailActivity.class);
                    intent.putExtra("bookId", mBook.getBookId());
                    startActivity(intent);
                }

                @Override
                public boolean onLongClick(View view) {

                    return true;

                }
            }

            private class BookAdapter extends RecyclerView.Adapter<BookHolder> {

                private List<Book> mBookList;

                public BookAdapter(List<Book> bookList) {
                    mBookList = bookList;
                }

                @Override
                public BookHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                    return new BookHolder(layoutInflater, parent);
                }

                @Override
                public void onBindViewHolder(BookHolder holder, int position) {
                    Book book = mBookList.get(position);
                    holder.bind(book);
                }

                @Override
                public int getItemCount() {
                    return mBookList.size();
                }
            }
        }


        /**
         * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
         * one of the sections/tabs/pages.
         */
        public class SectionsPagerAdapter extends FragmentPagerAdapter {


            public SectionsPagerAdapter(FragmentManager fm) {
                super(fm);
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return mFragmentPageTitle.get(position);
            }

            @Override
            public Fragment getItem(int position) {
                // getItem is called to instantiate the fragment for the given page.
                // Return a PlaceholderFragment (defined as a static inner class below).
                return mFragmentList.get(position);
            }

            @Override
            public int getCount() {
                // Show 3 total pages.
                return mFragmentList.size();
            }
        }
    }


