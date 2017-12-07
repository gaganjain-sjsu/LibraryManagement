package com.example.shauryamittal.librarymanagement;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
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

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shauryamittal.librarymanagement.model.Book;
import com.example.shauryamittal.librarymanagement.model.BookFactory;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class ViewBooksActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_books);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_books, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.logout) {
            FirebaseAuth.getInstance().signOut();
            finish();
            CurrentUser.destroyCurrentUser();
            startActivity(new Intent(ViewBooksActivity.this, LoginActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */

        private static final String TAG = "PlaceholderFragment";
        private BookFactory factory;
        private Book book;
        private RecyclerView mBookRecyclerView;
        private BookAdapter mAdapter;
        private ImageView mBookCover;
        private TextView mBookTitle;
        private TextView mBookAuthor;
        private DatabaseReference mDatabase;
        private static final String ARG_SECTION_NUMBER = "section_number";
        private List<Book> mBookList;
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
            /*View rootView = inflater.inflate(R.layout.fragment_view_books, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;*/
            mBookList = new ArrayList<>();
            View rootView = inflater.inflate(R.layout.fragment_view_books, container, false);
            mBookRecyclerView = rootView
                    .findViewById(R.id.book_recycler_view);
            mBookRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

            Log.d(TAG, "inside onCreateView");


            //fetchBooks();
            //updateUI();
            return rootView;
        }

        @Override
        public void onResume() {
            super.onResume();
            //getCurrentLocation(getActivity());
            //Log.d(TAG, "inside onResume()");
            //fetchBooks();
            updateUI();
        }

        private void updateUI() {

            Log.d(TAG, "inside updateUI()");
            factory = BookFactory.get(getActivity());
            //List<Book> bookList = factory.getBookList();

            if(mAdapter == null){
                mAdapter = new BookAdapter(mBookList);
            }
            mBookRecyclerView.setAdapter(mAdapter);

            FirebaseFirestore database= FirebaseFirestore.getInstance();
            //CollectionReference mRef=database.collection("books");

            database.collection("books")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            //BookFactory factory = BookFactory.get(getActivity());

                            if (task.isSuccessful()) {
                                for (DocumentSnapshot document : task.getResult()) {
                                    Book book = document.toObject(Book.class);
                                    mBookList.add(book);
                                    //factory.addBook(book);
                                    //Log.d(TAG, "book list size:" + mBookList.size());
                                    //Log.d(TAG, "Adapter book list size:" + mAdapter.getItemCount());
                                }
                                //mAdapter.mBookList = factory.getBookList();
                                mAdapter.notifyDataSetChanged();
                            } else {
                                //Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }


        private class BookHolder extends RecyclerView.ViewHolder

                implements View.OnClickListener, View.OnLongClickListener {


            public BookHolder(LayoutInflater inflater, ViewGroup parent) {
                super(inflater.inflate(R.layout.list_item_book, parent, false));
                itemView.setOnClickListener(this);
                itemView.setOnLongClickListener(this);

                mBookCover = (ImageView) itemView.findViewById(R.id.book_cover);
                mBookTitle = (TextView) itemView.findViewById(R.id.book_title);
                mBookAuthor = (TextView) itemView.findViewById(R.id.book_author_name);

            }

            public void bind(Book book) {

                /*
                Set up the widgets text here using DB call
                */
                mBookTitle.setText(book.getTitle());
                mBookAuthor.setText(book.getAuthor());
            }

            @Override
            public void onClick(View view) {
                //TODO Shaurya's activity (CityViewActivity)

                /*Log.d(TAG, "inside onClick() method");
                Intent intent = new Intent(getActivity(), WeatherDetailsActivity.class);
                int index = CityLab.get(getContext()).getCities().indexOf(mCity);
                intent.putExtra("index", index);
                intent.putExtra("cityName", mCity.getCityName());
                intent.putExtra("latitude", mCity.getLatitude());
                intent.putExtra("longitude", mCity.getLongitude());
                startActivity(intent);
                */
            }

            @Override
            public boolean onLongClick(View view) {

                /*Log.d(TAG, "inside onLongClick() method");

                final CharSequence[] items = {"Delete City"};

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.preference_file), Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.remove(mCity.getCityName());
                        editor.commit();
                        mAdapter.mCities.remove(mCity);
                        mAdapter.notifyDataSetChanged();
                        Toast.makeText(getContext(), "City " + mCity.getCityName() + " removed from the list",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
                */
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
            public Fragment getItem(int position) {
                // getItem is called to instantiate the fragment for the given page.
                // Return a PlaceholderFragment (defined as a static inner class below).
                return PlaceholderFragment.newInstance(position + 1);
            }

            @Override
            public int getCount() {
                // Show 3 total pages.
                return 2;
            }
        }
    }

