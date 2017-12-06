package com.example.shauryamittal.librarymanagement;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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

import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

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

        private RecyclerView mCityRecyclerView;
        private static final String ARG_SECTION_NUMBER = "section_number";

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
            View rootView = inflater.inflate(R.layout.fragment_view_books, container, false);
            mCityRecyclerView = (RecyclerView) rootView
                    .findViewById(R.id.city_recycler_view);
            mCityRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        }

        private class CityHolder extends RecyclerView.ViewHolder
                implements View.OnClickListener, View.OnLongClickListener {


            public CityHolder(LayoutInflater inflater, ViewGroup parent) {
                super(inflater.inflate(R.layout.list_item_city, parent, false));
                itemView.setOnClickListener(this);
                itemView.setOnLongClickListener(this);

                //mCurrentTimeView = (TextView) itemView.findViewById(R.id.current_time);
                //mCityNameView = (TextView) itemView.findViewById(R.id.city_name);
                //mCurrentTemperatureView = (TextView) itemView.findViewById(R.id.city_temperature);

            }

            public void bind(City city) {


                SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.preference_file), Context.MODE_PRIVATE);

                String scale = sharedPref.getString("TemperatureScale", "imperial");

                Log.d(TAG, "Scale Value :" + scale);


                mCity = city;
                Log.d(TAG, "Before binding :" + city.toString());
                Log.d(TAG, "inside getWeatherData()" + "Latitude :" + city.getLatitude() + "Longitude :" + city.getLongitude());
                String apiKey = "9c358b9bde40e6e012c0c19c37f752d3";

                RequestQueue queue = Volley.newRequestQueue(getActivity());
                String temperature_url ="http://api.openweathermap.org/data/2.5/weather?lat=" + city.getLatitude() + "&lon=" + city.getLongitude() + "&units="+scale+"&APPID=" + apiKey;
                Log.d(TAG, "URL " + temperature_url);
                //http://api.openweathermap.org/data/2.5/weather?lat=37.3382082&lon=-121.8863286&units=imperial&APPID=9c358b9bde40e6e012c0c19c37f752d3


                JsonObjectRequest jsObjRequest = new JsonObjectRequest
                        (Request.Method.GET, temperature_url, null, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d(TAG, "Response from URL: " + response);
                                CurrentWeather weather = new CurrentWeather(response);
                                Log.d(TAG, "Current Temp: " + weather.getCurrentTemp());
                                mCurrentTemperatureView.setText(String.valueOf(weather.getCurrentTemp()) + "°");
                                //mCurrentTemperature = String.valueOf(weather.getCurrentTemp());
                                Log.v("City ", weather.getCityName());
                                Log.v("Current Temp ", weather.getCurrentTemp() + "");
                                Log.v("Low Temp ", weather.getLowTemp() + "");
                                Log.v("High Temp ", weather.getHighTemp() + "");
                                Log.v("Weather Condition", weather.getWeatherCondition());
                                Log.v("Humidity", weather.getHumidity() + "");
                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // TODO Auto-generated method stub

                                Log.v("ERROR:", error.toString());

                            }
                        });

                queue.add(jsObjRequest);

                UTCTimestamp = new GregorianCalendar().getTimeInMillis()/1000;

                String localtime_url ="https://maps.googleapis.com/maps/api/timezone/json?location=" + city.getLatitude() + ","+ city.getLongitude() +"&timestamp="+UTCTimestamp+"&key=AIzaSyBb31ykX-88UrhoTTyyJhZjcestZyKGINQ";
                Log.d(TAG, "URL " + localtime_url);

                JsonObjectRequest jsObjTimeRequest = new JsonObjectRequest
                        (Request.Method.GET, localtime_url, null, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d(TAG, "Response from localtime_url: " + response);
                                try {
                                    Log.d(TAG, "TimeZoneId " + response.getString("timeZoneId"));
                                    timeZoneId = response.getString("timeZoneId");

                                    TimeZone tz = TimeZone.getTimeZone(timeZoneId);
                                    Calendar calendar = Calendar.getInstance(tz);

                                    mCurrentTimeView.setText(String.format("%02d", calendar.get(Calendar.HOUR)) + ":" +
                                            String.format("%02d", calendar.get(Calendar.MINUTE)) + "  " +
                                            (calendar.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM"));

                                    String time = String.format("%02d", calendar.get(Calendar.MONTH) + 1) + "/" +	       // January is 0
                                            String.format("%02d", calendar.get(Calendar.DATE)) + "/" +
                                            calendar.get(Calendar.YEAR) + "   " +
                                            String.format("%02d", calendar.get(Calendar.HOUR)) + ":" +
                                            String.format("%02d", calendar.get(Calendar.MINUTE)) + "  " +
                                            (calendar.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM");

                                    Log.d(TAG, "Current Local Time :" + time);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // TODO Auto-generated method stub

                                Log.v("ERROR:", error.toString());

                            }
                        });


                queue.add(jsObjTimeRequest);
                city.setLocalTime(mCurrentTimeView.getText().toString());
                city.setCurrentTemperature(mCurrentTemperatureView.getText().toString());
                Log.d(TAG, "City after binding : " + city.toString());
                mCurrentTimeView.setText(String.valueOf(city.getLocalTime()));
                mCityNameView.setText(city.getCityName());
                Log.d(TAG, "City after binding : " + city.toString());
            }

            @Override
            public void onClick(View view) {
                //TODO Shaurya's activity (CityViewActivity)
                Log.d(TAG, "inside onClick() method");
                Intent intent = new Intent(getActivity(), WeatherDetailsActivity.class);
                int index = CityLab.get(getContext()).getCities().indexOf(mCity);
//            if(mCurrentCity.equals(mCity.getCityName())){
//                cmpe277.gaganjain.weatherapp.Location.currenCity = mCity.getCityName();
//                intent.putExtra("current_city", true);
//            }
//            else{
//                intent.putExtra("current_city", false);
//            }
                intent.putExtra("index", index);
                intent.putExtra("cityName", mCity.getCityName());
                intent.putExtra("latitude", mCity.getLatitude());
                intent.putExtra("longitude", mCity.getLongitude());
                startActivity(intent);
            }

            @Override
            public boolean onLongClick(View view) {

                Log.d(TAG, "inside onLongClick() method");

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

                return true;
            }
        }

        private class CityAdapter extends RecyclerView.Adapter<CityHolder> {

            private List<City> mCities;

            public CityAdapter(List<City> cities) {
                mCities = cities;
            }

            @Override
            public CityHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                return new CityHolder(layoutInflater, parent);
            }

            @Override
            public void onBindViewHolder(CityHolder holder, int position) {
                City city = mCities.get(position);
                holder.bind(city);
            }

            @Override
            public int getItemCount() {
                return mCities.size();
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
            return 3;
        }
    }
}
