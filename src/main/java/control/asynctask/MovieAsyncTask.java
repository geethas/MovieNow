package control.asynctask;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import control.asyncresponse.TheaterAsyncResponse;
import control.helper.ExecuteUrl;
import model.constant.Constants;
import model.data.GrabData;
import model.data.StaticDataHolder;
import model.vo.MovieVO;
import model.vo.RatingVO;
import view.activity.MovieView;

/**
 * Created by geethasrini on 2/21/15.
 */
public class MovieAsyncTask extends AsyncTask<String, Integer, String> implements Constants {

    String TAG = this.getClass().getSimpleName();

    public TheaterAsyncResponse delegate = null;

    private Map<String, RatingVO> ratingsMap = null;
    private Map<String, MovieVO> movieMap = null;
    private Map <String, String> imageMap = new HashMap<>();

    Context myContext;
    private Geocoder geocoder;

    public MovieAsyncTask(Context context) {
        myContext = context;
        geocoder = new Geocoder(myContext, Locale.US);
    }

    /**
     * Get zip code based on location
     *
     * @param latitude coordinate from map
     * @param longitude coordinate from map
     * @param name location name given if coordinates are not present
     * @return List of addresses
     */
    protected List<Address> getAddresses(double latitude, double longitude, String name) {

        Log.d(TAG, "Get Address");
        List<Address> addresses = null;

        try {
            if (name != null) {
                List<Address> gotAddresses = geocoder.getFromLocationName(name, 5);
                if(gotAddresses != null && gotAddresses.size() > 0) {
                    addresses = geocoder.getFromLocation(gotAddresses.get(0).getLatitude(), gotAddresses.get(0).getLongitude(), 1);
                }
            } else {
                Log.d("AsyncTask: Latitude", String.valueOf(latitude));
                Log.d("AsyncTask: Longitude", String.valueOf(longitude));
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
            }
        } catch (IOException e) {
            if (name != null) {
                Log.e(TAG, "Could not get Location using Name! " + e.getMessage());
            } else {
                Log.e(TAG, "Could not get Location using Latitude and Longitude! " + e.getMessage());
            }
        }

        // only one address should be returned if it exists
        return addresses;
    }

    /**
     * Delegate to appropriate method based on operation
     *
     * Operation MOVIES_LOCAL_THEATERS_OP: gets movies from local theaters within a 10 mile radius unless otherwise specified
     * Operation LOCAL_THEATERS_OP: get local theaters within a 10 mile radius unless otherwise specified
     * Operation ROTTEN_TOMATOES_OP: get ratings of all movies. Since the api returns only 16 movies as a default,
     * we send the movie count
     */
    @Override
    protected String doInBackground(String... arg0) {
        // ------first arg: operation, second arg: latitude or movie count, third arg:longitude or null
        String operation = arg0[0];
        Log.d(TAG, "Operation: " + operation);

        if (operation == null) {
            Log.d(TAG, "Unsupported Operation, Try Again");
            return null;
        }

        Log.d(TAG, "operation: " + operation);
        GrabData data = new GrabData();
        ExecuteUrl executeUrl = new ExecuteUrl();

        // this operation can have theater id and/or date which will get movies from that theater with today's start date or the one user selects
        if (operation.equals(THEATER_SHOWING_OP)) {

            String selectedDate = null;

            double latitude = 0;
            double longitude = 0;
            String locationName = null;
            String radius = null;
            String postalCode = null;

            if (arg0.length == 4) {
                selectedDate = arg0[3];
                Log.d(TAG, "selected date: " + selectedDate);
            }
            // ----- Comes here the first time app turns on using current location
            else if(arg0.length == 3) {

                // latitude and longitude exists
                latitude = Double.parseDouble(arg0[1]);
                longitude= Double.parseDouble(arg0[2]);

                // get previous state of radius if no changes has been made to it
                if(StaticDataHolder.radiusStr != null)
                    radius = StaticDataHolder.radiusStr;

                Log.d(TAG, "=====================================");
                Log.d(TAG, "Values Coming in for LOCAL_THEATERS_OP Operation");
                Log.d(TAG, "latitude: " + latitude);
                Log.d(TAG, "longitude: " + longitude);
                Log.d(TAG, "radius: " + radius);

                List<Address> currAddresses = getAddresses(latitude, longitude, locationName);
                // get zip code using latitude and longitude from map
                if (currAddresses != null && currAddresses.size() > 0) {

                    postalCode = currAddresses.get(0).getPostalCode();

                }

                if (postalCode == null) {
                    return null;
                }

                // set new postal code statically
                if(StaticDataHolder.postalCode != postalCode)
                    StaticDataHolder.postalCode = postalCode;

            }
            // ---- Comes here during search view (user types location) or radius is changed by user
            else if(arg0.length == 2) {

                if(Character.isDigit(arg0[1].charAt(0)) && arg0[1].length() <= 2) {
                    radius = arg0[1];
                    StaticDataHolder.radiusStr = radius;

                    Log.d(TAG, "=====================================");
                    Log.d(TAG, "Values Coming in for LOCAL_THEATERS_OP Operation");
                    Log.d(TAG, "radius: " + radius);

                    selectedDate = StaticDataHolder.date;
                }
                else {
                    locationName = arg0[1];
                    // get previous state of radius if no changes has been made to it
                    // so it doesn't reset to the default radius which is 5
                    if(StaticDataHolder.radiusStr != null)
                        radius = StaticDataHolder.radiusStr;

                    Log.d(TAG, "=====================================");
                    Log.d(TAG, "Values Coming in for LOCAL_THEATERS_OP Operation");
                    Log.d(TAG, "location name: " + locationName);
                    Log.d(TAG, "radius: " + radius);

                    List<Address> currAddresses = getAddresses(latitude, longitude, locationName);
                    // get zip code using latitude and longitude from map
                    if (currAddresses != null && currAddresses.size() > 0) {

                        postalCode = currAddresses.get(0).getPostalCode();

                    }

                    if (postalCode == null) {
                        return null;
                    }

                    // set new postal code statically
                    if(StaticDataHolder.postalCode != postalCode)
                        StaticDataHolder.postalCode = postalCode;

                    selectedDate = StaticDataHolder.date;
                }
            }
            else {
                return null;
            }

            Log.d(TAG, "=====================================");
            Log.d(TAG, "Values Coming in for THEATER_SHOWING_OP Operation");
            Log.d(TAG, "selected date: " + selectedDate);
            // check if movie information exists for that theater.
            Map<String, Map<String, MovieVO>> movieCache = StaticDataHolder.movieCache;

            if (movieCache != null && movieCache.containsKey(StaticDataHolder.postalCode) && selectedDate == null && radius == null) {
                Log.d(TAG, "Getting map from Cache");
                movieMap = movieCache.get(StaticDataHolder.postalCode);
            } else {
                // ------get movie information for that specific theater and new date if the user changed the date
                //callTheaterAPI(String operation, String postalCode, String count, String movieName, String radius, String selectedDate)
                JSONArray theaterShowingJsonArray = (JSONArray) executeUrl.callTheaterAPI(operation, StaticDataHolder.postalCode, null, null, StaticDataHolder.radiusStr, selectedDate);
                movieMap = data.populateMovieInfo(theaterShowingJsonArray);
            }

            if (movieMap != null && movieMap.size() > 0) {

                Map<String, RatingVO> ratingCache = StaticDataHolder.ratingCache;

                String count = String.valueOf(movieMap.size());

                if(Integer.parseInt(count) > 50) {
                    count = "50";
                }

                // ------check if images for movies exists for that theater id
                Map<String, String> imageCache = StaticDataHolder.imageMapCache;

                if(ratingCache.size() > 0) {
                    Log.d(TAG, "Rating Cache has Data Already");
                    for (MovieVO movieVO : movieMap.values()) {
                        if (ratingCache.get(movieVO.getTitle()) == null) {
                            Log.d(TAG, "This movie is not in rating map: " + movieVO.getTitle());
                            JSONObject tomatoJsonObject = (JSONObject) executeUrl.callTheaterAPI(TOMATOES_ONE_MOVIE_OP, null, null, movieVO.getTitle(), null, selectedDate);
                            Map<String, RatingVO> oneMovieRatingMap  = data.populateRatingInfo(tomatoJsonObject);

                            if ((oneMovieRatingMap != null && oneMovieRatingMap.size() > 0) && oneMovieRatingMap.get(movieVO.getTitle()) != null) {
                                RatingVO ratingVO = oneMovieRatingMap.get(movieVO.getTitle());
                                ratingCache.put(movieVO.getTitle(), ratingVO);
                                if(ratingVO != null && ratingVO.getThumbnail() != null)
                                    imageCache.put(ratingVO.getTitle(), ratingVO.getThumbnail());
                            }
                        }
                    }
                }
                else {
                    JSONObject tomatoJsonObject = (JSONObject) executeUrl.callTheaterAPI(TOMATOES_ALL_MOVIES_OP, null, count, null, null, selectedDate);
                    ratingsMap = data.populateRatingInfo(tomatoJsonObject);

                    // check if there are any movies not in ratings map
                    if (ratingsMap != null && ratingsMap.size() > 0) {

                        for (MovieVO movieVO : movieMap.values()) {

                            if (ratingsMap.containsKey(movieVO.getTitle())) {
                                if ((!ratingCache.containsKey(movieVO.getTitle()))) {
                                    ratingCache.put(movieVO.getTitle(), ratingsMap.get(movieVO.getTitle()));
                                }
                            } else {
                                Log.d(TAG, "This movie is not in rating map: " + movieVO.getTitle());
                                tomatoJsonObject = (JSONObject) executeUrl.callTheaterAPI(TOMATOES_ONE_MOVIE_OP, null, null, movieVO.getTitle(), null, selectedDate);
                                Map<String, RatingVO> oneMovieRatingMap = data.populateRatingInfo(tomatoJsonObject);

                                if ((oneMovieRatingMap != null && oneMovieRatingMap.size() > 0) && oneMovieRatingMap.get(movieVO.getTitle()) != null) {
                                    ratingsMap.put(movieVO.getTitle(), oneMovieRatingMap.get(movieVO.getTitle()));
                                    ratingCache.put(movieVO.getTitle(), oneMovieRatingMap.get(movieVO.getTitle()));
                                }
                            }
                        }
                    }

                    if (ratingsMap != null && ratingsMap.size() > 0) {

                        for (RatingVO ratingVO : ratingsMap.values()) {

                            if (imageCache != null && imageCache.size() > 0 && ratingVO != null && imageCache.containsKey(ratingVO.getTitle())) {
                                imageMap.put(ratingVO.getTitle(), imageCache.get(ratingVO.getTitle()));
                            } else if (ratingVO != null && ratingVO.getThumbnail() != null) {
                                imageCache.put(ratingVO.getTitle(), ratingVO.getThumbnail());
                            }
                        }
                    }
                }
            }
        }

        return null;
    }


    /**
     * Async Task has finished, return to the view
     *
     * @param result from task
     */
    @Override
    protected void onPostExecute(String result) {

        Log.d(TAG, "Theater Async Task Finished!");
       // Dialog.dismiss();
        // send results from async task to activity
        if (delegate != null) {
            delegate.processFinished(movieMap, ratingsMap, imageMap);
        }
         else {
            Log.d(TAG, "No Data Returned!");
        }
    }

}
