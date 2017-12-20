package com.example.shauryamittal.librarymanagement;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shauryamittal.librarymanagement.model.Book;
import com.example.shauryamittal.librarymanagement.model.BookFactory;
import com.example.shauryamittal.librarymanagement.model.CurrentUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class LibrarianViewBooksActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_librarian_view_books);
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if (fragment == null) {
            fragment = new PlaceholderFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }

    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */

        private static final String TAG = "PlaceholderFragment";
        private BookFactory factory;
        //private Book book;
        private RecyclerView mBookRecyclerView;
        private BookAdapter mAdapter;
        private DatabaseReference mDatabase;
        private static final String ARG_SECTION_NUMBER = "section_number";
        private List<Book> mBookList;

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static ViewBooksActivity.PlaceholderFragment newInstance(int sectionNumber) {
            ViewBooksActivity.PlaceholderFragment fragment = new ViewBooksActivity.PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            mBookList = new ArrayList<>();
            View rootView = inflater.inflate(R.layout.fragment_lib_view_books, container, false);
            mBookRecyclerView = rootView
                    .findViewById(R.id.book_recycler_view);
            mBookRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

            Log.d(TAG, "inside onCreateView");

            return rootView;
        }

        @Override
        public void onResume() {
            super.onResume();
            Log.d(TAG, "inside onResume()");
            updateUI();
        }

//        public void pullBooks(String bookId){
//
//            if(bookId != null){
//                FirebaseFirestore database = FirebaseFirestore.getInstance();
//                database.collection("books")
//                        .whereEqualTo("bookId", bookId)
//                        .get()
//                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                            @Override
//                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                if (task.isSuccessful()) {
//                                    mBookList.clear();
//                                    for (DocumentSnapshot document : task.getResult()) {
//                                        Book book = document.toObject(Book.class);
//                                        book.setBookId(document.getId());
//                                        mBookList.add(book);
//                                    }
//                                    mAdapter.notifyDataSetChanged();
//                                } else {
//                                    //TODO
//                                }
//                            }
//                        });
//            }
//
//        }

        private void updateUI() {

            Log.d(TAG, "inside updateUI()");
            //factory = BookFactory.get(getActivity());
            //List<Book> bookList = factory.getBookList();

            if (mAdapter == null) {
                mAdapter = new BookAdapter(mBookList);
            }
            mBookRecyclerView.setAdapter(mAdapter);

            FirebaseFirestore database = FirebaseFirestore.getInstance();
            database.collection("books")
                    .whereEqualTo("librarianId", CurrentUser.UID)
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





//            database.collection("books")
//                    .get()
//                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                            if (task.isSuccessful()) {
//                                for (DocumentSnapshot document : task.getResult()) {
//                                    Book book = document.toObject(Book.class);
//                                    book.setBookId(document.getId());
//                                    mBookList.add(book);
//                                }
//                                mAdapter.notifyDataSetChanged();
//                            } else {
//                                //TODO
//                            }
//                        }
//                    });
        }


        private class BookHolder extends RecyclerView.ViewHolder

                implements View.OnClickListener, View.OnLongClickListener {

            private Book mBook;
            private ImageView mBookCover;
            private TextView mBookTitle;
            private TextView mBookAuthor;


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
                mBook = book;
                mBookTitle.setText(book.getTitle());
                mBookAuthor.setText(book.getAuthor());
            }

            @Override
            public void onClick(View view) {
                //TODO Shaurya's activity (CityViewActivity)
                Intent intent = new Intent(getActivity(), LibrarianBookDetailActivity.class);
                intent.putExtra("bookId", mBook.getBookId());
                startActivity(intent);
                /*Log.d(TAG, "inside onClick() method");
                Intent intent = new Intent(getActivity(), WeatherDetailsActivity.class);
                int index = CityLab.get(getContext()).getCities().indexOf(mCity);
                intent.putExtra("index", index);
                intent.putExtra("cityName", mCity.getCityName());
                intent.putExtra("latitude", mCity.getLatitude());
                intent.putExtra("longitude", mCity.getLongitude());

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.librarian_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.backRedirect:
                startActivity(new Intent(LibrarianViewBooksActivity.this, LibrarianHomepageActivity.class));
                return true;
            case R.id.homePageRedirect:
                startActivity(new Intent(LibrarianViewBooksActivity.this, LibrarianHomepageActivity.class));
                return true;
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                finish();
                CurrentUser.destroyCurrentUser();
                startActivity(new Intent(LibrarianViewBooksActivity.this, LoginActivity.class));
        }

        return true;
    }
}
