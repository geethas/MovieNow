package view.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import control.asyncresponse.TheaterAsyncResponse;
import control.asynctask.MovieAsyncTask;
import gsrini.movienow.view.R;
import io.fabric.sdk.android.Fabric;
import model.constant.Constants;
import model.data.StaticDataHolder;
import model.vo.MovieListVO;
import model.vo.MovieVO;
import model.vo.RatingVO;
import view.adapter.MovieListAdapter;
import view.helper.CalendarDataHelper;
import view.helper.MovieDataHelper;

public class MovieView extends ActionBarActivity implements Constants, TheaterAsyncResponse, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public MovieAsyncTask asyncTask;

    String TAG = this.getClass().getSimpleName();

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager mLayoutManager;

    List<MovieListVO> listVOMovieList = new ArrayList<>();

    boolean critics = true;

    RelativeLayout loadingPanel;

    private SearchView searchView;
    private TextView radiusNum = null;

    // loads location service
    private GoogleApiClient mGoogleApiClient;
    Location mLastLocation;

    SharedPreferences prefs = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

      //  Fabric.with(this, new Crashlytics());

        setContentView(R.layout.activity_movie);

        Toolbar toolbar = (Toolbar) findViewById(R.id.custom_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        getSupportActionBar().setTitle(R.string.title_activity_movie_view);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        // loads current location
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        loadingPanel = (RelativeLayout) findViewById(R.id.loadingPanel);

        // create a list view for the movie info. Each click of the movie will send
        // event to another activity for more info on it
        mRecyclerView = (RecyclerView) findViewById(R.id.movie_list_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        LinearLayout.LayoutParams mParam = new LinearLayout.LayoutParams(width,(height *3)/4);
        mRecyclerView.setLayoutParams(mParam);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        adapter = new MovieListAdapter(this, listVOMovieList);
        mRecyclerView.setAdapter(adapter);

        // ==================================================================
        // add radiusNum slide bar
        // control the distance of theaters from current location
        final DiscreteSeekBar radiusBar = (DiscreteSeekBar) findViewById(R.id.radius_bar);
        radiusNum = (TextView) findViewById((R.id.radius_num));

        // default radius should be 5 miles
        if(StaticDataHolder.radiusNum == 0){
            StaticDataHolder.radiusNum = 5;
        }

        // set initial value or retaining value here
        radiusBar.setProgress(StaticDataHolder.radiusNum);
        radiusNum.setText("Range: " + StaticDataHolder.radiusNum + " mi");
        Log.d(TAG, "Range: " + radiusNum);

        radiusBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {

            int progressChanged = 0;

            @Override
            public void onProgressChanged(DiscreteSeekBar discreteSeekBar, int progress, boolean b) {
                progressChanged = progress;
                // int val = (progress * (discreteSeekBar.getWidth() - 2 * discreteSeekBar.getThumbOffset())) / discreteSeekBar.getMax();
                radiusNum.setText("Range: " + progress + " mi");
                Log.d(TAG, "New Radius: " + radiusNum);
                StaticDataHolder.radiusNum = progress;
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar discreteSeekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar discreteSeekBar) {

                String radiusNum = String.valueOf(progressChanged);
                // ----- get theaters based on new radiusNum setting
                if(StaticDataHolder.radiusStr != null && progressChanged > Integer.parseInt(StaticDataHolder.radiusStr))
                    theatersForNewLocation(radiusNum, false);

            }
        });

    }

    private void setupCalendar(final CalendarDataHelper calendarData) {

        //Creating the instance of PopupMenu
        View menuItemView = findViewById(R.id.calendar_week);
        final PopupMenu popup = new PopupMenu(MovieView.this, menuItemView);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.calendar_popup_menu, popup.getMenu());
        //set the titles of menu items with the correct dates and days
        calendarData.setCalendarDates(popup.getMenu());

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {

                String dateTitle = item.getTitle().toString();

                // check the new item in calendar popup menu and un check old item
                calendarData.checkCalendarDays(item);

                String selectedDate = null;
                Log.d(TAG, "Date Title: " + dateTitle);
                for (int i=0; i < dateTitle.length(); i++) {
                    if(Character.isDigit(dateTitle.charAt(i))) {
                        selectedDate = dateTitle.substring(dateTitle.indexOf(dateTitle.charAt(i)), dateTitle.length());
                        break;
                    }
                }

                loadingPanel.setVisibility(View.VISIBLE);
                Log.d(TAG, "Selected Date: " + selectedDate);
                StaticDataHolder.date = selectedDate;
                asyncTask = new MovieAsyncTask(MovieView.this);
                asyncTask.delegate = MovieView.this;
                //send theaterId to get movie information for that theater
                asyncTask.execute(THEATER_SHOWING_OP, null, null, selectedDate);
                return true;
            }
        });

        // Force icons to show
        Object menuHelper;
        Class[] argTypes;
        try {
            Field fMenuHelper = PopupMenu.class.getDeclaredField("mPopup");
            fMenuHelper.setAccessible(true);
            menuHelper = fMenuHelper.get(popup);
            argTypes = new Class[] { boolean.class };
            menuHelper.getClass().getDeclaredMethod("setForceShowIcon", argTypes).invoke(menuHelper, true);
        } catch (Exception e) {
            // Possible exceptions are NoSuchMethodError and NoSuchFieldError
            //
            // In either case, an exception indicates something is wrong with the reflection code, or the
            // structure of the PopupMenu class or its dependencies has changed.
            //
            // These exceptions should never happen since we're shipping the AppCompat library in our own apk,
            // but in the case that they do, we simply can't force icons to display, so log the error and
            // show the menu normally.

            Log.w(TAG, "error forcing menu icons to show", e);
            popup.show();
            return;
        }

        popup.show();
  }


     /**
     * setup search view widget
     *
     */
    private void setupSearchView() {

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        // keep it expanded
        searchView.setIconifiedByDefault(false);

        // listens for user typing location in search view. If user executes after typing, it
        // will get theaters for new location
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String location) {
                if (location.length() != 0) {
                    // hide keyboard after text submits
                    searchView.clearFocus();
                    // get theater info for new location
                    theatersForNewLocation(location, false);
                    return true;
                }
                return false;
            }
        });

    }

    private void setItemsVisibility(Menu menu, MenuItem exception, boolean visible) {

        Log.d(TAG, "Setting items as " + visible);
        for (int i=0; i<menu.size(); ++i) {
            MenuItem item = menu.getItem(i);
            if (item != exception) item.setVisible(visible);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.movie_list_menu, menu);

        final MenuItem searchItem = menu.findItem(R.id.search_bar);
        searchView = (SearchView) searchItem.getActionView();
        searchItem.collapseActionView();

        ImageView searchIcon = (ImageView) searchView.findViewById(android.support.v7.appcompat.R.id.search_mag_icon);
        searchIcon.setImageResource(R.drawable.search);

        searchView.setQueryHint(getResources().getString(R.string.search_hint));
        searchView.clearFocus();

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                Log.d(TAG, "On Click for Searchview");
                setItemsVisibility(menu, searchItem, false);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {

                //DO SOMETHING WHEN THE SEARCHVIEW IS CLOSING
                Log.d(TAG, "Closing Searchview");
                setItemsVisibility(menu, searchItem, true);
                return true;
            }
        });

        setupSearchView();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        CalendarDataHelper calendarData = new CalendarDataHelper();

        switch (item.getItemId()) {

            case R.id.calendar_week:
                setupCalendar(calendarData);
                return true;

            case R.id.rating_sort:
                sortByRating(item);
                return true;

            case R.id.my_location:
                Log.d(TAG, "Get your location");
                theatersForNewLocation(null, true);
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void sortByRating(MenuItem item) {

        if(critics) {
            critics = false;
            item.setIcon(R.drawable.audience);
            List<MovieListVO> list = new ArrayList<>();
            list.addAll(listVOMovieList);
            addToAdapter(list);
        }
        else {
            critics = true;
            item.setIcon(R.drawable.critic);
            List<MovieListVO> list = new ArrayList<>();
            list.addAll(listVOMovieList);
            addToAdapter(list);
        }
    }

    @Override
    protected void onStart() {

        super.onStart();
        //Connect the client.
        if (isGooglePlayServicesAvailable()) {
            mGoogleApiClient.connect();
        }

        if (searchView != null) {
            searchView.setQuery("", false);
            searchView.setQueryHint(getResources().getString(R.string.search_hint));
            searchView.clearFocus();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (prefs.getBoolean("firstrun", true)) {
            // Do first run stuff here then set 'firstrun' as false
            // using the following line to edit/commit prefs
            Log.d(TAG, "First Run");
            prefs.edit().putBoolean("firstrun", false).commit();
            Intent intent = new Intent(this, InstructionsActivity.class);
            this.startActivity(intent);
        }
    }


    /**
     * All information for that theater has been retrieved, display it to the user
     *
     * @param movieMap has movie info
     * @param ratingsMap has ratings for each movie
     * @param imageMap has movie image
     */
    @Override
    public void processFinished(Map<String, MovieVO> movieMap, Map<String, RatingVO> ratingsMap, Map<String, String> imageMap) {
        Log.d(TAG, "Async Task Returned! Got Rating Info");

        // final version of list
        List<MovieListVO> addItems = new ArrayList<>();

        MovieDataHelper movieDataHelper = new MovieDataHelper();

        Map<String, MovieVO> movieCache = StaticDataHolder.movieCache.get(StaticDataHolder.postalCode);
        //----- Go through movie map of the theater selected and set values needed from the map for the view
        if(movieMap == null || movieMap.isEmpty()){
            //Toast.makeText(this, "No Movies to Display for this Location, Please Try Again", Toast.LENGTH_SHORT).show();
            String title = "Location Not Found";
            String cast = "Type Location Above";
            MovieListVO movieListVO = new MovieListVO(title, null, null, null, cast, null, null);
            addItems.add(movieListVO);
            addToAdapter(addItems);
            loadingPanel.setVisibility(View.GONE);
            return;
        }

        Log.d(TAG, "Size of Movie Map: " + movieMap.size());

        for(Map.Entry<String, MovieVO> entry : movieMap.entrySet()) {

            MovieVO value = entry.getValue();
            if (value != null) {

                String title = value.getTitle();
                String topCast = value.getTopCast();

                // -----if title has 3D, don't need to print title again
                if (title.contains("3D")) {
                    String newTitle = null;

                    if (title.contains(" 3D") && !title.contains("IMAX")) {

                        if(title.contains(":"))
                            newTitle = title.substring(0, title.indexOf(":"));

                        String newTitle1 = title.substring(0, title.indexOf(" 3D"));

                        Log.d(TAG, "This movie has 3D showtime: " + title);
                        Log.d(TAG, "New title for movie: " + newTitle);
                        Log.d(TAG, "New title 1 for movie: " + newTitle1);

                        if(movieCache != null) {
                            if (newTitle != null && movieCache.get(newTitle) != null) {
                                Log.d(TAG, "Movie Cache has this Title: " + newTitle);
                                //if(value.getShowtimeVOMap() != null)
                                //showtimeVO3dMap.put(newTitle, value.getShowtimeVOMap());
                                movieCache.get(newTitle).setShowtimeVO3DMap(value.getShowtimeVOMap());
                            }

                            else if (movieCache.get(newTitle1) != null) {
                                Log.d(TAG, "Movie Cache has this Title: " + newTitle1);
                                //showtimeVO3dMap.put(newTitle1, value.getShowtimeVOList());
                                movieCache.get(newTitle1).setShowtimeVO3DMap(value.getShowtimeVOMap());
                            }
                        }

                    }
                    if (title.contains(" IMAX")) {

                        if(title.contains(":"))
                            newTitle = title.substring(0, title.indexOf(":"));

                        String newTitle1 = title.substring(0, title.indexOf(" IMAX"));

                        Log.d(TAG, "This movie has IMAX 3D showtime: " + newTitle);

                        if(movieCache != null) {
                            if (newTitle != null && movieCache.get(newTitle) != null) {
                                Log.d(TAG, "Movie Cache has this Title" + newTitle);
                                //showtimeVOImaxMap.put(newTitle, value.getShowtimeVOList());
                                movieCache.get(newTitle).setShowtimeVOIMAX3DMap(value.getShowtimeVOMap());
                            }

                            else if (movieCache.get(newTitle1) != null) {
                                Log.d(TAG, "Movie Cache has this Title" + newTitle1);
                                //showtimeVOImaxMap.put(newTitle1, value.getShowtimeVOList());
                                movieCache.get(newTitle1).setShowtimeVOIMAX3DMap(value.getShowtimeVOMap());
                            }
                        }

                    }

                        continue;
                }


                if(ratingsMap == null || ratingsMap.size() >= 0) {
                    ratingsMap = StaticDataHolder.ratingCache;
                }
                if(imageMap == null || imageMap.size() >= 0) {
                    imageMap = StaticDataHolder.imageMapCache;
                }

                //----- format top cast string properly. Only add 2 cast names
                String cast = movieDataHelper.getCast(topCast, false);

                //----- format runtime properly
                String typeAndRuntime = movieDataHelper.getMovieType(value.getCode(), value.getRunTime());

                //----- get rating scores and image
                String criticScore = movieDataHelper.getCriticScore(title, ratingsMap);
                String audienceScore = movieDataHelper.getAudienceScore(title, ratingsMap);
                String criticRating = movieDataHelper.getCriticRating(title, ratingsMap);

                // params for listVO: String name, String critic, String audience, String typeAndRuntime, String cast, Bitmap thumbnail, criticRating
                MovieListVO movieListVO = new MovieListVO(title, criticScore, audienceScore, typeAndRuntime, cast, imageMap.get(title), criticRating );

                movieListVO.setSelectedDate(StaticDataHolder.date);

                Log.d(TAG, "Values in ListVO");
                Log.d(TAG, "============================");
                Log.d(TAG, "Movie Title: " + title);
                Log.d(TAG, "Critics Score: " + criticScore);
                Log.d(TAG, "Audience Score: " + audienceScore);
                Log.d(TAG, "Type and Runtime: " + typeAndRuntime);
                Log.d(TAG, "Cast: " + cast);
                Log.d(TAG, "Image Bitmap: " + imageMap.get(title));
                Log.d(TAG, "Critic Rating: " + criticRating);
                Log.d(TAG, "============================");

                addItems.add(movieListVO);
            }
        }
        // reorder list to have highest critic rating on top and lowest critic rating on bottom
        addToAdapter(addItems);
        loadingPanel.setVisibility(View.GONE);
    }


    private synchronized void addToAdapter(List<MovieListVO> list){

        // clear list items so when items are added again, it doesn't get added more than once
        listVOMovieList.clear();

        if(list == null)
            return;

        // reorder list to have highest critic/audience rating on top and lowest critic/audience rating on bottom
        Collections.sort(list, new Comparator<MovieListVO>() {
            @Override
            public int compare(MovieListVO lhs, MovieListVO rhs) {
                // set all null values to 0 so they go to the bottom of the list
                if (lhs.getCriticRating() == null) {
                    lhs.setCriticRating("0");
                }
                if (rhs.getCriticRating() == null) {
                    rhs.setCriticRating("0");
                }
                if (lhs.getAudienceRating() == null) {
                    lhs.setAudienceRating("0");
                }
                if (rhs.getAudienceRating() == null) {
                    rhs.setAudienceRating("0");
                }

                // second item has a greater rating than the first, switch items
                if(critics) {
                    if (Integer.parseInt(lhs.getCriticRating()) < Integer.parseInt(rhs.getCriticRating())) {
                        return 1;
                    }
                }
                else {
                    if (Integer.parseInt(lhs.getAudienceRating()) < Integer.parseInt(rhs.getAudienceRating())) {
                        return 1;
                    }
                }

                return -1;
            }
        });

        // add all items after sorting is done
        listVOMovieList.addAll(list);
        Log.d(TAG, "Size of ListVOList: " + listVOMovieList.size());

        // notify adapter data has been changed
        loadingPanel.setVisibility(View.GONE);
        adapter.notifyDataSetChanged();
    }
    /**
     *  Using the title from the marker that user clicked, get the theater id and send
     *  to async task to get movies and ratings from that theater
     */
    private void getMovieInfo(){
        // create another thread for rotten tomatoes api
        // parse movie info
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

          if(StaticDataHolder.movieCache != null && StaticDataHolder.movieCache.get(StaticDataHolder.postalCode) != null
                  && StaticDataHolder.ratingCache != null && StaticDataHolder.imageMapCache != null) {
              //Map<String, MovieVO> movieMap, Map<String, RatingVO> ratingsMap, Map<String, Bitmap> imageMap
              Log.d(TAG, "Already have all the information for this postal code");
              processFinished(StaticDataHolder.movieCache.get(StaticDataHolder.postalCode), null, null);
          }
          else if(mLastLocation != null) {

              if(StaticDataHolder.latitude == 0 && StaticDataHolder.longitude == 0){
                  StaticDataHolder.latitude = mLastLocation.getLatitude();
                  StaticDataHolder.longitude = mLastLocation.getLongitude();
              }

              Log.d(TAG, "Latitude: (statically) " + String.valueOf(StaticDataHolder.latitude));
              Log.d(TAG, "Longitude (statically): " + String.valueOf(StaticDataHolder.longitude));

              // if theater info doesn't exist then get it
             ///mRecyclerView.setVisibility(View.INVISIBLE);
              loadingPanel.setVisibility(View.VISIBLE);

              asyncTask = new MovieAsyncTask(this);
              asyncTask.delegate = this;
              //send theaterId to get movie information for that theater
              asyncTask.execute(THEATER_SHOWING_OP, String.valueOf(mLastLocation.getLatitude()), String.valueOf(mLastLocation.getLongitude()));
              // }
          }
    }

    @Override
    public void onConnected(Bundle bundle) {
        // get movie information for theater
        getMovieInfo();
    }

    /**
     * Called when the Activity is no longer visible. Disconnect Google API Client
     */
    @Override
    protected void onStop() {
        // Disconnecting the client invalidates it.
        mGoogleApiClient.disconnect();
        //mGoogleApiClientPlaces.disconnect();
        super.onStop();
    }

    /**
     * Handle results returned to the FragmentActivity by Google Play services
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Decide what to do based on the original request code
        switch (requestCode) {

            case CONNECTION_FAILURE_RESOLUTION_REQUEST:
			/*
			 * If the result code is Activity.RESULT_OK, try to connect again
			 */
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        mGoogleApiClient.connect();
                        break;
                }

        }
    }

    /**
     * Check if GooglePlayServices is available before trying to use any
     * Google APIs
     */
    private boolean isGooglePlayServicesAvailable() {
        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d("Location Updates", "Google Play services is available.");
            return true;
        } else {
            // Get the error dialog from Google Play services
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                    CONNECTION_FAILURE_RESOLUTION_REQUEST);

            // If Google Play services can provide an error dialog
            if (errorDialog != null) {
                // Create a new DialogFragment for the error dialog
                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
                errorFragment.setDialog();
                //errorFragment.show(getSupportFragmentManager(), "Location Updates");
            }

            return false;
        }
    }


    /**
     * Called by Location Services if the attempt to get Location Services fails.
     */
//	@Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
		/*
		 * Google Play services can resolve some errors it detects. If the error
		 * has a resolution, try sending an Intent to start a Google Play
		 * services activity that can resolve error.
		 */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
				/*
				 * Thrown if Google Play services canceled the original
				 * PendingIntent
				 */
            } catch (IntentSender.SendIntentException e) {
                Log.e(TAG, "Attempt to get Location Failed! " + e.getMessage());
            }
        } else {
            Toast.makeText(getApplicationContext(),
                    "Sorry. Location services not Available", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Define a DialogFragment that displays the error dialog
     */
    public static class ErrorDialogFragment extends DialogFragment {

        //Default constructor. Sets the dialog field to null
        public ErrorDialogFragment() {
            super();
        }

        //Set the dialog to display
        public void setDialog() {
        }

    }

    @Override
    public void onConnectionSuspended(int arg0) {
        onStart();
    }
    /**
     * If user types new location in search widget, get nearby theaters for that
     * @param query, myLocation
     */
    private void theatersForNewLocation(String query, boolean myLocation) {

        Log.d(TAG, "Searching for : " + query);

        if(myLocation){
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);

            if(mLastLocation != null) {
                loadingPanel.setVisibility(View.VISIBLE);
                asyncTask = new MovieAsyncTask(this);
                asyncTask.delegate = this;
                asyncTask.execute(THEATER_SHOWING_OP, String.valueOf(mLastLocation.getLatitude()), String.valueOf(mLastLocation.getLongitude()));
            }

        }
        else {
            // get nearby theaters of new location
            loadingPanel.setVisibility(View.VISIBLE);
            asyncTask = new MovieAsyncTask(this);
            asyncTask.delegate = this;
            asyncTask.execute(THEATER_SHOWING_OP, query);
        }

    }

}





